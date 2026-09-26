package io.github.tobyrue.btc.util;

import io.github.tobyrue.btc.Ticker;
import java.util.List;

public interface LivingEntityMixinAccessor {
    List<Ticker> btc$getTickers();
}