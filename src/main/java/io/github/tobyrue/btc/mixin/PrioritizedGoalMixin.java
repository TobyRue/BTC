package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.ducks.MobOwnerAccess;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PrioritizedGoal.class)
public class PrioritizedGoalMixin implements MobOwnerAccess {
    @Shadow
    @Final
    public Goal goal;
    @Unique
    @Nullable
    private MobEntity btc$owner;

    @Override
    public void btc$setOwner(MobEntity mobEntity) {
        btc$owner = mobEntity;
    }

    @Override
    public @Nullable MobEntity btc$getOwner() {
        return btc$owner;
    }
    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    private void btc$blockStartWhenHijacked(CallbackInfoReturnable<Boolean> cir) {
        if (btc$shouldBlockGoal()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldContinue", at = @At("HEAD"), cancellable = true)
    private void btc$blockContinueWhenHijacked(CallbackInfoReturnable<Boolean> cir) {
        if (btc$shouldBlockGoal()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean btc$shouldBlockGoal() {
//        if (this.btc$owner != null && btc$isHijacked(this.btc$owner)) {
//            return !(this.goal instanceof YourHijackedGoalInterface);
//        }
        //TODO
        return false;
    }
}
