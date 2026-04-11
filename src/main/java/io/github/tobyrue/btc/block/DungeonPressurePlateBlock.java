package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.entity.custom.TrialCubeEntity;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DungeonPressurePlateBlock extends AbstractPressurePlateBlock implements IDungeonWire {
    private static final VoxelShape SHAPE;

    private static final VoxelShape DOWN_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public static final MapCodec<DungeonPressurePlateBlock> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    createSettingsCodec()
            ).apply(instance, DungeonPressurePlateBlock::new));

    @Override
    protected MapCodec<? extends AbstractPressurePlateBlock> getCodec() {
        return CODEC;
    }

    public DungeonPressurePlateBlock(Settings settings) {
        super(settings, BlockSetType.STONE);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    static {
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    }



    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!state.get(POWERED)) {
            return SHAPE;
        } else {
            return DOWN_SHAPE;
        }
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {

        boolean triggered = !world.getEntitiesByClass(
                Entity.class,
                BOX.offset(pos),
                this::isValidEntity
        ).isEmpty();
        return triggered ? 15 : 0;
    }

    @Override
    protected int getRedstoneOutput(BlockState state) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
        return state.with(POWERED, rsOut > 0);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!state.get(POWERED)) {
            return SHAPE;
        } else {
            return DOWN_SHAPE;
        }
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        if (!state.get(POWERED)) {
            return SHAPE;
        } else {
            return DOWN_SHAPE;
        }
    }
    private boolean isValidEntity(Entity entity) {
        return entity instanceof PlayerEntity
                || entity instanceof TrialCubeEntity;
    }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return state.getBlock() instanceof DungeonPressurePlateBlock && face == Direction.DOWN && state.get(POWERED);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }
}
