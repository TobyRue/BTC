package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.ICopperWireConnect;
import io.github.tobyrue.btc.IDungeonWireConnect;
import io.github.tobyrue.btc.enums.Connection;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static io.github.tobyrue.btc.block.DungeonWireBlock.*;

public class CopperWireBlock extends Block implements IDungeonWireConnect, ICopperWireConnect {
    public static final MapCodec<CopperWireBlock> CODEC = createCodec(CopperWireBlock::new);

    public static final DirectionProperty FACING = Properties.FACING;

    public static final BooleanProperty FACING_UP = BooleanProperty.of("up");
    public static final BooleanProperty FACING_DOWN = BooleanProperty.of("down");
    public static final BooleanProperty FACING_LEFT = BooleanProperty.of("left");
    public static final BooleanProperty FACING_RIGHT = BooleanProperty.of("right");

    public static final BooleanProperty ROOT1 = BooleanProperty.of("root");
    public static final EnumProperty<Connection> CONNECTION1 = EnumProperty.of("connection", Connection.class);

    public static final BooleanProperty POWERED1 = BooleanProperty.of("powered");

    public static final BooleanProperty POWERABLE_BY_REDSTONE = BooleanProperty.of("powerable_by_redstone");
    public static final BooleanProperty SURVIVAL = BooleanProperty.of("survival");

    public CopperWireBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState()
                .with(SURVIVAL, true)
                .with(ROOT1, false)
                .with(POWERABLE_BY_REDSTONE, true)
                .with(FACING_DOWN, false)
                .with(FACING_UP, false)
                .with(FACING_RIGHT, false)
                .with(FACING_LEFT, false)
                .with(FACING, Direction.NORTH)
                .with(CONNECTION1, Connection.NONE)
                .with(POWERED1, false)
        );
    }

    @Override
    public MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();

        BlockState placementState = this.getDefaultState()
                .with(FACING, ctx.getSide());

        placementState = updateFacingState(placementState, world, blockPos);
        Connection parent = findConnectionParent(placementState, world, blockPos);
        placementState = placementState.with(CONNECTION1, parent);
        placementState = updatePowered(placementState, world, blockPos);

        return placementState;
    }
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
                SURVIVAL,
                CONNECTION1,
                FACING_DOWN,
                FACING_LEFT,
                FACING_RIGHT,
                FACING_UP,
                FACING,
                POWERED1,
                POWERABLE_BY_REDSTONE,
                ROOT1
        );
    }

    private BlockState updateFacingState(BlockState blockState, World world, BlockPos blockPos) {
        BlockState upState = world.getBlockState(blockPos.offset(Direction.UP));
        BlockState downState = world.getBlockState(blockPos.offset(Direction.DOWN));
        BlockState northState = world.getBlockState(blockPos.offset(Direction.NORTH));
        BlockState eastState = world.getBlockState(blockPos.offset(Direction.EAST));
        BlockState southState = world.getBlockState(blockPos.offset(Direction.SOUTH));
        BlockState westState = world.getBlockState(blockPos.offset(Direction.WEST));
        boolean up = world.getBlockState(blockPos.offset(Direction.UP)).getBlock() instanceof ICopperWireConnect connects && connects.shouldCopperConnect(upState, world, blockPos);
        boolean down = world.getBlockState(blockPos.offset(Direction.DOWN)).getBlock() instanceof ICopperWireConnect connects && connects.shouldCopperConnect(downState, world, blockPos);
        boolean north = world.getBlockState(blockPos.offset(Direction.NORTH)).getBlock() instanceof ICopperWireConnect connects && connects.shouldCopperConnect(northState, world, blockPos);
        boolean east = world.getBlockState(blockPos.offset(Direction.EAST)).getBlock() instanceof ICopperWireConnect connects && connects.shouldCopperConnect(eastState, world, blockPos);
        boolean south = world.getBlockState(blockPos.offset(Direction.SOUTH)).getBlock() instanceof ICopperWireConnect connects && connects.shouldCopperConnect(southState, world, blockPos);
        boolean west = world.getBlockState(blockPos.offset(Direction.WEST)).getBlock() instanceof ICopperWireConnect connects && connects.shouldCopperConnect(westState, world, blockPos);


        if(!up && !down && !north && !east && !south && !west) {
            return blockState;
        }

        Direction facing = blockState.get(FACING);

        if (facing == Direction.UP) {
            return blockState
                    .with(FACING_UP, south)
                    .with(FACING_DOWN, north)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        } else if (facing == Direction.DOWN) {
            return blockState
                    .with(FACING_UP, north)
                    .with(FACING_DOWN, south)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        } else if (facing == Direction.NORTH) {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        } else if (facing == Direction.EAST) {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, south)
                    .with(FACING_RIGHT, north);
        } else if(facing == Direction.SOUTH) {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, west)
                    .with(FACING_RIGHT, east);
        } else {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, north)
                    .with(FACING_RIGHT, south);
        }
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     * @remarks Only finds blocks of the same class.
     */
    private Connection findConnectionParent(BlockState blockState, World world, BlockPos blockPos) {
        Connection poweredTarget = Connection.NONE;
        Connection unpoweredTarget = Connection.NONE;

        for(Direction direction: Direction.values()) {
            BlockState other = world.getBlockState(blockPos.offset(direction));
            if(!(other.isOf(this) || other.getBlock() instanceof DungeonWireBlock)) {
                continue;
            }

            if(other.contains(ROOT) && other.get(ROOT)) {
                return Connection.of(direction);
            } else if(other.contains(ROOT1) && other.get(ROOT1)) {
                return Connection.of(direction);
            }


            if(other.contains(CONNECTION) && other.contains(POWERED)) {

                Connection parent = other.get(CONNECTION);
                if(parent != Connection.NONE) {
                    if((parent.asDirection().getOpposite() == direction)) {
                        continue;
                    }

                    if(other.get(POWERED)) {
                        if(poweredTarget == Connection.NONE) {
                            poweredTarget = Connection.of(direction);
                        }
                    } else {
                        if(unpoweredTarget == Connection.NONE) {
                            unpoweredTarget = Connection.of(direction);
                        }
                    }
                }
            } else if(other.contains(CONNECTION1) && other.contains(POWERED1)) {
                Connection parent1 = other.get(CONNECTION1);
                if(parent1 != Connection.NONE) {
                    if((parent1.asDirection().getOpposite() == direction)) {
                        continue;
                    }
                    if(other.get(POWERED1)) {
                        if (poweredTarget == Connection.NONE) {
                            poweredTarget = Connection.of(direction);
                        }
                    } else {
                        if(unpoweredTarget == Connection.NONE) {
                            unpoweredTarget = Connection.of(direction);
                        }
                    }
                }
            }
        }
        if(poweredTarget != Connection.NONE) {
            return poweredTarget;
        }
        if(unpoweredTarget != Connection.NONE) {
            return unpoweredTarget;
        }
        return Connection.NONE;
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     */
    private boolean isValidConnectionParent(BlockState blockState, World world, BlockPos blockPos) {
        Connection parent = blockState.get(CONNECTION1);
        if(parent != Connection.NONE) {
            BlockState other = world.getBlockState(blockPos.offset(parent.asDirection()));
            if(other.isOf(this) && other.get(CONNECTION1) != Connection.NONE) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     */
    private BlockState updateConnectionParent(BlockState blockState, World world, BlockPos blockPos) {
        if(isValidConnectionParent(blockState, world, blockPos)) {
            return blockState;
        }
        Connection parent = findConnectionParent(blockState, world, blockPos);
        return blockState.with(CONNECTION1, parent);
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     */
    private BlockState updatePowered(BlockState blockState, World world, BlockPos blockPos) {
        if(blockState.get(ROOT1)) {
            return blockState.with(POWERED1, true);
        }

        if(blockState.get(CONNECTION1) == Connection.NONE) {
            return blockState.with(POWERED1, false);
        }
        Connection parent = blockState.get(CONNECTION1);
        BlockState other = world.getBlockState(blockPos.offset(parent.asDirection()));
        Connection parent1 = blockState.get(CONNECTION1);
        BlockState other1 = world.getBlockState(blockPos.offset(parent1.asDirection()));

        if(other.getBlock() instanceof DungeonWireBlock && other.get(POWERED)) {
            return blockState.with(POWERED1, true);
        }

        if(other1.isOf(this) && other.contains(POWERED1)) {
            return blockState.with(POWERED1, true);
        }
        return blockState.with(POWERED1, false);
    }


    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(blockState, world, blockPos, sourceBlock, sourcePos, notify);
//        System.out.println("Is receiving redstone on Client: " + world.isReceivingRedstonePower(blockPos));

        if(!world.isClient) {
            BlockState newState = blockState;
            newState = updateFacingState(newState, world, blockPos);
            newState = updateConnectionParent(newState, world, blockPos);
            newState = updatePowered(newState, world, blockPos);

            if(!blockState.equals(newState)) {
                world.setBlockState(blockPos, newState, (NOTIFY_NEIGHBORS | NOTIFY_LISTENERS));
            }
            BlockState sourceState = world.getBlockState(sourcePos);
//            System.out.println("Is receiving redstone on Server: " + world.isReceivingRedstonePower(blockPos));
            if(blockState.get(POWERABLE_BY_REDSTONE) && !(sourceState.getBlock() instanceof CopperWireBlock) && !(sourceState.getBlock() instanceof DungeonWireBlock)) {
                BlockState newConnectionState = blockState.with(CONNECTION1, Connection.NONE);
//                world.setBlockState(blockPos, newConnectionState, (Block.NOTIFY_NEIGHBORS | Block.NOTIFY_LISTENERS));
                BlockState newRootStateTrue = blockState.with(ROOT1, true);
                BlockState newRootStateFalse = blockState.with(ROOT1, false);
//                world.setBlockState(blockPos, newRootState, (Block.NOTIFY_NEIGHBORS | Block.NOTIFY_LISTENERS));
                boolean root = blockState.get(ROOT1);
                if (root != world.isReceivingRedstonePower(blockPos)) {
                    if (!root) {
                        world.setBlockState(blockPos, newConnectionState);
                        world.setBlockState(blockPos, blockState.cycle(POWERED1), 3);
                    }
                    world.setBlockState(blockPos, blockState.cycle(ROOT1), 3);
                }
            }
//            boolean root = blockState.get(ROOT1);
//            if(blockState.get(POWERABLE_BY_REDSTONE)) {
//                if (root != world.isReceivingRedstonePower(blockPos)) {
//                    System.out.println("Root value does not equal redstone value");
//                    if (root) {
//                        world.setBlockState(blockPos, newConnectionState);
//                        world.scheduleBlockTick(blockPos, this, 10);
//                        System.out.println("Root is on but redstone is not");
//                    } else {
//                        world.setBlockState(blockPos, blockState.cycle(ROOT1));
//                        world.scheduleBlockTick(blockPos, this, 10);
//                        System.out.println("Root is off but redstone is on");
//                    }
//                }
//            }
        }
    }


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.updateNeighbors(pos, this);
    }
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(POWERED1) ? 15 : 0;
    }



    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Check if the player is holding the wrench
        ItemStack heldItem = player.getStackInHand(hand);
        if ((heldItem.isOf(ModItems.IRON_WRENCH) || heldItem.isOf(ModItems.GOLD_WRENCH)) && state.get(SURVIVAL)) {
            if (!world.isClient) {
                // Toggle the POWERABLE_BY_REDSTONE property
                boolean currentState = state.get(POWERABLE_BY_REDSTONE);
                BlockState newState = state.with(POWERABLE_BY_REDSTONE, !currentState);
                world.setBlockState(pos, newState, Block.NOTIFY_ALL);

                // Send a message to the player
                String message = "Powerable by redstone: " + (!currentState ? "enabled" : "disabled");
                player.sendMessage(Text.literal(message), true);

                // Optionally, play a sound
                world.playSound(null, pos, SoundEvents.BLOCK_METAL_HIT, SoundCategory.BLOCKS, 8.0F, 1.0F);
            }
            return ItemActionResult.SUCCESS;
        } else if (stack.getItem() == ModItems.CREATIVE_WRENCH && player.isCreative() && !player.isSneaking()) {
            boolean currentState = state.get(ROOT1);
            BlockState newState = state.with(ROOT1, !state.get(ROOT1));
            String message = "Survival friendly: " + (!currentState ? "enabled" : "disabled");
            player.sendMessage(Text.literal(message), true);
            world.setBlockState(pos, newState, Block.NOTIFY_NEIGHBORS | Block.NOTIFY_LISTENERS);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    public static int getLuminance(BlockState currentBlockState) {
        // Get the value of the "activated" property.
        boolean activated = currentBlockState.get(CopperWireBlock.POWERED1);

        // Return a light level if activated = true
        return activated ? 15 : 0;
    }

    @Override
    public boolean shouldConnect(BlockState state, World world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean shouldCopperConnect(BlockState state, World world, BlockPos pos) {
        return true;
    }
}
