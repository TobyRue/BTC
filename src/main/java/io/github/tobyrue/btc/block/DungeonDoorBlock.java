package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.wires.IDungeonWire;
import io.github.tobyrue.btc.wires.IOnBlockUpdate;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

        DoorType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }

    private static final VoxelShape CUBE_SHAPE = Block.createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
    private static final VoxelShape FULL_BLOCK_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public static final int MAX_DISTANCE = 7;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


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
        builder.add(OPEN);
        builder.add(TYPE);
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(TYPE) == DoorType.GOLEM && !state.get(OPEN)) {
            NbtCompound leftShoulder = player.getShoulderEntityLeft();
            NbtCompound rightShoulder = player.getShoulderEntityLeft();
            if (!leftShoulder.isEmpty()) {
                String entityId = leftShoulder.getString("id");
                if (entityId.equals("btc:key_golem")) {
                    player.setShoulderEntityLeft(new NbtCompound());
                    for (BlockPos offsetPos : findDoors(world, pos)) {
                        setOpenNoClose(world.getBlockState(offsetPos), world, offsetPos);
                    }
                    return ItemActionResult.SUCCESS;
                }
            } else if (!rightShoulder.isEmpty()) {
                String entityId = rightShoulder.getString("id");
                if (entityId.equals("btc:key_golem")) {
                    player.setShoulderEntityRight(new NbtCompound());
                    for (BlockPos offsetPos : findDoors(world, pos)) {
                        setOpenNoClose(world.getBlockState(offsetPos), world, offsetPos);
                    }
                    return ItemActionResult.SUCCESS;
                }
            }
        } else if (stack.isOf(Items.TRIAL_KEY) && state.get(TYPE) == DoorType.KEYHOLE && !state.get(OPEN)) {
            stack.decrementUnlessCreative(1, player);
            for (BlockPos offsetPos : findDoors(world, pos)) {
                setOpenNoClose(world.getBlockState(offsetPos), world, offsetPos);
            }
            return ItemActionResult.SUCCESS;
        } else if (state.get(TYPE) == DoorType.NORMAL && !state.get(OPEN)) {
            for (BlockPos offsetPos : findDoors(world, pos)) {
                setOpen(world.getBlockState(offsetPos), world, offsetPos, true, 4000);
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(TYPE) == DoorType.GOLEM && !state.get(OPEN)) {
            NbtCompound leftShoulder = player.getShoulderEntityLeft();
            NbtCompound rightShoulder = player.getShoulderEntityLeft();
            if (!leftShoulder.isEmpty()) {
                String entityId = leftShoulder.getString("id");
                if (entityId.equals("btc:key_golem")) {
                    player.setShoulderEntityLeft(new NbtCompound());
                    for (BlockPos offsetPos : findDoors(world, pos)) {
                        setOpenNoClose(world.getBlockState(offsetPos), world, offsetPos);
                    }
                    return ActionResult.SUCCESS;
                }
            } else if (!rightShoulder.isEmpty()) {
                String entityId = rightShoulder.getString("id");
                if (entityId.equals("btc:key_golem")) {
                    player.setShoulderEntityRight(new NbtCompound());
                    for (BlockPos offsetPos : findDoors(world, pos)) {
                        setOpenNoClose(world.getBlockState(offsetPos), world, offsetPos);
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (state.get(TYPE) == DoorType.NORMAL && !state.get(OPEN)) {
            for (BlockPos offsetPos : findDoors(world, pos)) {
                setOpen(world.getBlockState(offsetPos), world, offsetPos, true, 4000);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
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

                    if(!found.contains(neighborPos) && neighborState.getBlock() instanceof DungeonDoorBlock &&
                            ((neighborState.get(TYPE) == originState.get(TYPE))
                                    || (((neighborState.get(TYPE) == DoorType.KEYHOLE) || (neighborState.get(TYPE) == DoorType.GOLEM)) && (originState.get(TYPE) == DoorType.LOCKED))
                                    || (((originState.get(TYPE) == DoorType.KEYHOLE) || (originState.get(TYPE) == DoorType.GOLEM)) && (neighborState.get(TYPE) == DoorType.LOCKED))
                            )) {
                        queue.add(new Pair<>(neighborPos, distance + 1));
                        found.add(neighborPos);
                    }
                }
            }
        }
        return found;
    }

    private ActionResult setOpen(BlockState state, World world, BlockPos pos, boolean open) {
        return setOpen(state, world, pos, open, null);
    }

    private ActionResult setOpen(BlockState state, World world, BlockPos pos, boolean open, @Nullable Integer delay) {
        if(state.get(OPEN) != open) {
            world.setBlockState(pos, state.with(OPEN, open));

            world.playSound(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 1.0f, 1.0f, true);

            if(open && delay != null) {
                world.emitGameEvent(GameEvent.BLOCK_OPEN, pos, GameEvent.Emitter.of(state));
                // only executed on server so no sound played on client fix me
                scheduler.schedule(() -> {
                   // world.getServer().execute(() -> {
                        BlockState currentState = world.getBlockState(pos);
                        if (currentState.getBlock() == ModBlocks.DUNGEON_DOOR && currentState.get(OPEN)) {
                            setOpen(currentState, world, pos, false);
                        }
                    //});
                }, delay, TimeUnit.MILLISECONDS);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ActionResult setOpenNoClose(BlockState state, World world, BlockPos pos) {
        if(!state.get(OPEN)) {
            world.setBlockState(pos, state.with(OPEN, true));
            world.playSound(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 1.0f, 1.0f, true);
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
        updateStateBasedOnNeighbors(state, world, pos);
    }
    private void updateStateBasedOnNeighbors(BlockState state, World world, BlockPos pos) {


//        if (!state.get(WIRED)) {
//            for (Direction direction : Direction.values()) {
//                BlockPos neighborPos = pos.offset(direction);
//                BlockState neighborState = world.getBlockState(neighborPos);
//                if (neighborState.getBlock() instanceof DungeonWireBlock) {
//                    if (neighborState.get(POWERED)) {
//                        open = true;
//                        break;
//                    } else {
//                        open = false;
//                    }
//                }
//            }
//        }
//        if (state.get(WIRED)) {
//
//
//
//            for (Direction direction : Direction.values()) {
//                BlockPos neighborPos = pos.offset(direction);
//                BlockState neighborState = world.getBlockState(neighborPos);
//
//
//
//            }
//        }
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
}