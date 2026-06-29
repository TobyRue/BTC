package io.github.tobyrue.btc.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.block.entities.FancyPotBlockEntity;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.SpyGlassBlockEntity;
import io.github.tobyrue.btc.client.SpyGlassCameraController;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SpyGlassBlock extends WallMountedBlock implements ModBlockEntityProvider<SpyGlassBlockEntity> {
    public static final MapCodec<SpyGlassBlock> CODEC = SpyGlassBlock.createCodec(SpyGlassBlock::new);
    private static final Map<String, VoxelShape> SHAPE_CACHE = Maps.newHashMap();

    private static final VoxelShape BASE_SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.24999999999999897, 3.4416913763379853e-15, 0.24999999999999967, 0.749999999999999, 0.6250000000000034, 0.7499999999999997)
    );

    static {
        for (BlockFace face : BlockFace.values()) {
            for (Direction dir : Direction.Type.HORIZONTAL) {
                SHAPE_CACHE.put(face.name() + "_" + dir.name(), rotateShape(BASE_SHAPE, face, dir));
            }
        }
    }


    public SpyGlassBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FACE, BlockFace.FLOOR));
    }

    public static VoxelShape rotateShape(VoxelShape base, BlockFace face, Direction direction) {
        VoxelShape[] buffer = new VoxelShape[]{VoxelShapes.empty()};

        base.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double x1 = minX, y1 = minY, z1 = minZ;
            double x2 = maxX, y2 = maxY, z2 = maxZ;

            if (face == BlockFace.CEILING) {
                double tempY1 = 1.0 - y2;
                double tempY2 = 1.0 - y1;
                y1 = tempY1; y2 = tempY2;
            } else if (face == BlockFace.WALL) {
                double tempY1 = z1;
                double tempY2 = z2;
                double tempZ1 = 1.0 - y2;
                double tempZ2 = 1.0 - y1;
                y1 = tempY1; y2 = tempY2;
                z1 = tempZ1; z2 = tempZ2;
            }


            switch (direction) {
                case SOUTH -> {
                    double tx1 = 1.0 - x2; double tx2 = 1.0 - x1;
                    double tz1 = 1.0 - z2; double tz2 = 1.0 - z1;
                    x1 = tx1; x2 = tx2; z1 = tz1; z2 = tz2;
                }
                case WEST -> {
                    double tx1 = z1; double tx2 = z2;
                    double tz1 = 1.0 - x2; double tz2 = 1.0 - x1;
                    x1 = tx1; x2 = tx2; z1 = tz1; z2 = tz2;
                }
                case EAST -> {
                    double tx1 = 1.0 - z2; double tx2 = 1.0 - z1;
                    double tz1 = x1; double tz2 = x2;
                    x1 = tx1; x2 = tx2; z1 = tz1; z2 = tz2;
                }
                default -> {}
            }

            VoxelShape rotatedBox = VoxelShapes.cuboid(
                    Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
                    Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
            );
            buffer[0] = VoxelShapes.union(buffer[0], rotatedBox);
        });

        return buffer[0];
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockFace face = state.get(FACE);
        Direction dir = state.get(FACING);
        return SHAPE_CACHE.getOrDefault(face.name() + "_" + dir.name(), BASE_SHAPE);
    }


    @Override
    protected MapCodec<? extends WallMountedBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    public BlockEntityType<SpyGlassBlockEntity> getBlockEntityType() {
        return ModBlockEntities.SPY_GLASS_BLOCK_ENTITY;
    }
    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    protected int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }
    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            if (SpyGlassCameraController.isActive()) {
                SpyGlassCameraController.stopZooming();
            } else {
                SpyGlassCameraController.startZooming(pos);
            }
        }
        return net.minecraft.util.ActionResult.SUCCESS;
    }
}