package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.packets.ChannelHudPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class ChanneledSpell extends Spell {
    protected final int castTime;
    protected final int intervalTicks;
    protected final int waitForFirst;

    protected final boolean runsOnlyOnce;
    protected final boolean showParticles;
    protected final ParticleEffect particleType;
    protected final ParticleAnimation animation;

    protected final DisturbConfig disturbConfig;

    @FunctionalInterface
    public interface DisturbFunction {
        int apply(ChanneledSpell spell, GrabBag args);
    }

    public enum ParticleAnimation {
        SPIRAL,
        CYLINDER,
        AURA,
        SPHERE,
        BURST
    }

    public enum DistributionLevels {
        NONE,
        DAMAGE,
        CROUCH,
        MOVE,
        CLICK,
        ROTATION,
        TARGET_LOST,
        IN_AIR,
        MOVE_AND_DAMAGE,
        MOVE_AND_CROUCH,
        DAMAGE_AND_CROUCH,
        DAMAGE_AND_CLICK,
        CROUCH_AND_CLICK,
        MOVE_AND_CLICK,
        DAMAGE_CROUCH_AND_MOVE,
        MOVE_DAMAGE_AND_CLICK,
        MOVE_CROUCH_AND_CLICK,
        DAMAGE_CROUCH_AND_CLICK,
        DAMAGE_CROUCH_MOVE_AND_CLICK,
        ALL
    }

    public enum InterruptReason {
        CLICK(true),
        CROUCHED(true),

        DAMAGE_TAKEN(false),
        MOVED_TOO_FAR(false),
        ROTATED_TOO_FAST(false),
        TARGET_LOST(false),
        LEFT_GROUND(false),
        DISSPELLED(false);

        private final boolean purposeful;

        InterruptReason(boolean purposeful) {
            this.purposeful = purposeful;
        }

        public boolean isPurposeful() {
            return purposeful;
        }
    }

    public record DisturbConfig(
            DistributionLevels distributionLevel,
            DisturbFunction globalDisturbableTill,
            double moveableDistance,
            DisturbFunction hold,
            float maxAngleChange,
            Map<InterruptReason, DisturbFunction> perConditionWindows
    ) {
        public DisturbConfig(DistributionLevels level) {
            this(level, (spell, args) -> spell.getCastTime(args), 0.5D, (spell, args) -> 1, 45.0f, new EnumMap<>(InterruptReason.class));
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private DistributionLevels level = DistributionLevels.NONE;
            private DisturbFunction globalDisturbableTill = (spell, args) -> spell.getCastTime(args);
            private double moveableDistance = 0.5D;
            private DisturbFunction hold = (spell, args) -> 1;
            private float maxAngleChange = 45.0f;
            private final Map<InterruptReason, DisturbFunction> customWindows = new EnumMap<>(InterruptReason.class);

            public Builder level(DistributionLevels level) { this.level = level; return this; }

            public Builder disturbableTill(int ticks) { this.globalDisturbableTill = (spell, args) -> ticks; return this; }
            public Builder disturbableTill(DisturbFunction fn) { this.globalDisturbableTill = fn; return this; }

            public Builder moveableDistance(double distance) { this.moveableDistance = distance; return this; }

            public Builder hold(int holdTicks) { this.hold = (spell, args) -> holdTicks; return this; }
            public Builder hold(DisturbFunction fn) { this.hold = fn; return this; }

            public Builder maxAngleChange(float maxAngle) { this.maxAngleChange = maxAngle; return this; }

            public Builder setWindow(InterruptReason reason, int tillTick) {
                this.customWindows.put(reason, (spell, args) -> tillTick);
                return this;
            }

            public Builder setWindow(InterruptReason reason, DisturbFunction windowFunction) {
                this.customWindows.put(reason, windowFunction);
                return this;
            }

            public DisturbConfig build() {
                return new DisturbConfig(level, globalDisturbableTill, moveableDistance, hold, maxAngleChange, customWindows);
            }
        }
    }

    public record Start(float health, Vec3d pos, float yaw, float pitch, Entity target) {}

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, DisturbConfig disturbConfig,
                          boolean showParticles, ParticleEffect particleType, ParticleAnimation animation,
                          int waitForFirst, boolean runsOnlyOnce) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.disturbConfig = disturbConfig != null ? disturbConfig : new DisturbConfig(DistributionLevels.NONE);
        this.showParticles = showParticles;
        this.particleType = particleType != null ? particleType : ParticleTypes.ENCHANTED_HIT;
        this.animation = animation != null ? animation : ParticleAnimation.CYLINDER;
        this.waitForFirst = Math.max(0, waitForFirst);
        this.runsOnlyOnce = runsOnlyOnce;
    }

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, DisturbConfig disturbConfig,
                          boolean showParticles, ParticleEffect particleType, ParticleAnimation animation, int waitForFirst) {
        this(type, castTime, intervalTicks, disturbConfig, showParticles, particleType, animation, waitForFirst, false);
    }

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, DisturbConfig disturbConfig,
                          boolean showParticles, ParticleEffect particleType, ParticleAnimation animation) {
        this(type, castTime, intervalTicks, disturbConfig, showParticles, particleType, animation, 0, false);
    }

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, DisturbConfig disturbConfig) {
        this(type, castTime, intervalTicks, disturbConfig, false, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.CYLINDER, 0, false);
    }

    @Override
    public abstract int getColor(GrabBag args);

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        LivingEntity user = ctx.user();
        if (user == null) return;

        Entity initialTarget = ctx.target();
        Start start = new Start(user.getHealth(), user.getPos(), user.getYaw(), user.getPitch(), initialTarget);

        int effectiveCastTime = getCastTime(args);
        int effectiveIntervalTicks = getIntervalTicks(args);

        double moveableDistance = getMoveableDistance(args);
        double moveableDistanceSq = moveableDistance * moveableDistance;
        float maxAngleChange = getMaxAngleChange(args);
        int color = getColor(args);
        int requiredHold = getHoldTicks(args);

        class ChannelState {
            boolean ranOnce = false;
            boolean cancelled = false;
            int crouchHoldTicks = 0;
        }
        final ChannelState state = new ChannelState();

        onChannelStart(ctx, args, start);
        LivingEntity caster = ctx.user();
        ItemStack mainHandStack = caster.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHandStack = caster.getStackInHand(Hand.OFF_HAND);

        ItemStack sourceStack = (!mainHandStack.isEmpty() && mainHandStack.getItem() instanceof SpellItem)
                ? mainHandStack
                : (!offHandStack.isEmpty() && offHandStack.getItem() instanceof SpellItem ? offHandStack : null);

        ((Ticker.TickerTarget) user).bTC$add(
                Ticker.forTicks(tick -> {
                    if (user.isSneaking()) {
                        state.crouchHoldTicks++;
                    } else {
                        state.crouchHoldTicks = 0;
                    }

                    InterruptReason reason = evaluateInterrupt(user, ctx, args, start, tick, moveableDistanceSq, maxAngleChange, state.crouchHoldTicks);

                    if (user instanceof ServerPlayerEntity serverPlayer) {
                        int cancelMask = getActivePurposefulCancelMask(tick, args);

                        boolean isClickActive = (cancelMask & (1 << InterruptReason.CLICK.ordinal())) != 0;
                        boolean isCrouchActive = (cancelMask & (1 << InterruptReason.CROUCHED.ordinal())) != 0;

                        int activeHoldProgress = 0;
                        if (isClickActive || isCrouchActive) {
                            int currentClickHold = isClickActive && serverPlayer.isUsingItem() ? serverPlayer.getItemUseTime() : 0;
                            int currentCrouchHold = isCrouchActive ? state.crouchHoldTicks : 0;

                            activeHoldProgress = Math.max(currentClickHold, currentCrouchHold);
                        }

                        if (sourceStack != null) {
                            String sourceItemId = net.minecraft.registry.Registries.ITEM.getId(sourceStack.getItem()).toString();

                            int spellIndex = 0;

                            if (sourceStack.getItem() instanceof MinimalPredefinedSpellsItem minimalItem) {
                                List<InstancedSpell> availableSpells = minimalItem.getAvailableSpells(sourceStack, ctx.world(), caster);

                                for (int i = 0; i < availableSpells.size(); i++) {
                                    if (availableSpells.get(i).spell().equals(this)) {
                                        spellIndex = i;
                                        break;
                                    }
                                }
                            }

                            ServerPlayNetworking.send(serverPlayer, new ChannelHudPayload(
                                    true,
                                    this.getTranslationKey(),
                                    tick,
                                    effectiveCastTime,
                                    cancelMask,
                                    color,
                                    activeHoldProgress,
                                    requiredHold,
                                    sourceItemId,
                                    spellIndex
                            ));
                        }
                    }

                    if (reason != null && canInterrupt(ctx, reason, tick)) {
                        state.cancelled = true;

                        if (reason.isPurposeful()) {
                            onPurposefulCancel(ctx, args, tick, reason);
                        } else {
                            onChannelInterrupt(ctx, args, tick, reason);
                        }

                        return true;
                    }

                    if (tick >= waitForFirst && tick % effectiveIntervalTicks == 0) {
                        if (!runsOnlyOnce || !state.ranOnce) {
                            state.ranOnce = true;
                            useChanneled(ctx, args, tick, start);
                        }
                    }

                    if (showParticles) {
                        renderParticles(user, tick, effectiveCastTime, args);
                    }

                    if (tick >= effectiveCastTime) {
                        if (!state.cancelled) {
                            runEnd(ctx, args, tick);
                        }
                        return true;
                    }

                    return isDisspelled(ctx, tick, user);
                }, effectiveCastTime + 1)
        );
    }

    InterruptReason evaluateInterrupt(LivingEntity user, SpellContext ctx, GrabBag args, Start start, int currentTick, double moveableDistanceSq, float maxAngleChange, int crouchHoldTicks) {
        DistributionLevels level = disturbConfig.distributionLevel();
        if (level == DistributionLevels.NONE) return null;

        int requiredHold = getHoldTicks(args);

        if (isConditionActive(InterruptReason.CLICK, currentTick, args) && isFlagEnabled(level, DistributionLevels.CLICK)) {
            if (hasUserHeldLongEnough(user, args)) return InterruptReason.CLICK;
        }

        if (isConditionActive(InterruptReason.CROUCHED, currentTick, args) && isFlagEnabled(level, DistributionLevels.CROUCH)) {
            if (crouchHoldTicks >= requiredHold) return InterruptReason.CROUCHED;
        }

        if (isConditionActive(InterruptReason.DAMAGE_TAKEN, currentTick, args) && isFlagEnabled(level, DistributionLevels.DAMAGE)) {
            if (user.getHealth() < start.health()) return InterruptReason.DAMAGE_TAKEN;
        }

        if (isConditionActive(InterruptReason.MOVED_TOO_FAR, currentTick, args) && isFlagEnabled(level, DistributionLevels.MOVE)) {
            if (user.getPos().squaredDistanceTo(start.pos()) > moveableDistanceSq) return InterruptReason.MOVED_TOO_FAR;
        }

        if (isConditionActive(InterruptReason.LEFT_GROUND, currentTick, args) && isFlagEnabled(level, DistributionLevels.IN_AIR)) {
            if (!user.isOnGround()) return InterruptReason.LEFT_GROUND;
        }

        if (isConditionActive(InterruptReason.ROTATED_TOO_FAST, currentTick, args) && isFlagEnabled(level, DistributionLevels.ROTATION)) {
            float yawDiff = Math.abs(MathHelper.wrapDegrees(user.getYaw() - start.yaw()));
            float pitchDiff = Math.abs(user.getPitch() - start.pitch());
            if (yawDiff > maxAngleChange || pitchDiff > maxAngleChange) {
                return InterruptReason.ROTATED_TOO_FAST;
            }
        }

        if (isConditionActive(InterruptReason.TARGET_LOST, currentTick, args) && isFlagEnabled(level, DistributionLevels.TARGET_LOST)) {
            Entity currentTarget = ctx.target();
            if (currentTarget == null || !currentTarget.isAlive() || user.squaredDistanceTo(currentTarget) > 144.0D) {
                return InterruptReason.TARGET_LOST;
            }
        }

        return null;
    }

    protected int getActivePurposefulCancelMask(int currentTick, GrabBag args) {
        DistributionLevels level = disturbConfig.distributionLevel();

        if (level == DistributionLevels.NONE) return 0;

        int mask = 0;

        if (isFlagEnabled(level, DistributionLevels.CROUCH) && isConditionActive(InterruptReason.CROUCHED, currentTick, args)) {
            mask |= (1 << InterruptReason.CROUCHED.ordinal());
        }

        if (isFlagEnabled(level, DistributionLevels.CLICK) && isConditionActive(InterruptReason.CLICK, currentTick, args)) {
            mask |= (1 << InterruptReason.CLICK.ordinal());
        }

        return mask;
    }

    public int getHoldTicks(GrabBag args) {
        return disturbConfig.hold().apply(this, args);
    }

    public int getGlobalDisturbableTillTicks(GrabBag args) {
        int defaultDisturbable = disturbConfig.globalDisturbableTill().apply(this, args);
        return args.getInt("disturbableTill", defaultDisturbable);
    }

    public int getInterruptWindowTicks(InterruptReason reason, GrabBag args) {
        if (disturbConfig.perConditionWindows().containsKey(reason)) {
            return disturbConfig.perConditionWindows().get(reason).apply(this, args);
        }

        return args.getInt(reason.name().toLowerCase() + "_till", getGlobalDisturbableTillTicks(args));
    }

    public double getMoveableDistance(GrabBag args) {
        return args.getDouble("moveableDistance", Math.max(0, disturbConfig.moveableDistance()));
    }

    public float getMaxAngleChange(GrabBag args) {
        return (float) args.getDouble("maxAngleChange", disturbConfig.maxAngleChange());
    }

    protected boolean isConditionActive(InterruptReason reason, int currentTick, GrabBag args) {
        return currentTick <= getInterruptWindowTicks(reason, args);
    }

    public boolean hasUserHeldLongEnough(LivingEntity user, GrabBag args) {
        if (!(user instanceof PlayerEntity player)) return false;

        int requiredHold = getHoldTicks(args);
        if (!player.isUsingItem()) {
            return false;
        }

        return player.getItemUseTime() >= requiredHold;
    }

    protected void onPurposefulCancel(SpellContext ctx, GrabBag args, int tick, InterruptReason reason) {
        resetCooldown(ctx);
        onChannelInterrupt(ctx, args, tick, reason);
    }

    protected void resetCooldown(SpellContext ctx) {
        if (disturbConfig.globalDisturbableTill().apply(this, ctx.data().getArgs()) < ctx.data().getArgs().getInt("castTime", this.castTime)) {
            ctx.data().setCooldown(new SpellCooldown(0, this.getCooldown(ctx.data().getArgs(), ctx.user()).key()));
        }
    }

    protected boolean canInterrupt(SpellContext ctx, InterruptReason reason, int currentTick) {
        if (reason.isPurposeful()) {
            return currentTick >= 0;
        }
        return currentTick > 0;
    }

    protected void onChannelStart(final SpellContext ctx, final GrabBag args, final Start start) {}

    protected abstract void useChanneled(final SpellContext ctx, final GrabBag args, final int tick, final Start start);

    protected void onChannelInterrupt(final SpellContext ctx, final GrabBag args, final int tick, final InterruptReason reason) {
        if (ctx.user() instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new ChannelHudPayload(
                    false, "", 0, 1, 0, 0, 0, 1, "", -1
            ));
        }
    }

    protected void runEnd(final SpellContext ctx, final GrabBag args, final int tick) {
        if (ctx.user() instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new ChannelHudPayload(
                    false, "", 0, 1, 0, 0, 0, 1, "", -1
            ));
        }
    }

    public int getCastTime(GrabBag args) {
        return Math.max(1, args.getInt("castTime", this.castTime));
    }

    public int getIntervalTicks(GrabBag args) {
        return Math.max(1, args.getInt("intervalTicks", this.intervalTicks));
    }

    public float getChannelProgress(int currentTick, GrabBag args) {
        return MathHelper.clamp((float) currentTick / getCastTime(args), 0.0f, 1.0f);
    }

    public int getChannelRemainingTicks(int currentTick, GrabBag args) {
        return Math.max(0, getCastTime(args) - currentTick);
    }

    public boolean isFinalTick(int currentTick, GrabBag args) {
        return currentTick >= getCastTime(args);
    }

    private boolean isFlagEnabled(DistributionLevels level, DistributionLevels flag) {
        if (level == DistributionLevels.ALL) return true;
        if (level == DistributionLevels.NONE) return false;

        return switch (flag) {
            case DAMAGE -> switch (level) {
                case DAMAGE, MOVE_AND_DAMAGE, DAMAGE_AND_CROUCH, DAMAGE_AND_CLICK,
                     DAMAGE_CROUCH_AND_MOVE, MOVE_DAMAGE_AND_CLICK, DAMAGE_CROUCH_AND_CLICK,
                     DAMAGE_CROUCH_MOVE_AND_CLICK -> true;
                default -> false;
            };
            case CROUCH -> switch (level) {
                case CROUCH, MOVE_AND_CROUCH, DAMAGE_AND_CROUCH, CROUCH_AND_CLICK,
                     DAMAGE_CROUCH_AND_MOVE, MOVE_CROUCH_AND_CLICK, DAMAGE_CROUCH_AND_CLICK,
                     DAMAGE_CROUCH_MOVE_AND_CLICK -> true;
                default -> false;
            };
            case MOVE -> switch (level) {
                case MOVE, MOVE_AND_DAMAGE, MOVE_AND_CROUCH, MOVE_AND_CLICK,
                     DAMAGE_CROUCH_AND_MOVE, MOVE_DAMAGE_AND_CLICK, MOVE_CROUCH_AND_CLICK,
                     DAMAGE_CROUCH_MOVE_AND_CLICK -> true;
                default -> false;
            };
            case CLICK -> switch (level) {
                case CLICK, DAMAGE_AND_CLICK, CROUCH_AND_CLICK, MOVE_AND_CLICK,
                     MOVE_DAMAGE_AND_CLICK, MOVE_CROUCH_AND_CLICK, DAMAGE_CROUCH_AND_CLICK,
                     DAMAGE_CROUCH_MOVE_AND_CLICK -> true;
                default -> false;
            };
            case ROTATION -> level == DistributionLevels.ROTATION;
            case TARGET_LOST -> level == DistributionLevels.TARGET_LOST;
            case IN_AIR -> level == DistributionLevels.IN_AIR;
            default -> level == flag;
        };
    }

    private void renderParticles(LivingEntity user, int tick, int totalDuration, GrabBag args) {
        switch (animation) {
            case CYLINDER -> spawnChannelParticlesCylinder(user, tick, totalDuration, args);
            case SPIRAL -> spawnChannelParticlesSpiral(user, tick, totalDuration, args);
            case AURA -> spawnChannelParticlesAura(user, tick, totalDuration, args);
            case SPHERE -> spawnChannelParticlesSphere(user, tick, totalDuration, args);
            case BURST -> spawnChannelParticlesBurst(user, tick, totalDuration, args);
        }
    }

    protected final void spawnChannelParticlesCylinder(LivingEntity entity, int tick, int totalDuration, GrabBag args) {
        World world = entity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        double height = entity.getHeight();
        double radius = entity.getWidth() * 0.75;
        int steps = args.getInt("particleCount", 20);

        double progress = (double) tick / totalDuration;
        double yOffset = progress * height;
        double angle = (tick * 0.3) % (Math.PI * 2);

        ParticleEffect effect = getResolvedParticleEffect(args, 1.0f);

        for (int i = 0; i < steps; i++) {
            double theta = angle + (2 * Math.PI * i / steps);
            double x = entity.getX() + Math.cos(theta) * radius;
            double y = entity.getY() + yOffset;
            double z = entity.getZ() + Math.sin(theta) * radius;

            serverWorld.spawnParticles(effect, x, y + 0.1, z, 1, 0, 0, 0, 0);
        }
    }

    protected final void spawnChannelParticlesSpiral(LivingEntity entity, int tick, int totalDuration, GrabBag args) {
        World world = entity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        double height = entity.getHeight();
        double radius = entity.getWidth() * 0.75;

        int pointsPerRotation = args.getInt("pointsPerRotation", 20);
        int rotations = args.getInt("spiralRotations", 3);
        int visibleRings = args.getInt("maxVisibleRings", 10);

        int totalSteps = pointsPerRotation * rotations;
        double progress = (double) tick / totalDuration;
        double yBase = entity.getY();

        int startRing = Math.max(0, (int)(progress * totalSteps) - visibleRings);
        int endRing = Math.min(totalSteps, (int)(progress * totalSteps));

        double heightStep = height / totalSteps;
        double angleStep = (2 * Math.PI * rotations) / totalSteps;

        for (int i = startRing; i < endRing; i++) {
            float fade = (float) MathHelper.clamp(1.0 - ((double)(endRing - i) / visibleRings), 0.0, 1.0);

            double theta = i * angleStep + (tick * 0.25);
            double y = yBase + (i * heightStep);
            double x = entity.getX() + Math.cos(theta) * radius;
            double z = entity.getZ() + Math.sin(theta) * radius;

            ParticleEffect effect = getResolvedParticleEffect(args, fade);
            serverWorld.spawnParticles(effect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    protected final void spawnChannelParticlesAura(LivingEntity entity, int tick, int totalDuration, GrabBag args) {
        World world = entity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        double radius = entity.getWidth() * 1.2;
        int particleCount = args.getInt("auraCount", 6);

        ParticleEffect effect = getResolvedParticleEffect(args, 1.0f);

        for (int i = 0; i < particleCount; i++) {
            double angle = world.getRandom().nextDouble() * Math.PI * 2;
            double x = entity.getX() + Math.cos(angle) * radius;
            double y = entity.getY() + (world.getRandom().nextDouble() * entity.getHeight());
            double z = entity.getZ() + Math.sin(angle) * radius;

            serverWorld.spawnParticles(effect, x, y, z, 1, 0, 0.02, 0, 0.01);
        }
    }

    protected final void spawnChannelParticlesSphere(LivingEntity entity, int tick, int totalDuration, GrabBag args) {
        World world = entity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        double radius = args.getDouble("sphereRadius", 1.5D);
        int count = args.getInt("sphereCount", 12);
        Vec3d center = entity.getPos().add(0, entity.getHeight() / 2.0, 0);

        ParticleEffect effect = getResolvedParticleEffect(args, 1.0f);

        for (int i = 0; i < count; i++) {
            double u = world.getRandom().nextDouble();
            double v = world.getRandom().nextDouble();
            double theta = u * 2.0 * Math.PI;
            double phi = Math.acos(2.0 * v - 1.0);

            double x = center.x + (radius * Math.sin(phi) * Math.cos(theta));
            double y = center.y + (radius * Math.sin(phi) * Math.sin(theta));
            double z = center.z + (radius * Math.cos(phi));

            serverWorld.spawnParticles(effect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    protected final void spawnChannelParticlesBurst(LivingEntity entity, int tick, int totalDuration, GrabBag args) {
        World world = entity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        ParticleEffect effect = getResolvedParticleEffect(args, 1.0f);
        serverWorld.spawnParticles(effect, entity.getX(), entity.getBodyY(0.5), entity.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
    }

    private ParticleEffect getResolvedParticleEffect(GrabBag args, float alphaMultiplier) {
        if (particleType != null) {
            return particleType;
        }

        int color = getColor(args);
        float r = (((color >> 16) & 0xFF) / 255.0F) * alphaMultiplier;
        float g = (((color >> 8) & 0xFF) / 255.0F) * alphaMultiplier;
        float b = ((color & 0xFF) / 255.0F) * alphaMultiplier;

        return EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b);
    }

    @Override
    protected boolean canUse(SpellContext ctx, GrabBag args) {
        return ctx.user() instanceof LivingEntity && super.canUse(ctx, args);
    }
}