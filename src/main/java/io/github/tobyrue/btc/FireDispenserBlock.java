package io.github.tobyrue.btc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static io.github.tobyrue.btc.DungeonWireBlock.POWERED;

public class FireDispenserBlock extends Block {
    public static final EnumProperty<FireDispenserType> FIRE_DISPENSER_TYPE = EnumProperty.of("fire_dispenser_type", FireDispenserType.class);
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_SHAPE;

    private static final VoxelShape SHAPE;

    public FireDispenserBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FIRE_DISPENSER_TYPE, FireDispenserType.NO_FIRE));
    }
    static {
        BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
        MIDDLE_SHAPE = Block.createCuboidShape(2.0, 1.0, 2.0, 14.0, 2.0, 14.0);
        TOP_SHAPE = Block.createCuboidShape(0.0, 2.0, 0.0, 16.0, 3.0, 16.0);
        SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MIDDLE_SHAPE, TOP_SHAPE);
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FIRE_DISPENSER_TYPE, FireDispenserType.NO_FIRE);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FIRE_DISPENSER_TYPE);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }
    /** Make a Block Entity and change method FireSwich to ticker*/
    /**
    public void FireSwich(BlockState state, World world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof DungeonWireBlock && neighborState.get(POWERED)) {
                BlockState newState1 = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.TALL_FIRE);
                world.setBlockState(pos, newState1);
            } else if(neighborState.getBlock() instanceof DungeonWireBlock && !neighborState.get(POWERED)) {
                BlockState newState2 = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.SHORT_FIRE);
                world.setBlockState(pos, newState2);
            }
        }
    }
     */
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(8) == 0) {
            if (!(state.get(FireDispenserBlock.FIRE_DISPENSER_TYPE) == FireDispenserType.NO_FIRE)) {
                world.playSound((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 2.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }
        }

        int i;
        double d2;
        double e2;
        double f2;
        for(i = 0; i < 3; ++i) {
            d2 = (double)pos.getX() + random.nextDouble() * 0.35 + 0.35;
            e2 = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
            f2 = (double)pos.getZ() + random.nextDouble() * 0.35 + 0.35;
            world.addParticle(ParticleTypes.SMOKE, d2, e2, f2, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d2, e2, f2, 0.0, 0.0, 0.0);

        }
        super.randomDisplayTick(state, world, pos, random);
    }
}
