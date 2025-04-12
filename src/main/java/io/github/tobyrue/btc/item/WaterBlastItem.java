package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WaterBlastItem extends Item {
    public WaterBlastItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        Vec3d velocity = user.getRotationVec(1.0f).multiply(1.5f);

        if (!world.isClient) {
            // Spawn the entity 1 block higher
            WaterBlastEntity waterBlast = new WaterBlastEntity(user, world, user.getX(), user.getY() + 1.25, user.getZ(), velocity);
            world.spawnEntity(waterBlast);
            user.getItemCooldownManager().set(this, 15);
//            Vec3d direction = user.getRotationVec(1.0f);
//            waterBlast.setVelocity(direction.multiply(1.5));
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
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

