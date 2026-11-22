package io.github.tobyrue.btc.ducks;

import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public interface MobOwnerAccess {
    void btc$setOwner(MobEntity mobEntity);
    @Nullable MobEntity btc$getOwner();
}
