package io.github.tobyrue.btc;

import net.minecraft.block.*;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DungeonFireBlock extends Block {
    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE2;
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape COAL_SHAPE1;
    private static final VoxelShape COAL_SHAPE2;
    private static final VoxelShape COAL_SHAPE3;
    private static final VoxelShape COAL_SHAPE4;

    private static final VoxelShape SHAPE;

    public static final IntProperty DAMAGE = IntProperty.of("damage", 0, 31);
    public static final BooleanProperty INFERNAL = BooleanProperty.of("infernal");

    public DungeonFireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(DAMAGE, 2));
    }
    private ActionResult infernal(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean infernal) {
        if(player.isSneaking()) {
        }
        return ActionResult.FAIL;
    }
    static {
        BOTTOM_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 2.0, 13.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 6.0, 12.0);
        MIDDLE_SHAPE2 = Block.createCuboidShape(3.0, 6.0, 3.0, 13.0, 7.0, 13.0);
        TOP_SHAPE = Block.createCuboidShape(2.0, 7.0, 2.0, 14.0, 10.0, 14.0);
        COAL_SHAPE1 = Block.createCuboidShape(2.0, 8.0, 2.0, 14.0, 11.0, 14.0);
        COAL_SHAPE2 = Block.createCuboidShape(2.0, 11.0, 2.0, 14.0, 12.0, 14.0);
        COAL_SHAPE3 = Block.createCuboidShape(2.0, 12.0, 2.0, 14.0, 13.0, 14.0);
        COAL_SHAPE4 = Block.createCuboidShape(2.0, 13.0, 2.0, 14.0, 14.0, 14.0);

        SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MIDDLE_SHAPE, MIDDLE_SHAPE2, TOP_SHAPE, COAL_SHAPE1, COAL_SHAPE2, COAL_SHAPE3, COAL_SHAPE4);
    }
        @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(INFERNAL, false)
                .with(DAMAGE, 2);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(INFERNAL);
        builder.add(DAMAGE);
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
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
            if (!entity.bypassesSteppingEffects() && entity instanceof LivingEntity) {
                int damageValue = state.get(DAMAGE);
                // Convert the damage value to float
                float damageFloat = (float) damageValue;
                entity.damage(world.getDamageSources().hotFloor(), damageFloat);
                entity.setOnFireFor(5);
            }


        super.onSteppedOn(world, pos, state, entity);
    }
}