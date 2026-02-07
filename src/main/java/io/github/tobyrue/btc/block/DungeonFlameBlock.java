package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.IDungeonWireConnect;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DungeonFlameBlock extends Block implements IDungeonWireConnect {
    public static final BooleanProperty LIT = BooleanProperty.of("lit");
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape SHAPE;
    private final @Nullable ParticleEffect particle0, particle1;


    public DungeonFlameBlock(Settings settings, @Nullable ParticleEffect particle0, @Nullable ParticleEffect particle1) {
        super(settings);
        this.particle0 = particle0;
        this.particle1 = particle1;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(LIT, false));
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
                .with(LIT, false);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
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

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(8) == 0) {
            if (state.get(LIT)) {
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
            if ((particle0 != null && !state.get(LIT)) || (particle1 != null && state.get(LIT))) {
                world.addParticle(ParticleTypes.SMOKE, d2, e2, f2, 0.0, 0.0, 0.0);
            }
            if (particle0 != null && !state.get(LIT)) {
                world.addParticle(particle0, d2, e2, f2, 0.0, 0.0, 0.0);
            }
            if (particle1 != null && state.get(LIT)) {
                world.addParticle(particle1, d2, e2, f2, 0.0, 0.0, 0.0);
            }
        }
        super.randomDisplayTick(state, world, pos, random);
    }

    public static int getLuminance(BlockState currentBlockState) {
        return currentBlockState.get(LIT) ? 15 : 0;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        world.setBlockState(pos, state.with(LIT, world.getBlockState(pos.down()).getBlock() instanceof IDungeonWire wire && wire.isEmittingDungeonWirePower(world.getBlockState(pos.down()), world, pos.down(), Direction.UP)));

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public boolean shouldConnect(BlockState state, World world, BlockPos pos) {
        return true;
    }
}
