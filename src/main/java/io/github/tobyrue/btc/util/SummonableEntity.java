package io.github.tobyrue.btc.util;

import java.util.UUID;

public interface SummonableEntity {
    UUID btc$getOwnerUuid();
    void btc$setOwnerUuid(UUID ownerUuid);
}