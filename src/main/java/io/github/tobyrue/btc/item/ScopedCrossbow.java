package io.github.tobyrue.btc.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ScopedCrossbow extends CrossbowItem {
    private static final float VELOCITY_MULTIPLIER = 1.5f;
    public ScopedCrossbow(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        if (enchantment.value().isAcceptableItem(Items.BOW.getDefaultStack()) || enchantment.value().isAcceptableItem(Items.CROSSBOW.getDefaultStack()) ) {
            return true;
        }
        return super.canBeEnchantedWith(stack, enchantment, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        super.use(world, user, hand);
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        float modifiedSpeed = speed * VELOCITY_MULTIPLIER;
        super.shoot(shooter, projectile, index, modifiedSpeed, divergence, yaw, target);
    }


    @Override
    public void shootAll(World world, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float divergence, @Nullable LivingEntity target) {
        float modifiedSpeed = speed * VELOCITY_MULTIPLIER;
        super.shootAll(world, shooter, hand, stack, modifiedSpeed, divergence, target);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.playStopUsingSound(user);
        return stack;
    }


    private void playStopUsingSound(LivingEntity user) {
        user.playSound(SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0f, 1.0f);
    }
}
