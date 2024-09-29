package io.github.tobyrue.btc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AntierBlock extends Block implements ModBlockEntityProvider<AntierBlockEntity>, ModTickBlockEntityProvider<AntierBlockEntity> {
    public static final EnumProperty<AntierType> ANTIER_TYPE = EnumProperty.of("antier_type", AntierType.class);
    private static final VoxelShape MIDDLE;
    private static final VoxelShape TOP;
    private static final VoxelShape BOTTOM;
    private static final VoxelShape SHAPE;
    private static final VoxelShape FULL_SHAPE;

    static {
        MIDDLE = Block.createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
        //change top and bottom
        TOP = Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        BOTTOM = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        FULL_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SHAPE = VoxelShapes.union(MIDDLE, TOP, BOTTOM);
    }
    public AntierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(ANTIER_TYPE, AntierType.NO_MINE));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(ANTIER_TYPE, AntierType.NO_MINE);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ANTIER_TYPE);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return FULL_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return FULL_SHAPE;
    }

    @Override
    public BlockEntityType<AntierBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ANTIER_BLOCK_ENTITY;
    }

}
