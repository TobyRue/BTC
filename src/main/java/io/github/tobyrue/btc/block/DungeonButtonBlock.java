package io.github.tobyrue.btc.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.regestries.ModInventoryItemRegistry;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
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
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.BiConsumer;

public class DungeonButtonBlock extends FacingBlock implements IDungeonWire {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty FACING = Properties.FACING;


    private static final VoxelShape DOWN_SHAPE = VoxelShapes.union(createCuboidShape(4, 0, 4, 12, 1, 12), createCuboidShape(5, 1, 5, 11, 2, 11));
    private static final VoxelShape DOWN_PRESSED = VoxelShapes.union(createCuboidShape(4, 0, 4, 12, 1, 12), createCuboidShape(5, 1, 5, 11, 1.5, 11));
    private static final VoxelShape UP_SHAPE = VoxelShapes.union(createCuboidShape(4, 15, 4, 12, 16, 12), createCuboidShape(5, 14, 5, 11, 15, 11));
    private static final VoxelShape UP_PRESSED = VoxelShapes.union(createCuboidShape(4, 15, 4, 12, 16, 12), createCuboidShape(5, 14.5, 5, 11, 15, 11));
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(createCuboidShape(4, 4, 15, 12, 12, 16), createCuboidShape(5, 5, 14, 11, 11, 15));
    private static final VoxelShape NORTH_PRESSED = VoxelShapes.union(createCuboidShape(4, 4, 15, 12, 12, 16), createCuboidShape(5, 5, 14.5, 11, 11, 15));
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(createCuboidShape(4, 4, 0, 12, 12, 1), createCuboidShape(5, 5, 1, 11, 11, 2));
    private static final VoxelShape SOUTH_PRESSED = VoxelShapes.union(createCuboidShape(4, 4, 0, 12, 12, 1), createCuboidShape(5, 5, 0.5, 11, 11, 1));
    private static final VoxelShape WEST_SHAPE = VoxelShapes.union(createCuboidShape(15, 4, 4, 16, 12, 12), createCuboidShape(14, 5, 5, 15, 11, 11));
    private static final VoxelShape WEST_PRESSED = VoxelShapes.union(createCuboidShape(15, 4, 4, 16, 12, 12), createCuboidShape(14.5, 5, 5, 15, 11, 11));
    private static final VoxelShape EAST_SHAPE = VoxelShapes.union(createCuboidShape(0, 4, 4, 1, 12, 12), createCuboidShape(1, 5, 5, 2, 11, 11));
    private static final VoxelShape EAST_PRESSED = VoxelShapes.union(createCuboidShape(0, 4, 4, 1, 12, 12), createCuboidShape(0.5, 5, 5, 1, 11, 11));


    private final int pressTicks;
    private final BlockSetType blockSetType;

    public static final MapCodec<DungeonButtonBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((block) -> {
            return block.blockSetType;
        }), Codec.intRange(1, 1024).fieldOf("ticks_to_stay_pressed").forGetter((block) -> {
            return block.pressTicks;
        }), createSettingsCodec()).apply(instance, DungeonButtonBlock::new);
    });

    public DungeonButtonBlock(BlockSetType blockSetType, int pressTicks, Settings settings) {
        super(settings.sounds(blockSetType.soundType()));
        this.blockSetType = blockSetType;
        this.pressTicks = pressTicks;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(POWERED, false)
                .with(FACING, ctx.getSide().getAxis() == Direction.Axis.Y ? ctx.getSide().getOpposite() : ctx.getSide());
    }

//    @Override
//    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
//        world.updateNeighborsAlways(pos, this);
//        world.updateNeighborsAlways(pos.offset(state.get(FACING).getOpposite()), this);
//    }

    protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.canTriggerBlocks() && !state.get(POWERED)) {
            this.powerOn(state, world, pos, null);
        }

        super.onExploded(state, world, pos, explosion, stackMerger);
    }

//    @Override
//    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
//        if (!moved && !state.isOf(newState.getBlock())) {
//            if (state.get(POWERED)) {
//                this.updateNeighbors(state, world, pos);
//            }
//            super.onStateReplaced(state, world, pos, newState, moved);
//        }
//    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        boolean pressed = state.get(POWERED);
        return switch (direction) {
            case NORTH -> pressed ? NORTH_PRESSED : NORTH_SHAPE;
            case SOUTH -> pressed ? SOUTH_PRESSED : SOUTH_SHAPE;
            case WEST -> pressed ? WEST_PRESSED : WEST_SHAPE;
            case EAST -> pressed ? EAST_PRESSED : EAST_SHAPE;
            case UP -> pressed ? UP_PRESSED : UP_SHAPE;
            case DOWN -> pressed ? DOWN_PRESSED : DOWN_SHAPE;
        };
    }



    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        boolean pressed = state.get(POWERED);
        return switch (direction) {
            case NORTH -> pressed ? NORTH_PRESSED : NORTH_SHAPE;
            case SOUTH -> pressed ? SOUTH_PRESSED : SOUTH_SHAPE;
            case WEST -> pressed ? WEST_PRESSED : WEST_SHAPE;
            case EAST -> pressed ? EAST_PRESSED : EAST_SHAPE;
            case UP -> pressed ? UP_PRESSED : UP_SHAPE;
            case DOWN -> pressed ? DOWN_PRESSED : DOWN_SHAPE;
        };
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        boolean pressed = state.get(POWERED);
        return switch (direction) {
            case NORTH -> pressed ? NORTH_PRESSED : NORTH_SHAPE;
            case SOUTH -> pressed ? SOUTH_PRESSED : SOUTH_SHAPE;
            case WEST -> pressed ? WEST_PRESSED : WEST_SHAPE;
            case EAST -> pressed ? EAST_PRESSED : EAST_SHAPE;
            case UP -> pressed ? UP_PRESSED : UP_SHAPE;
            case DOWN -> pressed ? DOWN_PRESSED : DOWN_SHAPE;
        };
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(POWERED)) {
            return ActionResult.CONSUME;
        } else {
            this.powerOn(state, world, pos, player);
            return ActionResult.success(world.isClient);
        }
    }

    public void powerOn(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_ALL);
        this.updateNeighbors(state, world, pos);
        world.scheduleBlockTick(pos, this, this.pressTicks);
        world.playSound(player, pos, this.blockSetType.buttonClickOn(), SoundCategory.BLOCKS);
        world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_ALL);
            this.updateNeighbors(state, world, pos);
            world.playSound(null, pos, this.blockSetType.buttonClickOff(), SoundCategory.BLOCKS);
            world.emitGameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
        }
    }


    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(state.get(FACING).getAxis() == Direction.Axis.Y ? state.get(FACING) : state.get(FACING).getOpposite()), this);
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && ((state.get(FACING).getAxis() == Direction.Axis.Y ? state.get(FACING) : state.get(FACING).getOpposite()) == direction.getOpposite()) ? 15 : 0;
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected MapCodec<? extends FacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return ((state.get(FACING).getAxis() == Direction.Axis.Y ? state.get(FACING) : state.get(FACING).getOpposite()) == face) && state.get(POWERED);
    }
    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }
    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }
}
