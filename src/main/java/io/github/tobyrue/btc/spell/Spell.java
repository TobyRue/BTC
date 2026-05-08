package io.github.tobyrue.btc.spell;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.stream.IntStream;

public abstract class Spell {
    protected final SpellTypes type;
    public static final Codec<Spell> CODEC = Identifier.CODEC.xmap(
            ModRegistries.SPELL::get,
            ModRegistries.SPELL::getId
    );

    @SuppressWarnings("unchecked")
    public <T> void silenceAnyHost(SpellHost<T> host, Object context, int duration) {
        try {
            T castedContext = (T) context;

            if (context instanceof io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity luminary) {
                luminary.setGlobalCastDelay(duration);
            }

            SpellDataStore data = host.getSpellDataStore(castedContext);
            if (data != null) {

                if (context instanceof ItemStack stack && stack.getItem() instanceof SpellItem) {
                    forceSilenceItem(stack, duration);
                } else {
                    data.setCooldown(new Spell.SpellCooldown(duration, ModRegistries.SPELL.getId(data.getSpell())));
                }
            }
        } catch (ClassCastException ignored) {}
    }
    @SuppressWarnings("unchecked")
    public <T> boolean silenceAnyHostWithResult(SpellHost<T> host, Object context, int duration) {
        try {
            T castedContext = (T) context;

            if (context instanceof io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity luminary) {
                luminary.setGlobalCastDelay(duration);
                return true;
            }

            SpellDataStore data = host.getSpellDataStore(castedContext);
            if (data != null) {

                if (context instanceof ItemStack stack && stack.getItem() instanceof SpellItem) {
                    forceSilenceItem(stack, duration);
                } else {
                    data.setCooldown(new Spell.SpellCooldown(duration, ModRegistries.SPELL.getId(data.getSpell())));
                }
                return true;
            }
        } catch (ClassCastException ignored) {}
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
    public void checkHand(LivingEntity target, ItemStack stack, int duration) {
        if (!stack.isEmpty() && stack.getItem() instanceof SpellHost<?> host) {
            silenceAnyHost(host, stack, duration);
        }
    }
    public boolean checkHandWithResult(LivingEntity target, ItemStack stack, int duration) {
        if (!stack.isEmpty() && stack.getItem() instanceof SpellHost<?> host) {
            return silenceAnyHostWithResult(host, stack, duration);
        }
        return false;
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

    public record SpellContext(World world, Vec3d pos, Vec3d direction, SpellDataStore data, @Nullable LivingEntity user) {}
    public record SpellCooldown(int ticks, Identifier key) {}

    public record InstancedSpell(Spell spell, GrabBag args) {
        public static final Codec<InstancedSpell> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
                Spell.CODEC.fieldOf("spell").forGetter(InstancedSpell::spell),
                NbtCompound.CODEC.fieldOf("args").forGetter(s -> GrabBag.toNBT(s.args()))
        ).apply(instance, (spell, args) -> new InstancedSpell(spell, GrabBag.fromNBT(args)))));
    }
}