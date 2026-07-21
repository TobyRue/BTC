package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DungeonDoorBlock extends Block {
    public static final EnumProperty<DoorType> TYPE = EnumProperty.of("door_type", DoorType.class);
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    public enum DoorType implements StringIdentifiable {
        NORMAL("normal"),
        WIRED("wired"),
        LOCKED("locked"),
        KEYHOLE("keyhole"),
        GOLEM("golem");

        private final String name;
        DoorType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }

    private static final VoxelShape CUBE_SHAPE = Block.createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
    private static final VoxelShape FULL_BLOCK_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public static final int MAX_DISTANCE = 7;

    public DungeonDoorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(OPEN, false)
                .with(TYPE, DoorType.NORMAL));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(OPEN, false)
                .with(TYPE, DoorType.NORMAL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN, TYPE);
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(TYPE) == DoorType.GOLEM && !state.get(OPEN)) {
            NbtCompound leftShoulder = player.getShoulderEntityLeft();
            NbtCompound rightShoulder = player.getShoulderEntityRight();
            if (!leftShoulder.isEmpty() && leftShoulder.getString("id").equals("btc:key_golem")) {
                player.setShoulderEntityLeft(new NbtCompound());
                openConnectedDoors(world, pos, false, 0);
                return ItemActionResult.SUCCESS;
            } else if (!rightShoulder.isEmpty() && rightShoulder.getString("id").equals("btc:key_golem")) {
                player.setShoulderEntityRight(new NbtCompound());
                openConnectedDoors(world, pos, false, 0);
                return ItemActionResult.SUCCESS;
            }
        } else if (stack.isOf(Items.TRIAL_KEY) && state.get(TYPE) == DoorType.KEYHOLE && !state.get(OPEN)) {
            stack.decrementUnlessCreative(1, player);
            openConnectedDoors(world, pos, false, 0);
            return ItemActionResult.SUCCESS;
        } else if (state.get(TYPE) == DoorType.NORMAL && !state.get(OPEN)) {
            openConnectedDoors(world, pos, true, 4000);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(TYPE) == DoorType.GOLEM && !state.get(OPEN)) {
            NbtCompound leftShoulder = player.getShoulderEntityLeft();
            NbtCompound rightShoulder = player.getShoulderEntityRight();
            if ((!leftShoulder.isEmpty() && leftShoulder.getString("id").equals("btc:key_golem")) ||
                    (!rightShoulder.isEmpty() && rightShoulder.getString("id").equals("btc:key_golem"))) {
                if (!leftShoulder.isEmpty()) player.setShoulderEntityLeft(new NbtCompound());
                else player.setShoulderEntityRight(new NbtCompound());
                openConnectedDoors(world, pos, false, 0);
                return ActionResult.SUCCESS;
            }
        }
        if (state.get(TYPE) == DoorType.NORMAL && !state.get(OPEN)) {
            openConnectedDoors(world, pos, true, 4000);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    private void openConnectedDoors(World world, BlockPos pos, boolean autoClose, int delayMs) {
        for (BlockPos offsetPos : findDoors(world, pos)) {
            if (autoClose) {
                setOpen(world.getBlockState(offsetPos), world, offsetPos, true, delayMs);
            } else {
                setOpenNoClose(world.getBlockState(offsetPos), world, offsetPos);
            }
        }
    }

    private Set<BlockPos> findDoors(World world, BlockPos originPos) {
        HashSet<BlockPos> found = new HashSet<>();
        Queue<Pair<BlockPos, Integer>> queue = new LinkedList<>();
        BlockState originState = world.getBlockState(originPos);

        queue.add(new Pair<>(originPos, 0));
        found.add(originPos);

        while(!queue.isEmpty()) {
            var entry = queue.poll();
            var pos = entry.getLeft();
            int distance = entry.getRight();

            if(distance < MAX_DISTANCE) {
                for(Direction direction : Direction.values()) {
                    var neighborPos = pos.offset(direction);
                    var neighborState = world.getBlockState(neighborPos);

                    if(!found.contains(neighborPos) && neighborState.getBlock() instanceof DungeonDoorBlock) {
                        boolean match = (neighborState.get(TYPE) == originState.get(TYPE))
                                || (((neighborState.get(TYPE) == DoorType.KEYHOLE) || (neighborState.get(TYPE) == DoorType.GOLEM)) && (originState.get(TYPE) == DoorType.LOCKED))
                                || (((originState.get(TYPE) == DoorType.KEYHOLE) || (originState.get(TYPE) == DoorType.GOLEM)) && (neighborState.get(TYPE) == DoorType.LOCKED));

                        if (match) {
                            queue.add(new Pair<>(neighborPos, distance + 1));
                            found.add(neighborPos);
                        }
                    }
                }
            }
        }
        return found;
    }

    private ActionResult setOpen(BlockState state, World world, BlockPos pos, boolean open) {
        return setOpen(state, world, pos, open, null);
    }

    private ActionResult setOpen(BlockState state, World world, BlockPos pos, boolean open, @Nullable Integer delayMs) {
        if(state.get(OPEN) != open) {
            world.setBlockState(pos, state.with(OPEN, open), Block.NOTIFY_ALL);
            world.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 1.0f, 1.0f);

            if(open) {
                world.emitGameEvent(GameEvent.BLOCK_OPEN, pos, GameEvent.Emitter.of(state));
                if (delayMs != null && !world.isClient()) {
                    int ticks = delayMs / 50;
                    world.scheduleBlockTick(pos, this, ticks);
                }
            } else {
                world.emitGameEvent(GameEvent.BLOCK_CLOSE, pos, GameEvent.Emitter.of(state));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(OPEN)) {
            setOpen(state, world, pos, false);
        }
    }

    private ActionResult setOpenNoClose(BlockState state, World world, BlockPos pos) {
        if(!state.get(OPEN)) {
            world.setBlockState(pos, state.with(OPEN, true), Block.NOTIFY_ALL);
            world.playSound(null, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(OPEN) ? CUBE_SHAPE : FULL_BLOCK_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(OPEN) ? VoxelShapes.empty() : FULL_BLOCK_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return state.get(OPEN) ? VoxelShapes.empty() : FULL_BLOCK_SHAPE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if(state.get(TYPE) == DoorType.WIRED && sourceBlock instanceof IDungeonWire) {
            var doors = findDoors(world, pos);
            var powered = doors.stream().anyMatch(doorPos -> IDungeonWire.isReceivingDungeonWirePower(world.getBlockState(doorPos), world, doorPos, Direction.values()));
            for (BlockPos offsetPos : doors) {
                setOpen(world.getBlockState(offsetPos), world, offsetPos, powered);
            }
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private static class Pair<L, R> {
        private final L left;
        private final R right;
        public Pair(L left, R right) { this.left = left; this.right = right; }
        public L getLeft() { return left; }
        public R getRight() { return right; }
    }
}