package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.ModTickBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.OminousBeaconBlockEntity;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OminousBeaconBlock extends BlockWithEntity implements ModBlockEntityProvider<OminousBeaconBlockEntity>, ModTickBlockEntityProvider<OminousBeaconBlockEntity>, IDungeonWire {

    public static final MapCodec<OminousBeaconBlock> CODEC = createCodec(OminousBeaconBlock::new);
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty<BeaconMode> BEACON_MODE = EnumProperty.of("beacon_mode", BeaconMode.class);

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        if (state.get(BEACON_MODE) == BeaconMode.DECORATIVE) {
            return false;
        }
        return state.get(POWERED) && state.get(BEACON_MODE) == BeaconMode.RECEIVER;
    }

    public enum BeaconMode implements StringIdentifiable {
        DECORATIVE("decorative"),
        RECEIVER("receiver"),
        SENDER("sender");

        private final String name;
        BeaconMode(String name) { this.name = name; }
        @Override public String asString() { return this.name; }
    }

    public OminousBeaconBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP)
                .with(POWERED, false)
                .with(BEACON_MODE, BeaconMode.DECORATIVE));
    }

    @Override
    public MapCodec<? extends OminousBeaconBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(POWERED, false)
                .with(BEACON_MODE, BeaconMode.DECORATIVE);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient) return;

        if (state.get(BEACON_MODE) == BeaconMode.SENDER) {
            boolean isPowered = (IDungeonWire.isReceivingDungeonWirePower(state, world, pos, Direction.values()));
            if (isPowered != state.get(POWERED)) {
                world.setBlockState(pos, state.with(POWERED, isPowered), 3);
            }
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        if (state.get(BEACON_MODE) == BeaconMode.SENDER) {
            boolean isPowered = (sourceBlock instanceof IDungeonWire && IDungeonWire.isReceivingDungeonWirePower(state, world, pos, Direction.values()));
            if (isPowered != state.get(POWERED)) {
                world.setBlockState(pos, state.with(POWERED, isPowered), 3);
            }
        }
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
        builder.add(FACING, POWERED, BEACON_MODE);
    }

    @Override
    public net.minecraft.block.entity.BlockEntityType<OminousBeaconBlockEntity> getBlockEntityType() {
        return ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY;
    }
}