package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.FanBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FanBlockEntity extends BlockEntity implements BlockEntityTicker<FanBlockEntity> {

    public FanBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FAN_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, FanBlockEntity blockEntity) {
        if (state.get(FanBlock.POWERED)) {
            var directionVec = this.getCachedState().get(FanBlock.FACING).getVector();
            var direction = Vec3d.of(directionVec);
            var baseVelocity = 0.2;

            for (Entity entity : FanBlock.getEntitiesInCone(state, world, pos)) {
                var currentVelocity = entity.getVelocity();
                var newVelocity = currentVelocity.add(direction.multiply(baseVelocity));
                if (newVelocity.length() < 0.3) {
                    entity.setVelocity(newVelocity);
                    entity.velocityModified = true;
                }
            }

            if (world.isClient) {
                spawnGustParticles(world, pos, direction, baseVelocity / 2);
            }
        }
    }

    private void spawnGustParticles(World world, BlockPos pos, Vec3d direction, double speed) {
        double spawnX = pos.getX() + 0.5 + (direction.x * 0.6);
        double spawnY = pos.getY() + 0.5 + (direction.y * 0.6);
        double spawnZ = pos.getZ() + 0.5 + (direction.z * 0.6);

        double jitter = 0.05;
        double vx = (direction.x * speed) + (world.random.nextDouble() - 0.5) * jitter;
        double vy = (direction.y * speed) + (world.random.nextDouble() - 0.5) * jitter;
        double vz = (direction.z * speed) + (world.random.nextDouble() - 0.5) * jitter;

        world.addParticle(
                net.minecraft.particle.ParticleTypes.CLOUD,
                spawnX, spawnY, spawnZ,
                vx, vy, vz
        );
    }
}
