package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.ModTickBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.OminousBeaconBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OminousBeaconBlock extends BlockWithEntity implements ModBlockEntityProvider<OminousBeaconBlockEntity>, ModTickBlockEntityProvider<OminousBeaconBlockEntity> {

    public OminousBeaconBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
    }

    public static final MapCodec<OminousBeaconBlock> CODEC = createCodec(OminousBeaconBlock::new);

    public static final DirectionProperty FACING = FacingBlock.FACING;


    @Override
    public MapCodec<? extends OminousBeaconBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
//        // Make sure to check world.isClient if you only want to tick only on serverside.
//        return validateTicker(type, ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, OminousBeaconBlockEntity::tick);
//    }
    @Override
    public BlockEntityType<OminousBeaconBlockEntity> getBlockEntityType() {
        return ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY;
    }
}