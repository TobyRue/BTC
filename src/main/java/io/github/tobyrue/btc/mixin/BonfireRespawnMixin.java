package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.block.entities.BonfireBlockEntity;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class BonfireRespawnMixin implements BonfirePlayerData {
    private NbtCompound bonfireData = new NbtCompound();


    @Override
    public void btc$setBonfireData(NbtCompound nbt) {
        this.bonfireData = nbt;
    }

    @Override
    public NbtCompound btc$getBonfireData() {
        return this.bonfireData;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void btc$saveBonfireData(NbtCompound nbt, CallbackInfo ci) {
        if (this.bonfireData != null) {
            nbt.put("BonfireRespawnData", this.bonfireData);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void btc$loadBonfireData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("BonfireRespawnData")) {
            this.bonfireData = nbt.getCompound("BonfireRespawnData");
        }
    }

    @Inject(method = "dropInventory()V",at = @At("HEAD"), cancellable = true)
    protected void btc$stopInventoryDrop(CallbackInfo ci) {
        PlayerEntity me = (PlayerEntity) (Object) this;
        NbtCompound bonfire = ((BonfirePlayerData) me).btc$getBonfireData();

        if (bonfire != null && bonfire.contains("pos")) {
            BlockPos bonfirePos = BlockPos.fromLong(bonfire.getLong("pos"));
            String dimId = bonfire.getString("dim");
            if (me.getWorld().getBlockEntity(bonfirePos) instanceof BonfireBlockEntity block) {
                double distSq = me.getPos().squaredDistanceTo(bonfirePos.toCenterPos());
                double configRadius = block.getRadius();

                if (distSq <= (configRadius * configRadius) && me.getWorld().getRegistryKey().equals(RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimId)))) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "getXpToDrop()I", at = @At("HEAD"), cancellable = true)
    protected void btc$stopXPDrop(CallbackInfoReturnable<Integer> cir) {
        PlayerEntity me = (PlayerEntity) (Object) this;
        NbtCompound bonfire = ((BonfirePlayerData) me).btc$getBonfireData();
        if (bonfire != null && bonfire.contains("pos")) {
            BlockPos bonfirePos = BlockPos.fromLong(bonfire.getLong("pos"));
            String dimId = bonfire.getString("dim");
            if (me.getWorld().getBlockEntity(bonfirePos) instanceof BonfireBlockEntity block) {
                double distSq = me.getPos().squaredDistanceTo(bonfirePos.toCenterPos());
                double configRadius = block.getRadius();

                if (distSq <= (configRadius * configRadius) && me.getWorld().getRegistryKey().equals(RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimId)))) {
                    cir.setReturnValue(0);
                }
            }
        }
    }

}