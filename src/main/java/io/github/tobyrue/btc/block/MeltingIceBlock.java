package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MeltingIceBlock extends TranslucentBlock {
    public static final MapCodec<FrostedIceBlock> CODEC = createCodec(FrostedIceBlock::new);
    public static final IntProperty AGE;
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty EAST  = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST  = BooleanProperty.of("west");
    public static final BooleanProperty UP    = BooleanProperty.of("up");
    public static final BooleanProperty DOWN  = BooleanProperty.of("down");
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MeltingIceBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState())
                .with(AGE, 0)
                .with(NORTH, true)
                .with(EAST, true)
                .with(SOUTH, true)
                .with(WEST, true)
                .with(UP, true)
                .with(DOWN, true));
    }


    public MapCodec<FrostedIceBlock> getCodec() {
        return CODEC;
    }
    public static BlockState getMeltedState() {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.isOf(this)) {
                updateConnections(world, neighborPos, neighborState);
            }
        }
    }

        protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient && projectile instanceof ArrowEntity) {
            this.melt(state, world, hit.getBlockPos());
            world.playSoundAtBlockCenter(hit.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, 1, true);
            projectile.discard(); // Optional: destroy arrow on hit
        }
        super.onProjectileHit(world, state, hit, projectile);
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();

        // North
        if (!world.getBlockState(pos.north()).isOf(this)) {
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.0625));
        }
        // South
        if (!world.getBlockState(pos.south()).isOf(this)) {
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.9375, 1, 1, 1));
        }
        // West
        if (!world.getBlockState(pos.west()).isOf(this)) {
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.0625, 1, 1));
        }
        // East
        if (!world.getBlockState(pos.east()).isOf(this)) {
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, 0, 1, 1, 1));
        }
        // Top
        if (!world.getBlockState(pos.up()).isOf(this)) {
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.9375, 0, 1, 1, 1));
        }
        // Bottom
        if (!world.getBlockState(pos.down()).isOf(this)) {
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.0625, 1));
        }

        return shape;
    }


//    @Override
//    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        VoxelShape shape = VoxelShapes.empty();
//
//        boolean connectNorth = isSameBlock(world, pos.north());
//        boolean connectSouth = isSameBlock(world, pos.south());
//        boolean connectEast  = isSameBlock(world, pos.east());
//        boolean connectWest  = isSameBlock(world, pos.west());
//        boolean connectUp    = isSameBlock(world, pos.up());
//        boolean connectDown  = isSameBlock(world, pos.down());
//
//        // Add faces where no adjacent block exists
//        if (!connectNorth) shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.0625)); // North
//        if (!connectSouth) shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.9375, 1, 1, 1)); // South
//        if (!connectWest)  shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.0625, 1, 1)); // West
//        if (!connectEast)  shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, 0, 1, 1, 1)); // East
//        if (!connectUp)    shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.9375, 0, 1, 1, 1)); // Top
//        if (!connectDown)  shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.0625, 1)); // Bottom
//
//        return shape;
//    }

    private boolean isSameBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).isOf(this);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getOutlineShape(state, world, pos, context);
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, MathHelper.nextInt(world.getRandom(), 60, 120));
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getBlockStateAtPos().isOf(this)) {
            entity.slowMovement(state, new Vec3d(0.8999999761581421, 1.5, 0.8999999761581421));
            if (world.isClient) {
                Random random = world.getRandom();
                boolean bl = entity.lastRenderX != entity.getX() || entity.lastRenderZ != entity.getZ();
                if (bl && random.nextBoolean()) {
                    world.addParticle(ParticleTypes.SNOWFLAKE, entity.getX(), (double)(pos.getY() + 1), entity.getZ(), (double)(MathHelper.nextBetween(random, -1.0F, 1.0F) * 0.083333336F), 0.05000000074505806, (double)(MathHelper.nextBetween(random, -1.0F, 1.0F) * 0.083333336F));
                }
            }
        }
        entity.setInPowderSnow(true);
        super.onEntityCollision(state, world, pos, entity);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((random.nextInt(3) == 0 || this.canMelt(world, pos, 4)) && world.getLightLevel(pos) > 11 - (Integer)state.get(AGE) - state.getOpacity(world, pos) && this.increaseAge(state, world, pos)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Direction direction = var6[var8];
                mutable.set(pos, direction);
                BlockState blockState = world.getBlockState(mutable);
                if (blockState.isOf(this) && !this.increaseAge(blockState, world, mutable)) {
                    world.scheduleBlockTick(mutable, this, MathHelper.nextInt(random, 20, 40));
                }
            }

        } else {
            world.scheduleBlockTick(pos, this, MathHelper.nextInt(random, 20, 40));
        }
    }
    protected void melt(BlockState state, World world, BlockPos pos) {
        if (world.getDimension().ultrawarm()) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockState(pos, getMeltedState());
            world.updateNeighbor(pos, getMeltedState().getBlock(), pos);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        for (Direction dir : Direction.values()) {
            //TODO
            neighborUpdate(state, , pos.offset(dir));
        }
    }

    private boolean increaseAge(BlockState state, World world, BlockPos pos) {
        int i = (Integer)state.get(AGE);
        if (i < 3) {
            world.setBlockState(pos, (BlockState)state.with(AGE, i + 1), 2);
            return false;
        } else {
            this.melt(state, world, pos);
            return true;
        }
    }
    private void updateConnections(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        BlockState newState = state
                .with(UP, !isSameBlock(world, pos.up()))
                .with(DOWN, !isSameBlock(world, pos.down()))
                .with(NORTH, !isSameBlock(world, pos.north()))
                .with(SOUTH, !isSameBlock(world, pos.south()))
                .with(EAST, !isSameBlock(world, pos.east()))
                .with(WEST, !isSameBlock(world, pos.west()));


        world.setBlockState(pos, newState, 3);
    }


    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof MeltingIceBlock) {
                updateConnections(world, neighborPos, neighborState);
                scheduler.schedule(() -> {
                    updateConnections(world, neighborPos, neighborState);
                }, 20, TimeUnit.MILLISECONDS);
            }
        }
        if (sourceBlock.getDefaultState().isOf(this) && state.get(AGE) > 3) {
            this.melt(state, world, pos);
        }

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private boolean canMelt(BlockView world, BlockPos pos, int maxNeighbors) {
        int i = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Direction[] var6 = Direction.values();
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction direction = var6[var8];
            mutable.set(pos, direction);
            if (world.getBlockState(mutable).isOf(this)) {
                ++i;
                if (i >= maxNeighbors) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
        builder.add(UP);
        builder.add(DOWN);
        builder.add(NORTH);
        builder.add(EAST);
        builder.add(SOUTH);
        builder.add(WEST);
    }

    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    static {
        AGE = Properties.AGE_3;
    }

}
