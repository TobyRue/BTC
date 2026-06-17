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
        System.out.println(me.age);
        if (me.age > 7 && me.getOwner() instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(ModStatusEffects.UNWARPING)) {

            this.breakEnderPearl(me);
            me.kill();
        }
    }
    @Unique
    private void breakEnderPearl(EnderPearlEntity me) {
        var world = MinecraftClient.getInstance().world;
        double d = me.getX();
        double e = me.getY();
        double f = me.getZ();
        if (world != null) {
            Random random = world.random;

            for (int i = 0; i < 8; ++i) {
                world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_PEARL)), d, e, f, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
            }

            for (var g = 0.0; g < 40; g += 1) {
                world.addParticle(ParticleTypes.PORTAL, d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -5.0, 0.0, Math.sin(g) * -5.0);
                world.addParticle(ParticleTypes.PORTAL, d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -7.0, 0.0, Math.sin(g) * -7.0);
            }
        }
    }
}
