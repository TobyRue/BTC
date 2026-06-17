package io.github.tobyrue.btc.block;

import com.jcraft.jorbis.DspState;
import io.github.tobyrue.btc.entity.custom.SuperHappyKillBallEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.block.TargetBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KillBallReceptorBlock extends Block {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty KILL_ON_HIT = BooleanProperty.of("kill_on_hit");


    public KillBallReceptorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWERED, false).with(KILL_ON_HIT, true));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }
    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            Box blockBox = new Box(pos);
            List<SuperHappyKillBallEntity> nearbyBalls = world.getEntitiesByClass(
                    SuperHappyKillBallEntity.class,
                    blockBox.expand(0.1),
                    entity -> true
            );

            if (nearbyBalls.isEmpty()) {
                world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_ALL);

                world.updateNeighbors(pos, this);

                for (Direction dir : Direction.values()) {
                    world.updateNeighbors(pos.offset(dir), this);
                }
            } else {
                world.scheduleBlockTick(pos, this, 20);
            }
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, net.minecraft.world.World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (!world.isClient && state.get(POWERED) && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.scheduleBlockTick(pos, this, 1);
        }
    }
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWERED, false).with(KILL_ON_HIT, true);
    }
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, KILL_ON_HIT);
    }

}
