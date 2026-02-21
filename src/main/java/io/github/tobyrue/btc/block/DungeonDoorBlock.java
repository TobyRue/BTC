package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.ICopperWireConnect;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.wires.IDungeonWireAction;
import io.github.tobyrue.btc.IDungeonWireConnect;
import net.minecraft.block.*;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
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

public class DungeonDoorBlock extends Block implements IDungeonWireAction, IDungeonWireConnect, ICopperWireConnect {
    public static final EnumProperty<DoorType> TYPE = EnumProperty.of("door_type", DoorType.class);
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty SURVIVAL = BooleanProperty.of("survival");

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
                .with(SURVIVAL, true)
                .with(TYPE, DoorType.NORMAL));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(OPEN, false)
                .with(SURVIVAL, true)
                .with(TYPE, DoorType.NORMAL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
        builder.add(SURVIVAL);
        builder.add(TYPE);
    }
//    @Override
//    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
//        super.neighborUpdate(state, world, pos, block, fromPos, notify);
//
//        System.out.println("Neighbor update triggered at: " + pos + " due to block at: " + fromPos);
//
//        // Update state based on neighbors (if you have any extra logic here)
//        updateStateBasedOnNeighbors(state, world, pos);
//
//        // Get all surrounding wire positions
//        List<BlockPos> wirePositions = getSurroundingWirePositions(world, pos);
//        System.out.println("Surrounding wire positions: " + wirePositions);
//
//        // Check if the updated position was one of the expected wire positions
//        boolean listsMatch = wirePositions.contains(fromPos);
//        System.out.println("Was fromPos one of the expected wire positions? " + listsMatch);
//
//        // Get the state of the block that updated
//        BlockState fromState = world.getBlockState(fromPos);
//        System.out.println("Block state at fromPos: " + fromState);
//
//        // Check if it’s no longer a DungeonWireBlock, and if the door is open, and if it was a relevant position
//        if (!(fromState.getBlock() instanceof DungeonWireBlock) && !listsMatch && !(fromState.getBlock() instanceof DungeonDoorBlock)) {
//            System.out.println("Conditions met to close connected doors. Closing doors...");
//
//            for (BlockPos offsetPos : findDoors(world, pos)) {
//                System.out.println("Closing door at: " + offsetPos);
//                setOpen(world.getBlockState(offsetPos), world, offsetPos, false);
//            }
//        } else {
//            System.out.println("No door state change needed.");
//        }
//    }
//    @Override
//    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
//        super.neighborUpdate(state, world, pos, block, fromPos, notify);
//        List<BlockPos> wirePositions = getSurroundingWirePositions(world, pos);
//        System.out.println("Surrounding wire positions: " + wirePositions);
//        boolean listsMatch = wirePositions.contains(fromPos);
//        System.out.println("Was fromPos one of the expected wire positions? " + !listsMatch);
//        BlockState fromState = world.getBlockState(fromPos);
//        System.out.println("Block state at fromPos: " + fromState);
//    }
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
//        if (!state.get(OPEN) && stack.getItem() == ModItems.IRON_WRENCH && state.get(SURVIVAL)) {
//            var currentState = state.get(TYPE);
//            BlockState newState = state.with(TYPE, TYPE.);
//            String message = "Wires Powering Doors has been: " + (!currentState ? "enabled" : "disabled");
//            player.sendMessage(Text.literal(message), true);
//            world.setBlockState(pos, newState, Block.NOTIFY_NEIGHBORS | Block.NOTIFY_LISTENERS);
//            return ItemActionResult.SUCCESS;
//        }
//            else if(!state.get(WIRED) && !state.get(OPEN) && stack.getItem() != ModItems.GOLD_WRENCH && stack.getItem() != ModItems.IRON_WRENCH && stack.getItem() != ModItems.CREATIVE_WRENCH) {
//            world.emitGameEvent(player, GameEvent.ENTITY_INTERACT, pos);
//            for (BlockPos offsetPos : findDoors(world, pos)) {
//                setOpen(world.getBlockState(offsetPos), world, offsetPos, true, 4000);
//            }
//            return ItemActionResult.SUCCESS;
//        }
        return ItemActionResult.FAIL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        System.out.println("Type: " + state.get(TYPE));
        if (state.get(TYPE) == DoorType.GOLEM && !state.get(OPEN)) {
            System.out.println("Left Shoulder: " + player.getShoulderEntityLeft() + " ID: " + player.getShoulderEntityLeft().getString("id"));
            System.out.println("Right Shoulder: " + player.getShoulderEntityRight() + " ID: " + player.getShoulderEntityRight().getString("id"));
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

//    @Override
//    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
////        if(!state.get(WIRED)) {
//            //return open(state, world, pos);
////        }
//        if(!state.get(WIRED) && !state.get(OPEN)) {
//            for (BlockPos offsetPos : findDoors(world, pos)) {
//                setOpen(world.getBlockState(offsetPos), world, offsetPos, true, 4000);
//            }
//            return ActionResult.SUCCESS;
//        }
//
//        return ActionResult.FAIL;
//                //super.onUse(state, world, pos, player, hit);
//    }


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
//                        break; // No need to check further, as we only need one powered neighbor.
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

        // Propagate the open state to adjacent blocks if it's being opened.

        // Update the current block's state.
    }

//    @Override
//    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
//        if(state.get(WIRED)) {
////            System.out.println("Pos is:" + pos + ", Offset block is: " + offset);
//            for (BlockPos offsetPos : findDoors(world, pos)) {
//                setOpen(world.getBlockState(offsetPos), world, offsetPos, shouldWirePower(state, world, pos, true, true, true));
//            }
//        }
//        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
//    }


    @Override
    public void onDungeonWireChange(BlockState state, World world, BlockPos pos, boolean powered) {
        if(state.get(TYPE) == DoorType.WIRED) {
//            System.out.println("Pos is:" + pos + ", Offset block is: " + offset);
            for (BlockPos offsetPos : findDoors(world, pos)) {
                setOpen(world.getBlockState(offsetPos), world, offsetPos, powered);
            }
        }
    }

    @Override
    public boolean shouldConnect(BlockState state, World world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean shouldCopperConnect(BlockState state, World world, BlockPos pos) {
        //TODO
        return true;
    }
}