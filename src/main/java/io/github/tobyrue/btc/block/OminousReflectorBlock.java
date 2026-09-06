package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public class OminousReflectorBlock extends Block {

    public static final MapCodec<OminousReflectorBlock> CODEC = createCodec(OminousReflectorBlock::new);
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final EnumProperty<ReflectorMode> REFLECTOR_MODE = EnumProperty.of("reflector_mode", ReflectorMode.class);

    public enum ReflectorMode implements StringIdentifiable {
        ALL_IN("all_in"),
        ALL_OUT("all_out");

        private final String name;
        ReflectorMode(String name) { this.name = name; }
        @Override public String asString() { return this.name; }
    }

    public OminousReflectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP)
                .with(REFLECTOR_MODE, ReflectorMode.ALL_IN));
    }

    @Override
    public MapCodec<? extends OminousReflectorBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(REFLECTOR_MODE, ReflectorMode.ALL_IN);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, REFLECTOR_MODE);
    }
}