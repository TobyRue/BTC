package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.block.entities.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ObsidianChestBlock extends BlockWithEntity implements ModBlockEntityProvider<ObsidianChestBlockEntity>, Waterloggable, ModTickBlockEntityProvider<ObsidianChestBlockEntity> {
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;

    protected static final VoxelShape DOUBLE_NORTH_SHAPE;
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE;
    protected static final VoxelShape DOUBLE_WEST_SHAPE;
    protected static final VoxelShape DOUBLE_EAST_SHAPE;
    protected static final VoxelShape SINGLE_SHAPE;

    public static final MapCodec<ObsidianChestBlock> CODEC = createCodec(ObsidianChestBlock::new);

    protected ObsidianChestBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.NORTH));
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SINGLE_SHAPE;
    }

    @Override
    public BlockEntityType<ObsidianChestBlockEntity> getBlockEntityType() {
        return ModBlockEntities.OBSIDIAN_CHEST_BLOCK_ENTITY;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        if (world.getBlockEntity(pos) instanceof ObsidianChestBlockEntity be && !isChestBlocked(world, pos)) {
            UUID playerUuid = player.getUuid();
            SimpleInventory playerChestInv = be.getInventoryForPlayer(playerUuid);

            if (be.getLootTableId() != null && !be.hasPlayerLooted(playerUuid)) {
                generateLootForPlayer(world, be, player, playerChestInv);
                System.out.println("Hi");
                be.markPlayerLooted(playerUuid);
                System.out.println("Hi: " + be.hasPlayerLooted(playerUuid));
                if (!world.isClient) {
                    world.addSyncedBlockEvent(pos, be.getCachedState().getBlock(), 2, 1);
                }
            }

            player.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return Text.translatable("container.obsidian_chest");
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) {
                    return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, inv, playerChestInv, 3) {
                        @Override
                        public void onClosed(PlayerEntity player) {
                            super.onClosed(player);
                            be.onClose(player);
                        }
                    };
                }
            });

            be.onOpen(player);
        }
        return ActionResult.CONSUME;
    }
    private void generateLootForPlayer(World world, ObsidianChestBlockEntity be, PlayerEntity player, SimpleInventory inventory) {
        MinecraftServer server = world.getServer();
        if (server == null || be.getLootTableId() == null) return;

        LootTable lootTable = server.getReloadableRegistries().getLootTable(
                RegistryKey.of(RegistryKeys.LOOT_TABLE, be.getLootTableId())
        );

        LootContextParameterSet parameterSet = new LootContextParameterSet.Builder((ServerWorld) world)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(be.getPos()))
                .add(LootContextParameters.THIS_ENTITY, player)
                .build(LootContextTypes.CHEST);

        List<ItemStack> loot = lootTable.generateLoot(parameterSet);

        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            slots.add(i);
        }
        Collections.shuffle(slots);

        for (ItemStack stack : loot) {
            if (slots.isEmpty()) break;
            int randomSlot = slots.remove(0);
            inventory.setStack(randomSlot, stack);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? validateTicker(type, ModBlockEntities.OBSIDIAN_CHEST_BLOCK_ENTITY, ObsidianChestBlockEntity::clientTick) : null;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

        return this.getDefaultState()
                .with(FACING, facing)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }
    public static boolean isChestBlocked(WorldAccess world, BlockPos pos) {
        return hasBlockOnTop(world, pos);
    }

    private static boolean hasBlockOnTop(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return world.getBlockState(blockPos).isSolidBlock(world, blockPos);
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
        WATERLOGGED = Properties.WATERLOGGED;
        DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
        DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
        DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
        DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
        SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    }
}
