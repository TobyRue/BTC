package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.ducks.MobOwnerAccess;
import io.github.tobyrue.btc.util.SummonableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MobEntity.class)
public class MobEntityMixin implements SummonableEntity {
    @Shadow
    @Final
    public GoalSelector goalSelector;
    @Shadow
    @Final
    public GoalSelector targetSelector;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void btc$init(EntityType<? extends MobEntity> entityType, World world, CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;
        ((MobOwnerAccess) this.goalSelector).btc$setOwner(self);
        ((MobOwnerAccess) this.targetSelector).btc$setOwner(self);
    }
    @Unique
    private UUID btc$spellOwnerUuid;

    @Override
    public UUID btc$getSpellOwnerUuid() {
        return btc$spellOwnerUuid;
    }

    @Override
    public void btc$setSpellOwnerUuid(UUID ownerUuid) {
        this.btc$spellOwnerUuid = ownerUuid;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void btc$writeOwnerToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (btc$spellOwnerUuid != null) {
            nbt.putUuid("BTC_SummonOwner", btc$spellOwnerUuid);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void btc$readOwnerFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("BTC_SummonOwner")) {
            this.btc$spellOwnerUuid = nbt.getUuid("BTC_SummonOwner");
        }
    }
}