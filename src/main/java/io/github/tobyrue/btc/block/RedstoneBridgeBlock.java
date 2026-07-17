package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.RedstoneBridgeBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RedstoneBridgeBlock extends Block implements ModBlockEntityProvider<RedstoneBridgeBlockEntity> {
    public static final EnumProperty<RedstoneBridgeType> X_AXIS = EnumProperty.of("x_axis", RedstoneBridgeType.class);
    public static final EnumProperty<RedstoneBridgeType> Y_AXIS = EnumProperty.of("y_axis", RedstoneBridgeType.class);
    public static final EnumProperty<RedstoneBridgeType> Z_AXIS = EnumProperty.of("z_axis", RedstoneBridgeType.class);

    @Override
    public BlockEntityType<RedstoneBridgeBlockEntity> getBlockEntityType() {
        return ModBlockEntities.REDSTONE_BRIDGE_BLOCK_ENTITY;
    }

    public enum RedstoneBridgeType implements StringIdentifiable {
        ALIGNED("aligned"),
        INVERTED("inverted"),
        NONE("none");

        public final String name;
        RedstoneBridgeType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }

    public RedstoneBridgeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(X_AXIS, RedstoneBridgeType.ALIGNED)
                .with(Y_AXIS, RedstoneBridgeType.ALIGNED)
                .with(Z_AXIS, RedstoneBridgeType.ALIGNED));
    }
    

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (world.isClient) return;

        if (world.getBlockEntity(pos) instanceof RedstoneBridgeBlockEntity bridgeEntity) {
            if (bridgeEntity.updateCachedPower(world, state)) {
                world.updateNeighbors(pos, this);
            }
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (world.isClient) return;

        if (world.getBlockEntity(pos) instanceof RedstoneBridgeBlockEntity bridgeEntity) {
            if (bridgeEntity.updateCachedPower(world, state)) {
                world.updateNeighbors(pos, this);
            }
        }
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (world.getBlockEntity(pos) instanceof RedstoneBridgeBlockEntity bridgeEntity) {
            return bridgeEntity.getWeakPower(direction);
        }
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(X_AXIS, Y_AXIS, Z_AXIS);
        super.appendProperties(builder);
    }
}