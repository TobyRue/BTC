package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.IDungeonWireConnect;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
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
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape SHAPE;
    private final Config config;

    public interface Config {
        float getDamage(final boolean powered);
        ParticleEffect getParticle(final boolean powered);
        boolean isLit(final boolean powered);
    }


    public DungeonFlameBlock(Settings settings, Config config) {
        super(settings);
        this.config = config;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(POWERED, false));
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
                .with(POWERED, false);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
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
            if (state.get(POWERED)) {
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

            if (this.config.getParticle(state.get(POWERED)) != null) {
                world.addParticle(config.getParticle(state.get(POWERED)), d2, e2, f2, 0.0, 0.0, 0.0);
            }
            if (this.config.isLit(state.get(POWERED))) {
                world.addParticle(ParticleTypes.SMOKE, d2, e2, f2, 0.0, 0.0, 0.0);
            }
        }
        super.randomDisplayTick(state, world, pos, random);
    }

    public static int getLuminance(BlockState currentBlockState) {
        return currentBlockState.get(POWERED) ? 15 : 0;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        world.setBlockState(pos, state.with(POWERED,
                IDungeonWire.isReceivingDungeonWirePower(state, world, pos, Direction.DOWN)));

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (config.isLit(state.get(POWERED))) {
            if (!entity.isFireImmune()) {
                entity.setFireTicks(entity.getFireTicks() + 1);
                if (entity.getFireTicks() == 0) {
                    entity.setOnFireFor(8.0F);
                }
            }

            entity.damage(world.getDamageSources().inFire(), config.getDamage(state.get(POWERED)));
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public boolean shouldConnect(BlockState state, World world, BlockPos pos) {
        return true;
    }
}
