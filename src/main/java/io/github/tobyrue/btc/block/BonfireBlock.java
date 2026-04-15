package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.BonfireBlockEntity;
import io.github.tobyrue.btc.mixin.EntityAccessor;
import io.github.tobyrue.btc.packets.BonfireSyncPayload;
import io.github.tobyrue.btc.packets.ModClientPackets;
import io.github.tobyrue.btc.packets.ModPackets;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BonfireBlock extends Block implements BlockEntityProvider {

    private static final VoxelShape SHAPE =  VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0, 0.375, 0.9375, 0.125, 0.5),
            VoxelShapes.cuboid(0.375, 0, 0.0625, 0.5, 0.125, 0.1875),
            VoxelShapes.cuboid(0.75, 0, 0.25, 0.875, 0.0625, 0.375),
            VoxelShapes.cuboid(0.0625, 0, 0.375, 0.1875, 0.125, 0.4375),
            VoxelShapes.cuboid(0.625, 0, 0.125, 0.8125, 0.125, 0.25),
            VoxelShapes.cuboid(0.125, 0, 0.1875, 0.25, 0.125, 0.375),
            VoxelShapes.cuboid(0.5, 0, 0.0625, 0.625, 0.1875, 0.25),
            VoxelShapes.cuboid(0.25, 0, 0.125, 0.375, 0.1875, 0.25),
            VoxelShapes.cuboid(0.375, 0, 0.8125, 0.5, 0.125, 0.9375),
            VoxelShapes.cuboid(0.0625, 0, 0.4375, 0.25, 0.0625, 0.625),
            VoxelShapes.cuboid(0.5, 0, 0.8125, 0.625, 0.125, 1),
            VoxelShapes.cuboid(0.125, 0, 0.625, 0.25, 0.125, 0.75),
            VoxelShapes.cuboid(0.75, 0, 0.5, 0.9375, 0.1875, 0.625),
            VoxelShapes.cuboid(0.25, 0, 0.6875, 0.375, 0.1875, 0.875),
            VoxelShapes.cuboid(0.75, 0, 0.625, 0.9375, 0.125, 0.8125),
            VoxelShapes.cuboid(0.625, 0, 0.75, 0.75, 0.1875, 0.9375),
            VoxelShapes.cuboid(0.0625, 0, 0.3125, 0.125, 0.0625, 0.375),
            VoxelShapes.cuboid(0.1875, 0, 0.75, 0.25, 0.0625, 0.8125),
            VoxelShapes.cuboid(0.5625, 0.125, 0.8125, 0.625, 0.1875, 0.9375),
            VoxelShapes.cuboid(0.8125, 0.125, 0.625, 0.875, 0.1875, 0.6875),
            VoxelShapes.cuboid(0.75, 0.0625, 0.3125, 0.8125, 0.125, 0.375),
            VoxelShapes.cuboid(0.4375, 0.125, 0.125, 0.5, 0.1875, 0.1875),
            VoxelShapes.cuboid(0.625, 0, 0.0625, 0.6875, 0.0625, 0.125),
            VoxelShapes.cuboid(0.1875, 0, 0.125, 0.25, 0.0625, 0.1875),
            VoxelShapes.cuboid(0.125, 0.0625, 0.4375, 0.25, 0.125, 0.5),
            VoxelShapes.cuboid(0.1875, 0.0625, 0.5, 0.25, 0.125, 0.5625),
            VoxelShapes.cuboid(0.375, 0, 0.6875, 0.4375, 0.0625, 0.8125),
            VoxelShapes.cuboid(0.375, 0.0625, 0.75, 0.4375, 0.125, 0.8125),
            VoxelShapes.cuboid(0.6875, 0, 0.25, 0.75, 0.0625, 0.3125),
            VoxelShapes.cuboid(0.6875, 0, 0.5625, 0.75, 0.0625, 0.625),
            VoxelShapes.cuboid(0.5, 0, 0.4375, 0.5625, 0.037500000000000006, 0.5),
            VoxelShapes.cuboid(0.5625, 0, 0.4375, 0.625, 0.018750000000000003, 0.5),
            VoxelShapes.cuboid(0.5625, 0, 0.5, 0.625, 0.00625, 0.5625),
            VoxelShapes.cuboid(0.6875, 0, 0.4375, 0.75, 0.025, 0.5625),
            VoxelShapes.cuboid(0.625, 0, 0.4375, 0.6875, 0, 0.5625),
            VoxelShapes.cuboid(0.5625, 0, 0.5625, 0.625, 0.0125, 0.625),
            VoxelShapes.cuboid(0.5625, 0, 0.625, 0.625, 0.00625, 0.6875),
            VoxelShapes.cuboid(0.5625, 0, 0.75, 0.625, 0.03125, 0.8125),
            VoxelShapes.cuboid(0.5625, 0, 0.6875, 0.625, 0.0125, 0.75),
            VoxelShapes.cuboid(0.6875, 0, 0.625, 0.75, 0.0125, 0.6875),
            VoxelShapes.cuboid(0.625, 0, 0.5625, 0.6875, 0.00625, 0.625),
            VoxelShapes.cuboid(0.625, 0, 0.625, 0.6875, 0, 0.6875),
            VoxelShapes.cuboid(0.625, 0, 0.6875, 0.6875, 0.00625, 0.75),
            VoxelShapes.cuboid(0.5, 0, 0.5625, 0.5625, 0.00625, 0.625),
            VoxelShapes.cuboid(0.5, 0, 0.625, 0.5625, 0.018750000000000003, 0.6875),
            VoxelShapes.cuboid(0.5, 0, 0.5, 0.5625, 0, 0.5625),
            VoxelShapes.cuboid(0.4375, 0, 0.5, 0.5, 0.00625, 0.5625),
            VoxelShapes.cuboid(0.3125, 0, 0.5, 0.375, 0.00625, 0.5625),
            VoxelShapes.cuboid(0.375, 0, 0.5, 0.4375, 0, 0.5625),
            VoxelShapes.cuboid(0.4375, 0, 0.5625, 0.5, 0, 0.625),
            VoxelShapes.cuboid(0.25, 0, 0.625, 0.3125, 0.00625, 0.6875),
            VoxelShapes.cuboid(0.3125, 0, 0.625, 0.375, 0.0125, 0.6875),
            VoxelShapes.cuboid(0.25, 0, 0.5625, 0.3125, 0.025, 0.625),
            VoxelShapes.cuboid(0.3125, 0, 0.5625, 0.375, 0.00625, 0.625),
            VoxelShapes.cuboid(0.375, 0, 0.5625, 0.4375, 0.0125, 0.625),
            VoxelShapes.cuboid(0.375, 0, 0.625, 0.4375, 0.018750000000000003, 0.6875),
            VoxelShapes.cuboid(0.4375, 0, 0.625, 0.5, 0.037500000000000006, 0.6875),
            VoxelShapes.cuboid(0.4375, 0, 0.75, 0.5625, 0.0125, 0.8125),
            VoxelShapes.cuboid(0.6875, 0, 0.6875, 0.75, 0.025, 0.75),
            VoxelShapes.cuboid(0.4375, 0, 0.4375, 0.5, 0.0125, 0.5),
            VoxelShapes.cuboid(0.375, 0, 0.4375, 0.4375, 0.00625, 0.5),
            VoxelShapes.cuboid(0.3125, 0, 0.4375, 0.375, 0, 0.5),
            VoxelShapes.cuboid(0.25, 0, 0.4375, 0.3125, 0.0125, 0.5625),
            VoxelShapes.cuboid(0.1875, 0, 0.375, 0.25, 0.00625, 0.4375),
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.3125, 0.025, 0.3125),
            VoxelShapes.cuboid(0.25, 0, 0.3125, 0.3125, 0.03125, 0.375),
            VoxelShapes.cuboid(0.25, 0, 0.375, 0.3125, 0.018750000000000003, 0.4375),
            VoxelShapes.cuboid(0.3125, 0, 0.25, 0.375, 0.037500000000000006, 0.3125),
            VoxelShapes.cuboid(0.375, 0, 0.1875, 0.4375, 0.03125, 0.25),
            VoxelShapes.cuboid(0.4375, 0, 0.1875, 0.5, 0.0125, 0.25),
            VoxelShapes.cuboid(0.375, 0, 0.25, 0.4375, 0.0625, 0.3125),
            VoxelShapes.cuboid(0.5625, 0, 0.25, 0.625, 0.0125, 0.3125),
            VoxelShapes.cuboid(0.625, 0, 0.25, 0.6875, 0.025, 0.3125),
            VoxelShapes.cuboid(0.6875, 0, 0.3125, 0.75, 0.00625, 0.375),
            VoxelShapes.cuboid(0.625, 0, 0.3125, 0.6875, 0, 0.375),
            VoxelShapes.cuboid(0.6875, 0, 0.375, 0.75, 0.018750000000000003, 0.4375),
            VoxelShapes.cuboid(0.625, 0, 0.375, 0.6875, 0.00625, 0.4375),
            VoxelShapes.cuboid(0.5625, 0, 0.375, 0.625, 0.0125, 0.4375),
            VoxelShapes.cuboid(0.4375, 0, 0.375, 0.5, 0.00625, 0.4375),
            VoxelShapes.cuboid(0.5, 0, 0.375, 0.5625, 0.018750000000000003, 0.4375),
            VoxelShapes.cuboid(0.375, 0, 0.375, 0.4375, 0.0125, 0.4375),
            VoxelShapes.cuboid(0.3125, 0, 0.375, 0.375, 0.018750000000000003, 0.4375),
            VoxelShapes.cuboid(0.5, 0, 0.3125, 0.5625, 0.0125, 0.375),
            VoxelShapes.cuboid(0.5625, 0, 0.3125, 0.625, 0, 0.375),
            VoxelShapes.cuboid(0.375, 0, 0.3125, 0.4375, 0, 0.375),
            VoxelShapes.cuboid(0.3125, 0, 0.3125, 0.375, 0.00625, 0.375),
            VoxelShapes.cuboid(0.4375, 0, 0.3125, 0.5, 0.018750000000000003, 0.375)
    );

    public BonfireBlock(Settings settings) {
        super(settings);
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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            BonfirePlayerData data = (BonfirePlayerData) serverPlayer;

            NbtCompound newData = new NbtCompound();
            newData.putLong("pos", pos.asLong());
            newData.putString("dim", world.getRegistryKey().getValue().toString());
            data.bTC$setBonfireData(newData);
            BonfireSyncPayload payload = new BonfireSyncPayload(newData);

            ServerPlayNetworking.send(serverPlayer, payload);

            player.sendMessage(Text.translatable("block.btc.bonfire.set_spawn"), true);
        }
        return ActionResult.SUCCESS;
    }
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BonfireBlockEntity(pos, state);
    }
}
