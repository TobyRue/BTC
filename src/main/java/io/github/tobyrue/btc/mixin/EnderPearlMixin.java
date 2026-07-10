package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public class EnderPearlMixin {

    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        var me = (EnderPearlEntity) (Object) this;
        if (me.getOwner() instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(ModStatusEffects.UNWARPING)) {
            this.breakEnderPearl(me);
            me.kill();
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        var me = (EnderPearlEntity) (Object) this;
        if (me.age > 7 && me.getOwner() instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(ModStatusEffects.UNWARPING)) {
            this.breakEnderPearl(me);
            me.kill();
        }
    }
    @Unique
    private void breakEnderPearl(EnderPearlEntity me) {
        //TODO
        if (me.getWorld() instanceof ServerWorld serverWorld) {
            double d = me.getX();
            double e = me.getY();
            double f = me.getZ();
            var random = serverWorld.getRandom();

            for (int i = 0; i < 8; ++i) {
                serverWorld.spawnParticles(
                        new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_PEARL)),
                        d, e, f, 1,
                        random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15,
                        0.0
                );
            }

            for (double g = 0.0; g < 40; g += 1.0) {
                double xOffset = Math.cos(g) * 5.0;
                double zOffset = Math.sin(g) * 5.0;

                serverWorld.spawnParticles(ParticleTypes.PORTAL, d + xOffset, e - 0.4, f + zOffset, 1, Math.cos(g) * -5.0, 0.0, Math.sin(g) * -5.0, 1.0);
                serverWorld.spawnParticles(ParticleTypes.PORTAL, d + xOffset, e - 0.4, f + zOffset, 1, Math.cos(g) * -7.0, 0.0, Math.sin(g) * -7.0, 1.0);
            }
        }
    }
}
