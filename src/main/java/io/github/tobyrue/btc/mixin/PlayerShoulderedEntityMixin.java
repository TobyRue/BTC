package io.github.tobyrue.btc.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerShoulderedEntityMixin {
    @Shadow public abstract NbtCompound getShoulderEntityLeft();
    @Shadow public abstract NbtCompound getShoulderEntityRight();

    @Inject(
            method = "tickMovement",
            // We use 'at' to find the specific logic block rather than the method call
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"),
            cancellable = true
    )
    private void btc$preventKeyGolemDrop(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        boolean isOnlyFalling = player.fallDistance > 0.5F
                && !player.isTouchingWater()
                && !player.getAbilities().flying
                && !player.isSleeping()
                && !player.inPowderSnow;
        if (isOnlyFalling) {
            boolean leftIsGolem = isKeyGolem(this.getShoulderEntityLeft());
            boolean rightIsGolem = isKeyGolem(this.getShoulderEntityRight());

            if (leftIsGolem && rightIsGolem) {
                ci.cancel();
            } else if (!leftIsGolem && rightIsGolem) {
                player.dropShoulderEntity(getShoulderEntityLeft());
                player.setShoulderEntityLeft(new NbtCompound());
                ci.cancel();
            } else if (leftIsGolem && !rightIsGolem) {
                player.dropShoulderEntity(getShoulderEntityRight());
                player.setShoulderEntityRight(new NbtCompound());
                ci.cancel();
            }
        }
    }

    @Unique
    private boolean isKeyGolem(NbtCompound nbt) {
        return nbt != null && nbt.getString("id").equals("btc:key_golem");
    }
}
