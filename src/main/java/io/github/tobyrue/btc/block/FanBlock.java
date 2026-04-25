package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.*;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class FanBlock extends Block implements ModBlockEntityProvider<FanBlockEntity>, ModTickBlockEntityProvider<FanBlockEntity> {
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;

    private static final double RADIUS = 1;
    private static final double DEPTH = 3;
    private static final double BASE_RADIUS = 0.5;

    public FanBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    public static List<Entity> getEntitiesInCone(BlockState state, World world, BlockPos pos) {
        var box = new Box(pos.toCenterPos(), pos.toCenterPos());
        var direction = Vec3d.of(state.get(FACING).getVector());
        var start = pos.toCenterPos();
        var entities = world.getEntitiesByClass(Entity.class, box = switch (state.get(FACING)) {
            case DOWN -> box.expand(RADIUS, DEPTH / 2d, RADIUS).offset(0, -(1 + DEPTH / 4d) - 0.5, 0);
            case UP -> box.expand(RADIUS, DEPTH / 2d, RADIUS).offset(0, (1 + DEPTH / 4d) + 0.5, 0);
            case NORTH -> box.expand(RADIUS, RADIUS, DEPTH / 2d).offset(0, 0, -(1 + DEPTH / 4d) - 0.5);
            case SOUTH -> box.expand(RADIUS, RADIUS, DEPTH / 2d).offset(0, 0, (1 + DEPTH / 4d) + 0.5);
            case WEST -> box.expand(DEPTH / 2d, RADIUS, RADIUS).offset(-(1 + DEPTH / 4d) - 0.5, 0, 0);
            case EAST -> box.expand(DEPTH / 2d, RADIUS, RADIUS).offset((1 + DEPTH / 4d) + 0.5, 0, 0);
        }, entity -> {
            Box entityBox = entity.getBoundingBox();

            Vec3d relativeCenter = entityBox.getCenter().subtract(start);
            double projectedDepth = relativeCenter.dotProduct(direction);

            double clampedDepth = Math.max(0, Math.min(DEPTH, projectedDepth));

            Vec3d coneAxisPoint = start.add(direction.multiply(clampedDepth));

            double closestX = Math.max(entityBox.minX, Math.min(coneAxisPoint.x, entityBox.maxX));
            double closestY = Math.max(entityBox.minY, Math.min(coneAxisPoint.y, entityBox.maxY));
            double closestZ = Math.max(entityBox.minZ, Math.min(coneAxisPoint.z, entityBox.maxZ));
            Vec3d closestPointOnHitbox = new Vec3d(closestX, closestY, closestZ);

            Vec3d relativeClosest = closestPointOnHitbox.subtract(start);
            double finalDepth = relativeClosest.dotProduct(direction);

            if (finalDepth < 0 || finalDepth > DEPTH) return false;

            double t = finalDepth / DEPTH;
            double radiusAtDist = BASE_RADIUS + t * (RADIUS - BASE_RADIUS);

            Vec3d vec = relativeClosest.subtract(direction.multiply(finalDepth));
            return vec.lengthSquared() < (radiusAtDist * radiusAtDist);
        });

//        var levels = 10;
//        var steps = 16;
//
//        var right = direction.crossProduct(new Vec3d(0, 1, 0));
//        if (right.lengthSquared() < 1e-6) {
//            right = direction.crossProduct(new Vec3d(1, 0, 0));
//        }
//        right = right.normalize();
//        var up = right.crossProduct(direction).normalize();

//        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
//            for (int j = 0; j <= levels; j++) {
//                double t = (double) j / levels;
//                double currentDepth = t * DEPTH;
//                double currentRadius = BASE_RADIUS + t * (RADIUS - BASE_RADIUS);
//                var center = start.add(direction.multiply(currentDepth));
//                for (int i = 0; i < steps; i++) {
//                    double angle = (2 * Math.PI * i) / steps;
//                    var offset = right.multiply(Math.cos(angle) * currentRadius)
//                            .add(up.multiply(Math.sin(angle) * currentRadius));
//                    var point = center.add(offset);
//                    world.addParticle(ParticleTypes.END_ROD, point.x, point.y, point.z, 0, 0, 0);
//                }
//            }
//        }
//        world.addParticle(ParticleTypes.END_ROD, box.minX, box.minY, box.minZ, 0, 0, 0);
//        world.addParticle(ParticleTypes.END_ROD, box.minX, box.minY, box.maxZ, 0, 0, 0);
//        world.addParticle(ParticleTypes.END_ROD, box.minX, box.maxY, box.minZ, 0, 0, 0);
//        world.addParticle(ParticleTypes.END_ROD, box.minX, box.maxY, box.maxZ, 0, 0, 0);
//        world.addParticle(ParticleTypes.END_ROD, box.maxX, box.minY, box.minZ, 0, 0, 0);
//        world.addParticle(ParticleTypes.END_ROD, box.maxX, box.minY, box.maxZ, 0, 0, 0);
//        world.addParticle(ParticleTypes.END_ROD, box.maxX, box.maxY, box.minZ, 0, 0, 0);
//        world.addParticle(ParticleTypes.END_ROD, box.maxX, box.maxY, box.maxZ, 0, 0, 0);
        return entities;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        world.setBlockState(pos, state.with(POWERED,
                IDungeonWire.isReceivingDungeonWirePower(state, world, pos, Arrays.stream(Direction.values().clone()).filter(dir -> dir != state.get(FACING))) || world.isReceivingRedstonePower(pos)));

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getSide();

        return this.getDefaultState()
                .with(FACING, facing)
                .with(POWERED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockEntityType<FanBlockEntity> getBlockEntityType() {
        return ModBlockEntities.FAN_BLOCK_ENTITY;
    }
}
