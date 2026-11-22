package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.ducks.MobOwnerAccess;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GoalSelector.class)
public class GoalSelectorMixin implements MobOwnerAccess {
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

    @ModifyArg(
            method = "add",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z")
    )
    private Object btc$attachMobToPrioritizedGoal(Object element) {
        if (element instanceof MobOwnerAccess access && this.btc$owner != null) {
            access.btc$setOwner(this.btc$owner);
        }
        return element;
    }
}
