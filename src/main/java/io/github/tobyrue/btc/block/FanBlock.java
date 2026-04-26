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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class FanBlock extends Block implements ModBlockEntityProvider<FanBlockEntity>, ModTickBlockEntityProvider<FanBlockEntity> {
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty<FanMode> MODE = EnumProperty.of("mode", FanMode.class);

    public enum FanMode implements StringIdentifiable {
        BLOW("blow"),
        PULL("pull");

        private final String name;
        FanMode(String name) { this.name = name; }
        @Override public String asString() { return this.name; }
    }

    public FanBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(MODE, FanMode.BLOW));
    }


    public static List<Entity> getEntitiesInCone(BlockState state, World world, BlockPos pos, double base_radius, double far_radius, double depth) {
        Direction facing = state.get(FACING);
        Vec3d direction = Vec3d.of(facing.getVector());
        Vec3d start = pos.toCenterPos().add(direction.multiply(0.5));

        double maxR = Math.max(base_radius, far_radius);
        Box searchBox = new Box(start, start.add(direction.multiply(depth))).expand(maxR);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            drawDebugCone(world, start, direction, depth, base_radius, far_radius);
        }

        return world.getEntitiesByClass(Entity.class, searchBox, entity -> {
            Box entityBox = entity.getBoundingBox();
            Vec3d entityCenter = entityBox.getCenter();

            BlockHitResult hit = world.raycast(new RaycastContext(
                    start,
                    entityCenter,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    entity
            ));

            if (hit.getType() != HitResult.Type.MISS) {
                return false;
            }

            Vec3d relativeCenter = entityCenter.subtract(start);
            double projectedDepth = relativeCenter.dotProduct(direction);
            if (projectedDepth < 0 || projectedDepth > depth) return false;

            double t = projectedDepth / depth;
            double radiusAtDist = base_radius + t * (far_radius - base_radius);

            Vec3d axisPoint = start.add(direction.multiply(projectedDepth));

            double closestX = Math.max(entityBox.minX, Math.min(axisPoint.x, entityBox.maxX));
            double closestY = Math.max(entityBox.minY, Math.min(axisPoint.y, entityBox.maxY));
            double closestZ = Math.max(entityBox.minZ, Math.min(axisPoint.z, entityBox.maxZ));
            Vec3d closestPoint = new Vec3d(closestX, closestY, closestZ);

            Vec3d relClosest = closestPoint.subtract(start);
            double finalDepth = relClosest.dotProduct(direction);
            Vec3d vecToAxis = relClosest.subtract(direction.multiply(finalDepth));

            return vecToAxis.lengthSquared() < (radiusAtDist * radiusAtDist);
        });
    }

    private static void drawDebugCone(World world, Vec3d start, Vec3d direction, double depth, double base_radius, double far_radius) {
        int levels = 12;
        int steps = 12;

        Vec3d right = direction.crossProduct(new Vec3d(0, 1, 0));
        if (right.lengthSquared() < 1e-6) right = direction.crossProduct(new Vec3d(1, 0, 0));
        right = right.normalize();
        Vec3d up = right.crossProduct(direction).normalize();

        for (int j = 0; j <= levels; j++) {
            double t = (double) j / levels;
            double currentDepth = t * depth;
            double currentRadius = base_radius + t * (far_radius - base_radius);
            Vec3d center = start.add(direction.multiply(currentDepth));

            for (int i = 0; i < steps; i++) {
                double angle = (2 * Math.PI * i) / steps;
                Vec3d offset = right.multiply(Math.cos(angle) * currentRadius)
                        .add(up.multiply(Math.sin(angle) * currentRadius));
                Vec3d point = center.add(offset);

                world.addParticle(ParticleTypes.END_ROD, point.x, point.y, point.z, 0, 0, 0);
            }
        }
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
                .with(POWERED, false)
                .with(MODE, FanMode.BLOW);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, MODE);
    }

    @Override
    public BlockEntityType<FanBlockEntity> getBlockEntityType() {
        return ModBlockEntities.FAN_BLOCK_ENTITY;
    }
}
