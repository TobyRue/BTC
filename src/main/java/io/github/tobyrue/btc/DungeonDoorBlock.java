package io.github.tobyrue.btc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DungeonDoorBlock extends Block {
    public static final BooleanProperty NORMAL = BooleanProperty.of("normal");
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    public DungeonDoorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(OPEN, false)
                .with(NORMAL, true));
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(OPEN, false)
                .with(NORMAL, true);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORMAL);
        builder.add(OPEN);
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        updateStateBasedOnNeighbors(state, world, pos);
    }
    private void updateStateBasedOnNeighbors(BlockState state, World world, BlockPos pos) {
        boolean open = true;



        BlockState newState = state
                .with(OPEN, open);
        //System.out.println("Updating state of block at " + pos + " to " + newState);
        world.setBlockState(pos, newState, NOTIFY_ALL_AND_REDRAW);
    }
}
