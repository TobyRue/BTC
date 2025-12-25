package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.KeyDispenserBlockEntity;
import io.github.tobyrue.btc.wires.IDungeonWirePowered;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static io.github.tobyrue.btc.block.DungeonWireBlock.POWERED;

public class KeyDispenserBlock extends Block implements ModBlockEntityProvider<KeyDispenserBlockEntity>, IDungeonWirePowered {
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape TOP_MIDDLE_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape BOTTOM_SHAPE1;

    private static final VoxelShape SHAPE;

    public static final BooleanProperty ALWAYS_ACCEPTABLE  = BooleanProperty.of("always_acceptable");
    public static final BooleanProperty CHECK_MOBS  = BooleanProperty.of("check_mobs");
    public static final BooleanProperty LOOKS_DOWN  = BooleanProperty.of("looks_down");
    public static final IntProperty WIDTH  = IntProperty.of("radius", 1, 16);

    public KeyDispenserBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ALWAYS_ACCEPTABLE, false).with(CHECK_MOBS, false).with(LOOKS_DOWN, false).with(WIDTH, 10));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ALWAYS_ACCEPTABLE);
        builder.add(CHECK_MOBS);
        builder.add(LOOKS_DOWN);
        builder.add(WIDTH);
    }

    static {
        BOTTOM_SHAPE1 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
        BOTTOM_SHAPE = Block.createCuboidShape(2.0, 1.0, 2.0, 14.0, 2.0, 14.0);
        TOP_MIDDLE_SHAPE = Block.createCuboidShape(3.0, 2.0, 3.0, 13.0, 5.0, 13.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 5.0, 4.0, 12.0, 9.0, 12.0);
        BOTTOM_MIDDLE_SHAPE = Block.createCuboidShape(3.0, 9.0, 3.0, 13.0, 12.0, 13.0);
        TOP_SHAPE = Block.createCuboidShape(2.0, 12.0, 2.0, 14.0, 14.0, 14.0);

        SHAPE = VoxelShapes.union(BOTTOM_SHAPE1, BOTTOM_SHAPE, TOP_MIDDLE_SHAPE, MIDDLE_SHAPE, BOTTOM_MIDDLE_SHAPE, TOP_SHAPE);
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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return world.getBlockEntity(pos, ModBlockEntities.KEY_DISPENSER_ENTITY).get().onUse(state, world, pos, player, hit);
    }


    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i;
        double d2;
        double e2;
        double f2;
        for(i = 0; i < 3; ++i) {
            d2 = (double)pos.getX() + random.nextDouble() * 0.35 + 0.35;
            e2 = (double)pos.getY() + random.nextDouble() * 0.5 + 0.9;
            f2 = (double)pos.getZ() + random.nextDouble() * 0.35 + 0.35;
            if (shouldWirePower(state, world, pos, false, true, false) || state.get(ALWAYS_ACCEPTABLE)) {
                world.addParticle(ParticleTypes.ENCHANTED_HIT, d2, e2, f2, 0.0, 0.0, 0.0);
            }
        }
        super.randomDisplayTick(state, world, pos, random);
    }

    @Override
    public BlockEntityType<KeyDispenserBlockEntity> getBlockEntityType() {
        return ModBlockEntities.KEY_DISPENSER_ENTITY;
    }
}
