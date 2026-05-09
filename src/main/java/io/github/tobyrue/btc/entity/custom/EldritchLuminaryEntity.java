package io.github.tobyrue.btc.entity.custom;


import io.github.tobyrue.btc.entity.ai.EldritchLuminaryStrafeGoal;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellDataStore;
import io.github.tobyrue.btc.spell.SpellHost;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

public class EldritchLuminaryEntity extends HostileEntity implements Angerable, SpellHost<LivingEntity> {
    private static final TrackedData<NbtCompound> SPELL_COOLDOWN = DataTracker.registerData(
            EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND
    );
    private static final TrackedData<String> CURRENT_SPELL = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<NbtCompound> SPELL_ARGS = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<NbtCompound> SPELLS = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<Integer> SPELL_CAST_TIME = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ILLUSION_TIME = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> GLOBAL_CAST_DELAY =
            DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int GLOBAL_DELAY = 40;

    private Spell.InstancedSpell activeCastingSpell = null;

    private LivingEntity target;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private final int castTime = 15;
    private final int illusionTime = 600;
    private final Vec3d[][] mirrorCopyOffsets;

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1D));
        this.goalSelector.add(6, new TemptGoal(this, 1.3D, Ingredient.ofItems(ModItems.STAFF, ModItems.DRAGON_STAFF, ModItems.FIRE_STAFF, ModItems.WIND_STAFF, ModItems.RUBY_TRIAL_KEY), false));
        this.goalSelector.add(1, new EldritchLuminaryStrafeGoal(this, 0.8, 16.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, (entity) -> {
            return Math.abs(entity.getY() - this.getY()) <= 4.0;
        }));
        this.targetSelector.add(0, new TrackTargetGoal(this, true, false) {
            @Override
            public boolean canStart() {
                return true;
            }
        });
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SPELL_COOLDOWN, new NbtCompound());
        builder.add(CURRENT_SPELL, "empty");
        builder.add(SPELL_ARGS, new NbtCompound());
        builder.add(SPELLS, new NbtCompound());
        builder.add(SPELL_CAST_TIME, 0);
        builder.add(ILLUSION_TIME, 0);
        builder.add(GLOBAL_CAST_DELAY, 0);
    }

    public static DefaultAttributeContainer.Builder createEldritchLuminaryAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ARMOR, 24f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,2f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.5)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.75f);
    }


    public EldritchLuminaryEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 20;
        this.mirrorCopyOffsets = new Vec3d[2][4];
        for (int i = 0; i < 4; ++i) {
            this.mirrorCopyOffsets[0][i] = Vec3d.ZERO;
            this.mirrorCopyOffsets[1][i] = Vec3d.ZERO;
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.INDIRECT_MAGIC)) {
            return false;
        }
        Entity attacker = source.getAttacker();
        if (attacker != null) {
            Vec3d bossLook = this.getRotationVec(1.0F).multiply(1, 0, 1).normalize();
            Vec3d toAttacker = attacker.getPos().subtract(this.getPos()).multiply(1, 0, 1).normalize();
            double dot = bossLook.dotProduct(toAttacker);

            if (dot < -0.5) {
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.HOSTILE, 1.5f, 0.5f);
                return super.damage(source, amount * 2.0f);
            }
        }
        if (activeCastingSpell != null) {
            var spell = activeCastingSpell.spell().getSpellType();

            if ((spell == SpellTypes.FIRE && source.isOf(DamageTypes.FREEZE)) ||
                    (spell == SpellTypes.WATER && source.isOf(DamageTypes.ON_FIRE))) {

                this.setGlobalCastDelay(100);
                this.activeCastingSpell = null;
                return super.damage(source, amount * 2.0f);
            }
        }

        return super.damage(source, amount);
    }

    private void setupAnimationStates() {
        SpellDataStore data = getSpellDataStore(this);

        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfNotRunning(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (getCastTime() > 0 && getCastTime() <= castTime && getGlobalCastDelay() <= 0) {
            this.attackAnimationTimeout = 20;
            this.attackAnimationState.startIfNotRunning(this.age);
        } else {
            if (this.attackAnimationTimeout > 0) {
                --this.attackAnimationTimeout;
            } else {
                this.attackAnimationState.stop();
            }
        }
    }


    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    private Vec3d[] illusionOffsets;

    public Vec3d[] getIllusionOffsets() {
        return illusionOffsets;
    }

    public void setIllusionOffsets(Vec3d[] illusionOffsets) {
        this.illusionOffsets = illusionOffsets;
    }

    public Vec3d[] getMirrorCopyOffsets(float tickDelta) {
        if (this.getIllusionTime() <= 0) {
            return this.mirrorCopyOffsets[1];
        }

        double d = ((float) this.getIllusionTime() - tickDelta) / 3.0f;
        d = Math.pow(d, 0.25);

        Vec3d[] vec3ds = new Vec3d[4];

        for (int i = 0; i < 4; ++i) {
            Vec3d interpolated = this.mirrorCopyOffsets[1][i]
                    .multiply(1.0 - d)
                    .add(this.mirrorCopyOffsets[0][i].multiply(d));

            vec3ds[i] = interpolated.multiply(d);
        }

        return vec3ds;
    }

    @Override
    protected Box getHitbox() {
        return super.getHitbox();
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity player) {
        if (getIllusionTime() > 0 && getIllusionTime() <= illusionTime) {
            return true;
        }
        return super.isInvisibleTo(player);
    }
    public void chorusTeleport() {
        double x = this.getX() + (this.random.nextDouble() - 0.5) * 16.0;
        double y = this.getY() + (double)(this.random.nextInt(16) - 8);
        double z = this.getZ() + (this.random.nextDouble() - 0.5) * 16.0;

        if (this.teleport(x, y, z, true)) {
            this.getWorld().emitGameEvent(GameEvent.TELEPORT, this.getPos(), GameEvent.Emitter.of(this));
            if (!this.isSilent()) {
                this.getWorld().playSound(null, this.prevX, this.prevY, this.prevZ,
                        SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
            }
        }
    }
    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            if (this.isInsideWall()) {

                this.chorusTeleport();

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.PORTAL,
                        this.getX(), this.getY() + 1, this.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1
                );
            }
        }
        if (this.getWorld().isClient()) {
            if (getIllusionTime() > 0 && getIllusionTime() <= illusionTime) {
                setIllusionTime(getIllusionTime() + 1);
            } else if (getIllusionTime() >= illusionTime) {
                setIllusionTime(0);
            }
        }


        SpellDataStore data = getSpellDataStore(this);
        Spell.InstancedSpell current = this.getCurrentSpellInstance();

        if (!this.getWorld().isClient() && this.getCurrentSpell() != null && this.getCurrentArgs() != null) {
//            System.out.println(
//                    "Delay=" + getGlobalCastDelay() +
//                            ", CastTime=" + getCastTime() +
//                            ", Active=" + (activeCastingSpell != null) +
//                            ", Target=" + (target != null) +
//                            ", Cooldowns=" + data.getCooldown(this.getCurrentSpell().getCooldown(getCurrentArgs(), this))
//            );
        }


        if (getGlobalCastDelay() > 0) {
            setGlobalCastDelay(getGlobalCastDelay() - 1);
        }

        if (this.target != null) {
            if (getGlobalCastDelay() <= 0) {

                if (activeCastingSpell == null && getCastTime() <= 0) {
                    activeCastingSpell = chooseRandomCurrentSpell();
                    setCastTime(1);
                } else if (activeCastingSpell != null && getCastTime() < castTime) {
                    setCastTime(getCastTime() + 1);
                } else if (activeCastingSpell != null && getCastTime() >= castTime) {
                    this.lookAtEntity(target, 90, 90);
                    castCurrentSpellAt(this.target);

                    activeCastingSpell = null;
                    setCastTime(0);
                    setSpellEmpty();
                }
            }
        } else {
            if (activeCastingSpell != null && getCastTime() >= castTime && (this.getHealth() / this.getMaxHealth()) * 100 <= 70) {
                activeCastingSpell = new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                    put("effect", "minecraft:regeneration");
                    put("duration", 150);
                    put("cooldown", 0);
                    put("amplifier", 4);
                }}));
                setCurrentSpellInstance(activeCastingSpell.spell(), activeCastingSpell.args());
                castCurrentSpellAt();

//                var cd = activeCastingSpell.spell().getCooldown(activeCastingSpell.args(), this);
//                getSpellDataStore(this).setCooldown(cd);

                activeCastingSpell = null;
                setCastTime(0);
                setSpellEmpty();
            }
        }


        if (this.getWorld().isClient() && current.spell() != ModSpells.EMPTY && current.spell() != null && !this.isInvisible() && getIllusionTime() <= 0 && getCastTime() > 0 && getCastTime() <= castTime && getGlobalCastDelay() <= 0) {
            int colorInt = current.spell().getColor(current.args());
            float r = ((colorInt >> 16) & 0xFF) / 255.0F;
            float g = ((colorInt >> 8) & 0xFF) / 255.0F;
            float b = (colorInt & 0xFF) / 255.0F;

            float i = this.bodyYaw * 0.017453292F + MathHelper.cos((float) this.age * 0.6662F) * 0.25F;
            float j = MathHelper.cos(i);
            float k = MathHelper.sin(i);
            double d = 0.6 * this.getScale();
            double e = 1.8 * this.getScale();

            this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                    this.getX() + j * d, this.getY() + e, this.getZ() + k * d, 0, 0, 0);
            this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                    this.getX() - j * d, this.getY() + e, this.getZ() - k * d, 0, 0, 0);
        }

        if (getAllSpellInstances().isEmpty()) {
            // Base elemental attacks
            this.addSpell(new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(1));
                put("level", 2);
                put("globalCooldown", 50);
            }})), 6, 32, -1, -1, -1, -1, 0.8f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.WATER_BLAST, GrabBag.fromMap(new HashMap<>() {{
                put("noGravity", true);
                put("cooldown", getSpellWaitAmount(1));
                put("globalCooldown", 50);
            }})), 6, 24, -1, -1, -1, -1, 0.8f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.EARTH_SPIKE_LINE, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(2));
                put("globalCooldown", 60);
            }})), 4, 16, -1, -1, -1, -1, 0.9f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(4));
                put("globalCooldown", 70);
            }})), 8, 24, -1, -1, -1, -1, 0.7f);

            // === AREA & MULTI-ATTACK ===
            // High impact, high recovery time.
            this.addSpell(new Spell.InstancedSpell(ModSpells.FIRE_STORM, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(5));
                put("globalCooldown", 60);
            }})), 0, 10, -1, -1, -1, -1, 1.0f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.CREEPER_WALL_CIRCLE, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(6));
                put("globalCooldown", 120);
            }})), 4, 12, -1, -1, -1, -1, 0.9f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.LOCALIZED_STORM_PUSH, GrabBag.fromMap(new HashMap<>() {{
                put("shootStrength", 2d);
                put("verticalMultiplier", 1.2d);
                put("cooldown", getSpellWaitAmount(1));
                put("globalCooldown", 40);
            }})), 4, 8, -1, -1, -1, -1, 1.2f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.LOCALIZED_STORM_PUSH, GrabBag.fromMap(new HashMap<>() {{
                put("shootStrength", 2d);
                put("verticalMultiplier", 1.2d);
                put("cooldown", getSpellWaitAmount(1));
                put("globalCooldown", 40);
            }})), 0, 4, -1, -1, -1, -1, 2f);
            // === SUMMONS & ILLUSIONS ===
            this.addSpell(new Spell.InstancedSpell(ModSpells.SHULKER_BULLET, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(3));
                put("globalCooldown", 40);
            }})), 10, 32, -1, -1, -1, -1, 0.6f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.ELDRITCH_ILLUSION, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(8));
                put("globalCooldown", 80);
            }})), 0, 32, -1, -1, -1, -1, 1.0f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.RAISE_UNDEAD, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(10));
                put("globalCooldown", 80);
            }})), 0, 32, 80, -1, -1, -1, 1.2f); // Use when below 60% health

            // === BUFFS & DEBUFFS ===
            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:invisibility");
                put("duration", 400);
                put("cooldown", getSpellWaitAmount(10));
                put("globalCooldown", 80);
            }})), 0, 48, 40, -1, -1, -1, 0.9f); // Panic invisibility at 40% health

            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:regeneration");
                put("duration", 150);
                put("amplifier", 3);
                put("cooldown", getSpellWaitAmount(16));
                put("globalCooldown", 80);
            }})), 0, 48, 60, -1, -1, -1, 2f); // Solid heal at 50%
            this.addSpell(new Spell.InstancedSpell(ModSpells.TRIGGERED_POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:regeneration");
                put("percentHealth", 0.6);
                put("activeTicks", 1200);
                put("duration", 100);
                put("amplifier", 2);
                put("cooldown", getSpellWaitAmount(32));
                put("globalCooldown", 80);
            }})), 0, 48, 80, -1, -1, -1, 2f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.TRIGGERED_POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:resistance");
                put("percentHealth", 0.4);
                put("activeTicks", 1200);
                put("duration", 400);
                put("amplifier", 1);
                put("cooldown", getSpellWaitAmount(32));
                put("globalCooldown", 80);
            }})), 0, 48, 80, -1, -1, -1, 2f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION_AREA_EFFECT, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:darkness");
                put("duration", 200);
                put("amplifier", 1);
                put("cooldown", getSpellWaitAmount(8));
                put("globalCooldown", 80);
            }})), 0, 15, -1, -1, -1, -1, 0.8f);

            // === MOVEMENT & TRICKS ===
            this.addSpell(new Spell.InstancedSpell(ModSpells.SHADOW_STEP, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(4));
                put("globalCooldown", 50);
            }})), 8, 32, -1, -1, -1, -1, 1.0f); // Use to escape when player is close

            this.addSpell(new Spell.InstancedSpell(ModSpells.WIND_TORNADO, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(5));
                put("globalCooldown", 70);
            }})), 0, 8, -1, -1, -1, -1, 1.1f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.MIST_VEIL, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(5));
                put("globalCooldown", 60);
            }})), 0, 12, -1, -1, -1, -1, 0.7f);

            // === ADVANCED MAGIC ===
            this.addSpell(new Spell.InstancedSpell(ModSpells.LIFE_STEAL, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(5));
                put("globalCooldown", 80);
            }})), 0, 15, 70, -1, -1, -1, 1.3f); // Prioritize when boss is < 70% health

            this.addSpell(new Spell.InstancedSpell(ModSpells.DRAGONS_BREATH, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(12));
                put("globalCooldown", 90);
            }})), 0, 8, -1, -1, -1, -1, 1.7f); // Brutal close-range punish
            this.addSpell(new Spell.InstancedSpell(ModSpells.FLAME_BURST, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(12));
                put("globalCooldown", 90);
            }})), 0, 8, -1, -1, -1, -1, 1.7f); // Brutal close-range punish

            this.addSpell(new Spell.InstancedSpell(ModSpells.LIGHTNING_STRIKE, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(6));
                put("globalCooldown", 30);
            }})), 4, 32, -1, -1, -1, -1, 1.1f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.FROST_REFLEX, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(32));
                put("activeTicks", getSpellWaitAmount(24));
                put("globalCooldown", 30);
            }})), 0, 32, -1, -1, 80, -1, 1.3f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.TELEPORT_FREEZE, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(32));
                put("activeTicks", getSpellWaitAmount(24));
                put("globalCooldown", 30);
            }})), 0, 32, -1, 70, 80, -1, 1.3f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.PURGE_BOLT, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(5));
            }})), 0, 64, -1, -1, -1, -1, 0f);
        }

        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }
        if (!this.getWorld().isClient()) {
            ((SpellHost<LivingEntity>) this).tickCooldowns(this);
        }
    }

    private void applyPyromancer() {
        addUniversalSpells();

        this.addSpell(new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(1));
            put("level", 2);
            put("globalCooldown", 40);
        }})), 6, 32, -1, -1, -1, -1, 1.0f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.FLAME_BURST, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(12));
            put("globalCooldown", 80);
        }})), 0, 10, -1, -1, -1, -1, 1.2f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.BLAZE_STORM, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(15));
            put("globalCooldown", 90);
        }})), 0, 12, -1, -1, -1, -1, 1.1f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.FIRE_STORM, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(25));
            put("globalCooldown", 120);
        }})), 0, 15, -1, -1, -1, -1, 1.5f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.DRAGONS_BREATH, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(12));
            put("globalCooldown", 60);
        }})), 0, 8, -1, -1, -1, -1, 2.0f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.DRAGON_FIREBALL, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(10));
            put("globalCooldown", 80);
        }})), 10, 32, -1, -1, -1, -1, 1.3f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.STORM_PUSH, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(5));
            put("globalCooldown", 40);
        }})), 0, 5, -1, -1, -1, -1, 1.4f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.GEYSER_STEP, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(6));
            put("globalCooldown", 50);
        }})), 0, 12, -1, -1, -1, -1, 1.0f);
    }

    private void applyStormWarden() {
        addUniversalSpells();

        this.addSpell(new Spell.InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(6));
            put("globalCooldown", 70);
        }})), 8, 24, -1, -1, -1, -1, 1.1f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.EARTH_SPIKE_LINE, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(4));
            put("globalCooldown", 60);
        }})), 4, 16, -1, -1, -1, -1, 1.0f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.WIND_TORNADO, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(8));
            put("globalCooldown", 80);
        }})), 0, 12, -1, -1, -1, -1, 1.2f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.LOCALIZED_STORM_PUSH, GrabBag.fromMap(new HashMap<>() {{
            put("shootStrength", 2.5d);
            put("cooldown", getSpellWaitAmount(3));
            put("globalCooldown", 40);
        }})), 0, 10, -1, -1, -1, -1, 1.5f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.LIGHTNING_STRIKE, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(10));
            put("globalCooldown", 50);
        }})), 0, 32, -1, -1, -1, -1, 1.3f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.WATER_BLAST, GrabBag.fromMap(new HashMap<>() {{
            put("noGravity", true);
            put("cooldown", getSpellWaitAmount(5));
            put("globalCooldown", 50);
        }})), 6, 24, -1, -1, -1, -1, 1.0f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.TEMPESTS_CALL, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(30));
            put("globalCooldown", 130);
        }})), 0, 32, -1, -1, -1, -1, 1.6f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.FROST_REFLEX, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(32));
            put("activeTicks", getSpellWaitAmount(24));
            put("globalCooldown", 30);
        }})), 0, 32, -1, -1, 85, -1, 2.0f);
    }

    private void applyShadowSummoner() {
        addUniversalSpells();

        this.addSpell(new Spell.InstancedSpell(ModSpells.RAISE_UNDEAD, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(20));
            put("globalCooldown", 100);
        }})), 0, 32, -1, -1, -1, -1, 1.5f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.SHULKER_BULLET, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(5));
            put("globalCooldown", 40);
        }})), 10, 32, -1, -1, -1, -1, 1.0f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.LIFE_STEAL, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(12));
            put("globalCooldown", 80);
        }})), 0, 15, 75, -1, -1, -1, 1.4f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.SHADOW_STEP, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(8));
            put("globalCooldown", 50);
        }})), 0, 12, -1, -1, -1, -1, 1.8f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.MIST_VEIL, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(15));
            put("globalCooldown", 90);
        }})), 0, 15, -1, -1, -1, -1, 1.1f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.ENDER_PEARL, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(10));
            put("globalCooldown", 60);
        }})), 12, 32, -1, -1, -1, -1, 0.9f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.CREEPER_WALL_CIRCLE, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(15));
            put("globalCooldown", 120);
        }})), 0, 12, -1, -1, -1, -1, 1.3f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.DISSOLUTION, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(40));
            put("globalCooldown", 160);
        }})), 0, 32, -1, -1, -1, -1, 1.7f);
    }

    private void addUniversalSpells() {
        this.addSpell(new Spell.InstancedSpell(ModSpells.ELDRITCH_ILLUSION, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(20));
            put("globalCooldown", 100);
        }})), 0, 32, -1, -1, -1, -1, 0.8f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.PURGE_BOLT, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(5));
        }})), 0, 64, -1, -1, -1, -1, 0f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.TRIGGERED_POTION, GrabBag.fromMap(new HashMap<>() {{
            put("effect", "minecraft:regeneration");
            put("percentHealth", 0.5);
            put("duration", 200);
            put("amplifier", 1);
            put("cooldown", getSpellWaitAmount(40));
            put("globalCooldown", 80);
        }})), 0, 48, 50, -1, -1, -1, 2.0f);

        this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
            put("effect", "minecraft:invisibility");
            put("duration", 200);
            put("cooldown", getSpellWaitAmount(30));
            put("globalCooldown", 80);
        }})), 0, 15, 35, -1, -1, -1, 1.5f);
    }

    private int getSpellWaitAmount(int amount) {
        return ((amount) * (GLOBAL_DELAY + castTime)) + 1;
    }

    private void castCurrentSpellAt(LivingEntity target) {
        SpellDataStore data = getSpellDataStore(this);
        Spell spell = data.getSpell();
        GrabBag args = data.getArgs();

        if (spell == null) return;

        setGlobalCastDelay(args.getInt("globalCooldown"));

        Vec3d origin = this.getPos().add(0, this.getStandingEyeHeight(), 0);
        Vec3d direction = target.getPos().add(0, target.getStandingEyeHeight() / 2, 0).subtract(origin).normalize();

        Spell.SpellContext ctx = new Spell.SpellContext(this.getWorld(), origin, direction, data, this);
        spell.tryUse(ctx, args);
    }
    private void castCurrentSpellAt() {
        SpellDataStore data = getSpellDataStore(this);
        Spell spell = data.getSpell();
        GrabBag args = data.getArgs();

        if (spell == null) return;

        setGlobalCastDelay(args.getInt("globalCooldown"));

        Vec3d origin = this.getPos().add(0, this.getStandingEyeHeight(), 0);

        Spell.SpellContext ctx = new Spell.SpellContext(this.getWorld(), origin, null, data, this);
        spell.tryUse(ctx, args);
    }

    public Spell.InstancedSpell getCurrentSpellInstance() {
        String spellIdString = this.dataTracker.get(CURRENT_SPELL);
        if (spellIdString == null || spellIdString.isEmpty()) {
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        Identifier id = Identifier.tryParse(spellIdString);
        Spell spell = ModRegistries.SPELL.get(id);

        NbtCompound argsNbt = this.dataTracker.get(SPELL_ARGS);
        GrabBag args = argsNbt != null ? GrabBag.fromNBT(argsNbt) : GrabBag.empty();

        return new Spell.InstancedSpell(spell, args);
    }

    public void setCurrentSpellInstance(Spell spell, @Nullable GrabBag args) {
        Identifier id = ModRegistries.SPELL.getId(spell);
        if (id == null) return;

        boolean exists = this.getAllSpellInstances().stream()
                .anyMatch(inst -> ModRegistries.SPELL.getId(inst.spell()).equals(id));
        this.getSpellDataStore(this).setSpell(spell, args);
        if (exists) {
            this.dataTracker.set(CURRENT_SPELL, id.toString());
            this.dataTracker.set(SPELL_ARGS, args != null ? GrabBag.toNBT(args) : new NbtCompound());
        }
    }

    public boolean canUseSpell(Spell.InstancedSpell spellInstance) {
        if (spellInstance == null || spellInstance.spell() == ModSpells.EMPTY) return false;

        SpellDataStore data = getSpellDataStore(this);
        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null) return false;

        Identifier id = ModRegistries.SPELL.getId(spellInstance.spell());
        if (id == null || !nbt.contains(id.toString())) return false;

        double distance = this.target != null ? this.distanceTo(this.target) : 0.0;
        double selfHealthPercent = (this.getHealth() / this.getMaxHealth()) * 100.0;
        double targetHealthPercent = (this.target != null && this.target.getMaxHealth() > 0)
                ? (this.target.getHealth() / this.target.getMaxHealth()) * 100.0
                : 100.0;

        boolean targetOnFire = (this.target != null && this.target.isOnFire());

        if (targetOnFire && (spellInstance.spell() == ModSpells.WATER_WAVE || spellInstance.spell() == ModSpells.WATER_BLAST))
            return false;
        if (this.target != null && this.target.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)
                && spellInstance.spell().getSpellType() == SpellTypes.FIRE)
            return false;

        NbtCompound spellData = nbt.getCompound(id.toString());

        double min = spellData.getDouble("minDistance");
        double max = spellData.getDouble("maxDistance");
        double belowLife = spellData.contains("belowLife") ? spellData.getDouble("belowLife") : -1;
        double aboveLife = spellData.contains("aboveLife") ? spellData.getDouble("aboveLife") : -1;
        double targetBelowLife = spellData.getDouble("targetBelowLife");
        double targetAboveLife = spellData.getDouble("targetAboveLife");

        if (belowLife == -1) belowLife = 100.0;
        if (aboveLife == -1) aboveLife = 0.0;
        if (targetBelowLife <= 0) targetBelowLife = 100.0;
        if (targetAboveLife <= 0) targetAboveLife = 0.0;

        boolean withinDistance = (max <= 0 || (distance >= min && distance <= max));
        boolean withinSelfHealth = (selfHealthPercent <= belowLife && selfHealthPercent >= aboveLife);
        boolean withinTargetHealth = (targetHealthPercent <= targetBelowLife && targetHealthPercent >= targetAboveLife);

        var cd = spellInstance.spell().getCooldown(spellInstance.args(), this);
        boolean cooldownReady = data.getCooldown(cd) <= 0;

        return withinDistance && withinSelfHealth && withinTargetHealth && cooldownReady;
    }

    public Spell.InstancedSpell chooseRandomCurrentSpell() {
        SpellDataStore data = getSpellDataStore(this);

        if (getGlobalCastDelay() > 0) {
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        List<Spell.InstancedSpell> spells = getAllSpellInstances();
        if (spells.isEmpty()) {
            setCurrentSpellInstance(ModSpells.EMPTY, GrabBag.empty());
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        double distance = this.getPos().distanceTo(target.getPos());
        double selfHealthPercent = (this.getHealth() / this.getMaxHealth()) * 100.0;
        double targetHealthPercent = (this.target != null && this.target.getMaxHealth() > 0)
                ? (this.target.getHealth() / this.target.getMaxHealth()) * 100.0
                : 100.0;

        NbtCompound nbt = this.dataTracker.get(SPELLS);
        List<Spell.InstancedSpell> validSpells = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        if (this.isOnFire() && !this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            Spell.InstancedSpell fallback = new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:fire_resistance");
                put("duration", 240);
                put("cooldown", 100);
            }}));
            setCurrentSpellInstance(fallback.spell(), fallback.args());
            return fallback;
        }

        boolean targetOnFire = (this.target != null && this.target.isOnFire());

        var pb = new Spell.InstancedSpell(ModSpells.PURGE_BOLT, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(5));
        }}));

        if (target.hasStatusEffect(StatusEffects.REGENERATION) || target.hasStatusEffect(StatusEffects.RESISTANCE) || target.hasStatusEffect(StatusEffects.ABSORPTION)) {
            if (canUseSpell(pb)) {
                setCurrentSpellInstance(pb.spell(), pb.args());
                return pb;
            }
        }
        for (Spell.InstancedSpell spellInstance : spells) {
            Identifier id = ModRegistries.SPELL.getId(spellInstance.spell());

            if (id == null || nbt == null || !nbt.contains(id.toString())) continue;

            if (targetOnFire && (spellInstance.spell() == ModSpells.WATER_WAVE && spellInstance.spell() == ModSpells.WATER_BLAST)) continue;
            if (target.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && spellInstance.spell().getSpellType() == SpellTypes.FIRE) {
                if (canUseSpell(pb)) {
                    setCurrentSpellInstance(pb.spell(), pb.args());
                    return pb;
                } else {
                    continue;
                }
            }
            NbtCompound spellData = nbt.getCompound(id.toString());
            double belowLife = spellData.contains("belowLife") ? spellData.getDouble("belowLife") : 100.0;
            double aboveLife = spellData.contains("aboveLife") ? spellData.getDouble("aboveLife") : 0.0;
            double targetBelowLife = spellData.contains("targetBelowLife") ? spellData.getDouble("targetBelowLife") : 100.0;
            double targetAboveLife = spellData.contains("targetAboveLife") ? spellData.getDouble("targetAboveLife") : 0.0;
            float weight = spellData.contains("weight") ? spellData.getFloat("weight") : 1.0f;
            double min = spellData.getDouble("minDistance");
            double max = spellData.getDouble("maxDistance");

            var cd = spellInstance.spell().getCooldown(spellInstance.args(), this);
            int currentCD = data.getCooldown(cd);

            boolean withinDistance = (max <= 0 || (distance >= min && distance <= max));
            boolean withinSelfHealth = (selfHealthPercent <= (belowLife <= 0 ? 100 : belowLife) && selfHealthPercent >= aboveLife);
            boolean withinTargetHealth = (targetHealthPercent <= (targetBelowLife <= 0 ? 100 : targetBelowLife) && targetHealthPercent >= targetAboveLife);
            boolean cdReady = currentCD <= 0;

            if (withinDistance && withinSelfHealth && withinTargetHealth && cdReady) {
                float finalWeight = weight;
                if (distance < 4.0f && max <= 8.0f) finalWeight *= 2.0f;

                validSpells.add(spellInstance);
                weights.add(finalWeight);
            }
        }

        if (validSpells.isEmpty()) {
            setCurrentSpellInstance(ModSpells.EMPTY, GrabBag.empty());
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        float totalWeight = 0.0f;
        for (float w : weights) totalWeight += w;

        float randomPoint = this.random.nextFloat() * totalWeight;

        for (int i = 0; i < validSpells.size(); i++) {
            randomPoint -= weights.get(i);
            if (randomPoint <= 0) {
                Spell.InstancedSpell chosen = validSpells.get(i);
                setCurrentSpellInstance(chosen.spell(), chosen.args());
                return chosen;
            }
        }

        return validSpells.getFirst();
    }
    public void setSpellEmpty() {
        Identifier id = ModRegistries.SPELL.getId(ModSpells.EMPTY);
        if (id != null) {
            this.dataTracker.set(CURRENT_SPELL, id.toString());
            this.dataTracker.set(SPELL_ARGS, new NbtCompound());
        }
    }

    public int getGlobalCastDelay() {
        return this.dataTracker.get(GLOBAL_CAST_DELAY);
    }


    public void setGlobalCastDelay(int ticks) {
        float healthPercent = this.getHealth() / this.getMaxHealth();
        int finalTicks = ticks;

        if (healthPercent < 0.6f) {
            finalTicks = Math.round(ticks * 0.65f);
        }

        this.dataTracker.set(GLOBAL_CAST_DELAY, finalTicks);
    }

    public int getCastTime() {
        return this.dataTracker.get(SPELL_CAST_TIME);
    }

    public void setCastTime(int time) {
        this.dataTracker.set(SPELL_CAST_TIME, time);
    }

    public int getIllusionTime() {
        return this.dataTracker.get(ILLUSION_TIME);
    }

    public void setIllusionTime(int time) {
        this.dataTracker.set(ILLUSION_TIME, time);
    }

    public Spell getCurrentSpell() {
        return getCurrentSpellInstance().spell();
    }

    public GrabBag getCurrentArgs() {
        return getCurrentSpellInstance().args();
    }

    public void setCurrentSpellNoArgs(Spell spell) {
        setCurrentSpellInstance(spell, GrabBag.empty());
    }


    public void addSpell(Spell.InstancedSpell instance, double minDistance, double maxDistance, double belowLife, double aboveLife, double targetBelowLife, double targetAboveLife, float weight) {
        if (instance == null || instance.spell() == null) return;

        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null) {
            nbt = new NbtCompound();
        } else {
            nbt = nbt.copy();
        }

        Identifier id = ModRegistries.SPELL.getId(instance.spell());
        if (id == null) return;

        NbtCompound spellData = new NbtCompound();
        spellData.put("args", GrabBag.toNBT(instance.args()));
        spellData.putDouble("minDistance", minDistance);
        spellData.putDouble("maxDistance", maxDistance);
        spellData.putDouble("belowLife", belowLife);
        spellData.putDouble("aboveLife", aboveLife);
        spellData.putDouble("targetBelowLife", targetBelowLife);
        spellData.putDouble("targetAboveLife", targetAboveLife);
        spellData.putFloat("weight", weight);

        nbt.put(id.toString(), spellData);

        this.dataTracker.set(SPELLS, nbt);
    }



    @Nullable
    public Spell.InstancedSpell getSpellInstance(int index) {
        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null || nbt.isEmpty()) return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());

        String spellId = nbt.getKeys().stream().skip(index).findFirst().orElse(null);
        if (spellId == null) return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());

        Spell spell = ModRegistries.SPELL.get(Identifier.tryParse(spellId));
        GrabBag args = GrabBag.fromNBT(nbt.getCompound(spellId));

        return new Spell.InstancedSpell(spell, args);
    }

    public List<Spell.InstancedSpell> getAllSpellInstances() {
        List<Spell.InstancedSpell> list = new ArrayList<>();
        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null || nbt.isEmpty()) return list;

        for (String key : nbt.getKeys()) {

            Spell spell = ModRegistries.SPELL.get(Identifier.tryParse(key));
            if (spell == null) continue;

            NbtCompound spellData = nbt.getCompound(key);
            GrabBag args;
            if (spellData.contains("args", NbtElement.COMPOUND_TYPE)) {
                args = GrabBag.fromNBT(spellData.getCompound("args"));
            } else {
                args = GrabBag.fromNBT(spellData);
            }

            list.add(new Spell.InstancedSpell(spell, args));
        }

        return list;
    }
    public void removeSpell(Spell.InstancedSpell instance) {
        if (instance == null || instance.spell() == null) return;

        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null) return;

        Identifier id = ModRegistries.SPELL.getId(instance.spell());
        if (id == null || !nbt.contains(id.toString())) return;

        nbt.remove(id.toString());
        this.dataTracker.set(SPELLS, nbt);
    }

    @Override
    public SpellDataStore getSpellDataStore(LivingEntity entity) {
        return new SpellDataStore() {
            @Override
            public Spell getSpell() {
                String spellName = dataTracker.get(CURRENT_SPELL);
                return ModRegistries.SPELL.get(Identifier.tryParse(spellName));
            }

            @Override
            public GrabBag getArgs() {
                NbtCompound argsNbt = dataTracker.get(SPELL_ARGS);

                if (argsNbt == null || argsNbt.isEmpty()) {
                    return GrabBag.empty();
                }

                if (argsNbt.contains("args", NbtElement.COMPOUND_TYPE)) {
                    argsNbt = argsNbt.getCompound("args");
                }

                return GrabBag.fromNBT(argsNbt);
            }

            @Override
            public void setSpell(Spell spell, GrabBag args) {
                dataTracker.set(CURRENT_SPELL, Objects.requireNonNull(ModRegistries.SPELL.getId(spell)).toString());
                dataTracker.set(SPELL_ARGS, args != null ? GrabBag.toNBT(args) : new NbtCompound());
            }
            @Override
            public int getCooldown(@Nullable Spell.SpellCooldown cooldown) {
                if (cooldown == null) return 0;

                NbtCompound cooldowns = dataTracker.get(SPELL_COOLDOWN);
                return cooldowns.contains(cooldown.key().toString())
                        ? cooldowns.getInt(cooldown.key().toString())
                        : 0;
            }


            @Override
            public float getCooldownPercent(@Nullable Spell.SpellCooldown cooldown) {
                int cd = getCooldown(cooldown);
                return cd / (float) (cooldown != null ? cooldown.ticks() : 1);
            }

            @Override
            public void setCooldown(@Nullable Spell.SpellCooldown cooldown) {
                if (cooldown == null) return;

                NbtCompound cooldowns = dataTracker.get(SPELL_COOLDOWN).copy();
                cooldowns.putInt(cooldown.key().toString(), cooldown.ticks());
                dataTracker.set(SPELL_COOLDOWN, cooldowns);
            }
        };
    }

    @Override
    public void tickCooldowns(LivingEntity entity) {
        NbtCompound cooldowns = dataTracker.get(SPELL_COOLDOWN).copy();
        boolean updated = false;

        for (String key : new HashSet<>(cooldowns.getKeys())) {
            int ticks = cooldowns.getInt(key);
            if (ticks > 0) {
                cooldowns.putInt(key, ticks - 1);
                updated = true;
            } else {
                cooldowns.remove(key);
                updated = true;
            }
        }

        if (updated) {
            dataTracker.set(SPELL_COOLDOWN, cooldowns);
        }
    }


    @Override
    public int getAngerTime() {
        return 0;
    }

    @Override
    public void setAngerTime(int angerTime) {

    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return null;
    }
    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }
    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {

    }

    @Override
    public void chooseRandomAngerTime() {
    }

}
