package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.TrialCoreBlockEntity;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TrialCoreBlock extends Block implements ModBlockEntityProvider<TrialCoreBlockEntity> {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public TrialCoreBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(POWERED, false);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        boolean isPowered = world.isReceivingRedstonePower(pos) || (sourceBlock instanceof IDungeonWire wire && IDungeonWire.isReceivingDungeonWirePower(world.getBlockState(pos), world, pos, Direction.values()));

        if (isPowered != state.get(POWERED)) {
            if (isPowered) {
                if (world.getBlockEntity(pos) instanceof TrialCoreBlockEntity core) {
                    core.runRandomFunction((ServerWorld) world, pos);
                }
            }
            world.setBlockState(pos, state.with(POWERED, isPowered), 3);
        }
    }


    @Override
    public BlockEntityType<TrialCoreBlockEntity> getBlockEntityType() {
        return ModBlockEntities.TRIAL_CORE_BLOCK_ENTITY;
    }
}