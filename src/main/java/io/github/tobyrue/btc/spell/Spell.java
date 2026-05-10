package io.github.tobyrue.btc.spell;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class Spell {
    protected final SpellTypes type;
    public static final Identifier SILENCE_ID = BTC.identifierOf("silence");
    public static final Codec<Spell> CODEC = Identifier.CODEC.xmap(
            ModRegistries.SPELL::get,
            ModRegistries.SPELL::getId
    );

    @SuppressWarnings("unchecked")
    public <T> void silenceAnyHost(SpellHost<T> host, Object context, int duration, int globalDuration) {
        try {
            T castedContext = (T) context;
            if (context instanceof EldritchLuminaryEntity luminary) {
                luminary.setGlobalCastDelay(globalDuration);
            }

            SpellDataStore data = host.getSpellDataStore(castedContext);
            if (data != null) {
                if (context instanceof ItemStack stack && stack.getItem() instanceof SpellItem) {
                    forceSilenceItem(stack, duration);
                } else {
                    data.setCooldown(new Spell.SpellCooldown(duration, ModRegistries.SPELL.getId(data.getSpell())));

                    data.setCooldown(new Spell.SpellCooldown(duration, SILENCE_ID));
                }
            }
        } catch (ClassCastException ignored) {}
    }

    protected void onDisspelled(SpellContext ctx, int tick, LivingEntity user) {
        if (user.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, user.getX(), user.getY() + 1, user.getZ(), 10, 0.2, 0.2, 0.2, 0.05);
        }
    }
    protected boolean isDisspelled(SpellContext ctx, int tick, LivingEntity user) {
        if (user.getMainHandStack().getItem() instanceof SpellItem spellItem) {
            if (spellItem instanceof SpellHost<ItemStack> host) {
                SpellDataStore data = host.getSpellDataStore(user.getMainHandStack());
                if (data.getCooldown(new Spell.SpellCooldown(0, Spell.SILENCE_ID)) > 0) {
                    onDisspelled(ctx, tick, user);
                    return true;
                }
            }
        } else if (user.getOffHandStack().getItem() instanceof SpellItem spellItem) {
            if (spellItem instanceof SpellHost<ItemStack> host) {
                SpellDataStore data = host.getSpellDataStore(user.getOffHandStack());
                if (data.getCooldown(new Spell.SpellCooldown(0, Spell.SILENCE_ID)) > 0) {
                    onDisspelled(ctx, tick, user);
                    return true;
                }
            }
        } else if (user instanceof EldritchLuminaryEntity entity) {
            if (entity instanceof SpellHost<LivingEntity> host) {
                SpellDataStore data = host.getSpellDataStore(entity);
                if (data.getCooldown(new Spell.SpellCooldown(0, Spell.SILENCE_ID)) > 0) {
                    onDisspelled(ctx, tick, user);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Specifically iterates through the NBT of a SpellItem to lock all existing spell cooldowns
     */
    public void forceSilenceItem(ItemStack stack, int duration) {
        NbtCompound nbt = stack.getOrDefault(ModComponents.SPELL_COMPONENT, NbtComponent.DEFAULT).copyNbt();
        NbtCompound cooldowns = nbt.getCompound("cooldowns");


        for (String key : cooldowns.getKeys()) {
            NbtCompound c = cooldowns.getCompound(key);
            c.putInt("value", duration);
            c.putInt("max", duration);
        }

        NbtComponent component = NbtComponent.of(nbt);
        stack.set(ModComponents.SPELL_COMPONENT, component);
    }
    public void checkHand(LivingEntity target, ItemStack stack, int duration, int globalDuration) {
        if (!stack.isEmpty() && stack.getItem() instanceof SpellHost<?> host) {
            silenceAnyHost(host, stack, duration, globalDuration);
        }
    }

    public static @org.jetbrains.annotations.Nullable Entity getEntityLookedAt(Vec3d lookVec, LivingEntity player, double range, double aimingForgiveness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit())) {
            Box entityBox = entity.getBoundingBox().expand(aimingForgiveness);
            Optional<Vec3d> optionalHit = entityBox.raycast(eyePos, reachVec);

            if (optionalHit.isPresent()) {
                double distanceSq = eyePos.squaredDistanceTo(optionalHit.get());
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    hitEntity = entity;
                }
            }
        }
        return hitEntity;
    }
    public static @org.jetbrains.annotations.Nullable Entity getEntityLookedAt(LivingEntity player, double range, double aimingForgiveness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit())) {
            Box entityBox = entity.getBoundingBox().expand(aimingForgiveness);
            Optional<Vec3d> optionalHit = entityBox.raycast(eyePos, reachVec);

            if (optionalHit.isPresent()) {
                double distanceSq = eyePos.squaredDistanceTo(optionalHit.get());
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    hitEntity = entity;
                }
            }
        }
        return hitEntity;
    }
    public static @org.jetbrains.annotations.Nullable Entity getEntityLookedAt(World world, Vec3d startPos, Vec3d lookVec, double range, double aimingForgiveness) {
        Vec3d reachVec = startPos.add(lookVec.multiply(range));

        Box searchBox = new Box(startPos, reachVec).expand(1.0D);

        Entity hitEntity = null;
        double closestDistanceSq = range * range;


        for (Entity entity : world.getOtherEntities(null, searchBox, e -> e.isAttackable() && e.canHit())) {
            Box entityBox = entity.getBoundingBox().expand(aimingForgiveness);
            Optional<Vec3d> optionalHit = entityBox.raycast(startPos, reachVec);

            if (optionalHit.isPresent()) {
                double distanceSq = startPos.squaredDistanceTo(optionalHit.get());
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    hitEntity = entity;
                }
            }
        }
        return hitEntity;
    }

    public static @Nullable LivingEntity getTargetEntity(World world, Vec3d startPos, Vec3d lookVec, GrabBag args, LivingEntity target, double range, double aimingForgiveness, double searchRadius) {
        double range2 = args.getDouble("range", range);
        double forgiveness = args.getDouble("aimingForgiveness", aimingForgiveness);
        double searchRadius2 = args.getDouble("searchRadius", searchRadius);

        Entity lookedAt = getEntityLookedAt(world, startPos, lookVec, range2, forgiveness);

        if (lookedAt instanceof LivingEntity living) {
            return living;
        }

        Vec3d reachVec = startPos.add(lookVec.multiply(range2));
        Box searchBox = new Box(reachVec.subtract(searchRadius2, searchRadius2, searchRadius2),
                reachVec.add(searchRadius2, searchRadius2, searchRadius2));

        List<LivingEntity> nearby = world.getEntitiesByClass(LivingEntity.class, searchBox,
                e -> e.isAlive() && e.isAttackable() && e == target);

        LivingEntity closest = null;
        double dist = Double.MAX_VALUE;
        for (LivingEntity potential : nearby) {
            double d = potential.squaredDistanceTo(reachVec);
            if (d < dist) {
                dist = d;
                closest = potential;
            }
        }

        return closest;
    }

    public static @Nullable LivingEntity isTargetInRange(LivingEntity user, LivingEntity target, double range) {
        if (user == null || target == null) return null;
        return target.squaredDistanceTo(user) <= range ? target : null;
    }

    public Spell(final SpellTypes type) {
        this.type = type;
    }

    protected boolean canUse(final SpellContext ctx, final GrabBag args) {
        return ctx.data().getCooldown(this.getCooldown(args, ctx.user())) == 0;
    }

    public final boolean tryUse(final SpellContext ctx, final GrabBag args) {
        if (canUse(ctx, args)) {
            use(ctx, args);
            ctx.data().setCooldown(this.getCooldown(args, ctx.user()));
            return true;
        }
        return false;
    }

    @Nullable
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return null;
    }

    public abstract int getColor(GrabBag args);

    protected abstract void use(final SpellContext ctx, final GrabBag args);

    public SpellTypes getSpellType() {
        return this.type;
    }

    public Text getDescription(final GrabBag args) {
        return Text.translatable(this.getTranslationKey() + ".description");
    }

    public Text getDescription() {
        return getDescription(GrabBag.empty());
    }

    public Text getName(final GrabBag args) {
        return Text.translatable(this.getTranslationKey());
    }

    public Text getName() {
        return this.getName(GrabBag.empty());
    }

    @Override
    public String toString() {
        return ModRegistries.SPELL.getEntry(this).getIdAsString();
    }
    @Deprecated
    public String getPureName() {
        return toString().substring(ModRegistries.SPELL.getId(this).getNamespace().length() + 1);
    }

    public String getTranslationKey() {
        return Util.createTranslationKey("spell", ModRegistries.SPELL.getId(this));
    }

    public static Identifier getId(Spell type) {
        return ModRegistries.SPELL.getId(type);
    }

    public record SpellContext(World world, Vec3d pos, Vec3d direction, SpellDataStore data, @Nullable LivingEntity user, @Nullable LivingEntity target) {}
    public record SpellCooldown(int ticks, Identifier key) {}

    public record InstancedSpell(Spell spell, GrabBag args) {
        public static final Codec<InstancedSpell> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
                Spell.CODEC.fieldOf("spell").forGetter(InstancedSpell::spell),
                NbtCompound.CODEC.fieldOf("args").forGetter(s -> GrabBag.toNBT(s.args()))
        ).apply(instance, (spell, args) -> new InstancedSpell(spell, GrabBag.fromNBT(args)))));
    }
}