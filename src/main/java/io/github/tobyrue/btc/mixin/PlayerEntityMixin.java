package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements BonfirePlayerData {
    private NbtCompound bonfireData = new NbtCompound();

    @Override
    public void bTC$setBonfireData(NbtCompound nbt) {
        this.bonfireData = nbt;
    }

    @Override
    public NbtCompound bTC$getBonfireData() {
        return this.bonfireData;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void saveBonfireData(NbtCompound nbt, CallbackInfo ci) {
        if (this.bonfireData != null) {
            nbt.put("BonfireRespawnData", this.bonfireData);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void loadBonfireData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("BonfireRespawnData")) {
            this.bonfireData = nbt.getCompound("BonfireRespawnData");
        }
    }

}