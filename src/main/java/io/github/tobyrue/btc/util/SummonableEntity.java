package io.github.tobyrue.btc.util;

import java.util.UUID;

public interface SummonableEntity {
    UUID btc$getSpellOwnerUuid();
    void btc$setSpellOwnerUuid(UUID ownerUuid);
}