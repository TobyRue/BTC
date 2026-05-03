package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.tobyrue.btc.block.entities.FanBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @WrapOperation(
            method = "useOnBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z")
    )
    private boolean preserveNbtOnStrip(World world, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
        BlockEntity oldBe = world.getBlockEntity(pos);
        if (oldBe instanceof FanBlockEntity) {
            NbtCompound nbt = oldBe.createNbt(world.getRegistryManager());

            boolean result = original.call(world, pos, state, flags);

            BlockEntity newBe = world.getBlockEntity(pos);
            if (newBe != null) {
                newBe.readNbt(nbt, world.getRegistryManager());
            }
            return result;
        }
        return original.call(world, pos, state, flags);
    }
}