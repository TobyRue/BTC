package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.BonfireBlockEntity;
import io.github.tobyrue.btc.mixin.EntityAccessor;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BonfireBlock extends Block implements BlockEntityProvider {

    public BonfireBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {

            NbtCompound bonfireData = new NbtCompound();
            bonfireData.putLong("pos", pos.asLong());
            bonfireData.putString("dim", world.getRegistryKey().getValue().toString());


            ((BonfirePlayerData) serverPlayer).bTC$setBonfireData(bonfireData);

            if (world.getBlockEntity(pos) instanceof BonfireBlockEntity be) {
                be.setLastActivatedBy(player.getUuid());
            }

            player.sendMessage(Text.literal("Bonfire spawn set!"), true);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BonfireBlockEntity(pos, state);
    }
}
