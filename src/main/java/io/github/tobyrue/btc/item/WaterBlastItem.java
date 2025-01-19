package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WindChargeItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class WaterBlastItem extends Item {
    public WaterBlastItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        Vec3d velocity = Vec3d.unpackRgb(2);

        if (!world.isClient) {
            System.out.println("Spawning WaterBlastEntity at: " + user.getX() + ", " + user.getY() + ", " + user.getZ());
            WaterBlastEntity waterBlast = new WaterBlastEntity(user, world, user.getX(), user.getY(), user.getZ(), velocity);
            waterBlast.setVelocity(user, user.getPitch(), user.getYaw(), 2.0f, 2.5f, 1f);
            world.spawnEntity(waterBlast);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

//    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
//        Random random = world.getRandom();
//        double d = random.nextTriangular((double)direction.getOffsetX(), 0.11485000000000001);
//        double e = random.nextTriangular((double)direction.getOffsetY(), 0.11485000000000001);
//        double f = random.nextTriangular((double)direction.getOffsetZ(), 0.11485000000000001);
//        Vec3d vec3d = new Vec3d(d, e, f);
//        WaterBlastEntity waterBlast = new WaterBlastEntity(world, pos.getX(), pos.getY(), pos.getZ(), vec3d);
//        waterBlast.setVelocity(vec3d);
//        return waterBlast;
//    }
}
