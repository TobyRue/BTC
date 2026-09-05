package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.StructureCoreBlockEntity;
import io.github.tobyrue.btc.client.screen.StructureCoreScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StructureCoreBlock extends Block implements ModBlockEntityProvider<StructureCoreBlockEntity> {
    public static final DirectionProperty FACING = Properties.FACING;

    public StructureCoreBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient && player.isCreativeLevelTwoOp()) {
            openClientScreen(world, pos);
        }
        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openClientScreen(World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof StructureCoreBlockEntity core) {
            core.setDataSetCount(0);
            MinecraftClient.getInstance().setScreen(new StructureCoreScreen(core));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof StructureCoreBlockEntity core && !core.getFunctions().isEmpty()) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            core.runRandomFunction(world, pos);
        }
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
    public BlockEntityType<StructureCoreBlockEntity> getBlockEntityType() {
        return ModBlockEntities.STRUCTURE_CORE_BLOCK_ENTITY;
    }
}