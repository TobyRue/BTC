package io.github.tobyrue.btc.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GunpowderDustBlock extends Block {
    public static final BooleanProperty BURNING = BooleanProperty.of("burning");
    public static final IntProperty FUSE = IntProperty.of("fuse", 0, 5);

    public static final EnumProperty<WireConnection> NORTH = Properties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> EAST = Properties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WEST = Properties.WEST_WIRE_CONNECTION;

    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_PROPERTIES = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST
    ));

    private static final VoxelShape DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
            Direction.SOUTH, Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
            Direction.EAST, Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
            Direction.WEST, Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));

    private static final Map<Direction, VoxelShape> DIRECTION_TO_UP_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.NORTH), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
            Direction.SOUTH, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.SOUTH), Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
            Direction.EAST, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.EAST), Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
            Direction.WEST, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.WEST), Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));

    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    public GunpowderDustBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, WireConnection.NONE).with(EAST, WireConnection.NONE)
                .with(SOUTH, WireConnection.NONE).with(WEST, WireConnection.NONE)
                .with(BURNING, false).with(FUSE, 5));

        for (BlockState blockState : this.getStateManager().getStates()) {
            if (!blockState.get(BURNING) && blockState.get(FUSE) == 5) {
                SHAPES.put(blockState, this.getShapeForState(blockState));
            }
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return Items.GUNPOWDER.getDefaultStack();
    }

    private void updateTargettedNeighbor(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.isOf(this)) {

            BlockState newState = this.getConnectionState(world, state, pos);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        }
    }
    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_PROPERTIES.get(direction));
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape, DIRECTION_TO_SIDE_SHAPE.get(direction));
            } else if (wireConnection == WireConnection.UP) {
                voxelShape = VoxelShapes.union(voxelShape, DIRECTION_TO_UP_SHAPE.get(direction));
            }
        }
        return voxelShape;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.with(BURNING, false).with(FUSE, 5));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getConnectionState(ctx.getWorld(), this.getDefaultState(), ctx.getBlockPos());
    }
    @Override
    protected BlockState rotate(BlockState state, net.minecraft.util.BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 -> {
                return state.with(NORTH, state.get(SOUTH))
                        .with(EAST, state.get(WEST))
                        .with(SOUTH, state.get(NORTH))
                        .with(WEST, state.get(EAST));
            }
            case COUNTERCLOCKWISE_90 -> {
                return state.with(NORTH, state.get(EAST))
                        .with(EAST, state.get(SOUTH))
                        .with(SOUTH, state.get(WEST))
                        .with(WEST, state.get(NORTH));
            }
            case CLOCKWISE_90 -> {
                return state.with(NORTH, state.get(WEST))
                        .with(EAST, state.get(NORTH))
                        .with(SOUTH, state.get(EAST))
                        .with(WEST, state.get(SOUTH));
            }
            default -> {
                return state;
            }
        }
    }

    @Override
    protected BlockState mirror(BlockState state, net.minecraft.util.BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT -> {
                return state.with(NORTH, state.get(SOUTH))
                        .with(SOUTH, state.get(NORTH));
            }
            case FRONT_BACK -> {
                return state.with(EAST, state.get(WEST))
                        .with(WEST, state.get(EAST));
            }
            default -> {
                return super.mirror(state, mirror);
            }
        }
    }
    public BlockState getConnectionState(BlockView world, BlockState state, BlockPos pos) {
        state = this.getUpdatedState(world, state, pos);

        if (isNotConnected(state)) {
            return state;
        }

        boolean n = state.get(NORTH).isConnected();
        boolean s = state.get(SOUTH).isConnected();
        boolean e = state.get(EAST).isConnected();
        boolean w = state.get(WEST).isConnected();

        boolean ns = !n && !s;
        boolean ew = !e && !w;
        if (!w && ns) state = state.with(WEST, WireConnection.SIDE);
        if (!e && ns) state = state.with(EAST, WireConnection.SIDE);
        if (!n && ew) state = state.with(NORTH, WireConnection.SIDE);
        if (!s && ew) state = state.with(SOUTH, WireConnection.SIDE);

        return state;
    }

    private static boolean isNotConnected(BlockState state) {
        if (!(state.getBlock() instanceof GunpowderDustBlock)) return true;
        return !state.get(NORTH).isConnected() &&
                !state.get(EAST).isConnected() &&
                !state.get(SOUTH).isConnected() &&
                !state.get(WEST).isConnected();
    }

    private BlockState getUpdatedState(BlockView world, BlockState state, BlockPos pos) {
        boolean canUp = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection connection = this.getRenderConnectionType(world, pos, direction, canUp);
            state = state.with(DIRECTION_PROPERTIES.get(direction), connection);
        }
        return state;
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean canUp) {
        BlockPos neighborPos = pos.offset(direction);
        BlockState neighborState = world.getBlockState(neighborPos);

        if (canUp) {
            BlockPos aboveNeighborPos = neighborPos.up();
            BlockState aboveNeighborState = world.getBlockState(aboveNeighborPos);
            if (connectsTo(aboveNeighborState)) {
                if (neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite())) {
                    return WireConnection.UP;
                }
                return WireConnection.SIDE;
            }
        }

        if (connectsTo(neighborState, direction)) {
            return WireConnection.SIDE;
        }

        if (!neighborState.isSolidBlock(world, neighborPos) && connectsTo(world.getBlockState(neighborPos.down()))) {
            return WireConnection.SIDE;
        }

        return WireConnection.NONE;
    }



    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }
    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            if (!this.canPlaceAt(state, world, pos)) {
                return Blocks.AIR.getDefaultState();
            }
        }

        return this.getConnectionState(world, state, pos);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {

        if (!state.get(BURNING)) {
            if (world.isReceivingRedstonePower(pos)) {
                ignite(world, pos, state);
            }
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(BURNING)) return;

        int fuse = state.get(FUSE);
        spreadFire(world, pos);

        if (fuse > 0) {
            world.setBlockState(pos, state.with(FUSE, fuse - 1), Block.NOTIFY_ALL);
            world.scheduleBlockTick(pos, this, 10);
        } else {
            this.inspectConnections(world, pos, state);
            world.removeBlock(pos, false);
            this.onBurnOut(world, pos);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.getStackInHand(Hand.MAIN_HAND).isOf(Items.FLINT_AND_STEEL)) {
            ignite(world, pos, state);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(BURNING)) {
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            double e = (double)pos.getY() + 0.15;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
            if (random.nextInt(3) == 0) {
                world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient) {
            this.updateAllNeighbors(world, pos);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if (!world.isClient) {
                this.updateAllNeighbors(world, pos);
            }
        }
    }

    private void updateAllNeighbors(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.isOf(this)) {
            world.setBlockState(pos, this.getConnectionState(world, state, pos), Block.NOTIFY_ALL);
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos neighborPos = pos.offset(direction);

            updateTargettedNeighbor(world, neighborPos);
            updateTargettedNeighbor(world, neighborPos.up());
            updateTargettedNeighbor(world, neighborPos.down());
        }
    }
    public int getColor(BlockState state) {
        if (!state.get(BURNING)) {
            return 0x777777;
        }

        int fuse = state.get(FUSE);
        float factor = 1.0f - (fuse / 5.0f);

        int r = (int) (139 + (116 * factor));
        int g = (int) (64 + (101 * factor));
        int b = 0;

        return (r << 16) | (g << 8) | b;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, BURNING, FUSE);
    }

    /**
     * Scans all current connections and identifies the blocks they lead to.
     */
    public void inspectConnections(World world, BlockPos pos, BlockState state) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection connection = state.get(DIRECTION_PROPERTIES.get(direction));
            if (connection == WireConnection.NONE) continue;

            BlockPos targetPos = pos.offset(direction);
            if (connection == WireConnection.UP) {
                targetPos = targetPos.up();
            }

            BlockState targetState = world.getBlockState(targetPos);

            handleInteraction(world, targetPos, targetState);
        }
        handleInteraction(world, pos.down(), world.getBlockState(pos.down()));
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        } else {
            ignite(world, pos, state);
            Item item = stack.getItem();
            if (stack.isOf(Items.FLINT_AND_STEEL)) {
                stack.damage(1, player, LivingEntity.getSlotForHand(hand));
            } else {
                stack.decrementUnlessCreative(1, player);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(item));
            return ItemActionResult.success(world.isClient);
        }
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient) {
            BlockPos blockPos = hit.getBlockPos();
            if (projectile.isOnFire() && projectile.canModifyAt(world, blockPos)) {
                ignite(world, blockPos, state);
            }
        }
    }

    private void handleInteraction(World world, BlockPos targetPos, BlockState targetState) {
        if (targetState.isOf(Blocks.TNT)) {
            TntBlock.primeTnt(world, targetPos);
            world.removeBlock(targetPos, false);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState floorState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, floorState);
    }

    private boolean isBurning(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isOf(this) && state.get(BURNING);
    }

    protected static boolean connectsTo(BlockState state) {
        return connectsTo(state, null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (state.isOf(ModBlocks.GUNPOWDER_DUST)) {
            return true;
        }
        if (state.isOf(Blocks.TNT)) {
            return true;
        }
        return false;
    }

    public void ignite(World world, BlockPos pos, BlockState state) {
        if (!state.get(BURNING)) {
            world.setBlockState(pos, state.with(BURNING, true), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.5f);
            world.scheduleBlockTick(pos, this, 10);
        }
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        BlockState initialState = ModBlocks.GUNPOWDER_DUST.getDefaultState();

        if (canPlaceAt(initialState, world, pos)) {

            world.setBlockState(pos, initialState, Block.NOTIFY_ALL);

            if (!world.isClient) {
                this.updateAllNeighbors(world, pos);
            }
            BlockState finalState = world.getBlockState(pos);
            ignite(world, pos, finalState);
        }

        super.onDestroyedByExplosion(world, pos, explosion);
    }


    private void spreadFire(World world, BlockPos pos) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos neighborPos = pos.offset(dir);

            checkAndIgnite(world, neighborPos);
            checkAndIgnite(world, neighborPos.up());
            checkAndIgnite(world, neighborPos.down());
        }

        checkAndIgnite(world, pos.up());
        checkAndIgnite(world, pos.down());
    }
    private void checkAndIgnite(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.isOf(this)) {
            ignite(world, pos, state);
        }
    }

    protected void onBurnOut(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.5f, 2.0f);

        BlockPos floorPos = pos.down();
        BlockState floorState = world.getBlockState(floorPos);

        if (floorState.isOf(Blocks.NETHERRACK) ||
                floorState.isOf(Blocks.SOUL_SAND) || floorState.isOf(Blocks.SOUL_SOIL)) {

            BlockState fireState = floorState.isOf(Blocks.SOUL_SAND) || floorState.isOf(Blocks.SOUL_SOIL)
                    ? Blocks.SOUL_FIRE.getDefaultState()
                    : Blocks.FIRE.getDefaultState();

            if (world.isAir(pos)) {
                world.setBlockState(pos, fireState);
            }
        }
    }

}