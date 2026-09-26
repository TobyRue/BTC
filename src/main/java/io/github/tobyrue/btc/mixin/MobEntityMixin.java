package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.util.SummonableEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MobEntity.class)
public class MobEntityMixin implements SummonableEntity {

    @Unique
    private UUID btc$ownerUuid;

    @Override
    public UUID btc$getOwnerUuid() {
        return btc$ownerUuid;
    }

    @Override
    public void btc$setOwnerUuid(UUID ownerUuid) {
        this.btc$ownerUuid = ownerUuid;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void btc$writeOwnerToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (btc$ownerUuid != null) {
            nbt.putUuid("BTC_SummonOwner", btc$ownerUuid);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void btc$readOwnerFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("BTC_SummonOwner")) {
            this.btc$ownerUuid = nbt.getUuid("BTC_SummonOwner");
        }
    }
}