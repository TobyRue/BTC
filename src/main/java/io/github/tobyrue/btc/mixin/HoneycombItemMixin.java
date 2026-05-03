package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.tobyrue.btc.block.entities.FanBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void btc$manualWaxWithNbt(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof FanBlockEntity fanEntity) {
            Optional<BlockState> waxedState = HoneycombItem.getWaxedState(state);

            if (waxedState.isPresent()) {
                if (!world.isClient) {
                    var lookup = world.getRegistryManager();
                    NbtCompound nbt = fanEntity.createNbt(lookup);

                    world.setBlockState(pos, waxedState.get(), 11);

                    BlockEntity newBe = world.getBlockEntity(pos);
                    if (newBe instanceof FanBlockEntity) {
                        newBe.readNbt(nbt, lookup);
                        newBe.markDirty();
                    }

                    if (context.getPlayer() != null && !context.getPlayer().getAbilities().creativeMode) {
                        context.getStack().decrement(1);
                    }
                    world.syncWorldEvent(context.getPlayer(), 3003, pos, 0);
                }
                cir.setReturnValue(ActionResult.success(world.isClient));
            }
        }
    }
}