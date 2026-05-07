package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.*;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DisspellSpell extends Spell {

    public DisspellSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    public void use(final SpellContext ctx, final GrabBag args) {
        World world = ctx.world();
        LivingEntity user = ctx.user();
        if (user == null) return;

        double speed = args.getDouble("speed", 1.2d);
        int lifetime = args.getInt("lifetime", 40);
        double range = args.getDouble("range", 20.0d);
        double forgiveness = args.getDouble("forgiveness", 0.6d);
        int silenceDuration = args.getInt("silenceDuration", 160);

        Vec3d start = user.getCameraPosVec(1.0F);
        Vec3d dir = user.getRotationVec(1.0F).normalize();

        ((Ticker.TickerTarget) user).bTC$add(
                Ticker.forTicks(tick -> {
                    Vec3d pos = start.add(dir.multiply(speed * tick));

                    if (!world.isClient) {
                        ((ServerWorld) world).spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
                    }

                    if (!world.isClient) {
                        var entity = getEntityLookedAt(user, range, forgiveness);
                        if (entity instanceof LivingEntity target) {

                            if (target instanceof SpellHost host) {
                                silenceAnyHost(host, target, silenceDuration);
                            }

                            checkHand(target, target.getMainHandStack(), silenceDuration);
                            checkHand(target, target.getOffHandStack(), silenceDuration);

                            ((ServerWorld) world).spawnParticles(ParticleTypes.FLASH, target.getX(), target.getEyeY(), target.getZ(), 1, 0, 0, 0, 0);
                            return true;
                        }
                    }

                    return tick >= lifetime;
                }, lifetime)
        );
    }

    private void checkHand(LivingEntity target, ItemStack stack, int duration) {
        if (!stack.isEmpty() && stack.getItem() instanceof SpellHost host) {
            silenceAnyHost(host, stack, duration);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void silenceAnyHost(SpellHost<T> host, Object context, int duration) {
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

    /**
     * Specifically iterates through the NBT of a SpellItem to lock all existing spell cooldowns
     */
    private void forceSilenceItem(ItemStack stack, int duration) {
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

    public static @Nullable Entity getEntityLookedAt(LivingEntity player, double range, double aimingForgiveness) {
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

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("disspell"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF00FFFF;
    }
}