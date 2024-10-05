package io.github.tobyrue.btc;

import net.minecraft.util.StringIdentifiable;

public enum FireSwich implements StringIdentifiable {
    TALL_TO_SHORT("tall_to_short"),
    TALL_SOUL_TO_SHORT_SOUL("tall_soul_to_short_soul"),
    SHORT_TO_TALL("short_to_tall"),
    SHORT_SOUL_TO_SHORT("short_soul_to_short"),
    SHORT_TO_SHORT_SOUL("short_to_short_soul"),
    TALL_SOUL_TO_TALL("tall_soul_to_tall"),
    TALL_TO_TALL_SOUL("tall_to_tall_soul"),
    SHORT_SOUL_TO_TALL_SOUL("short_soul_to_tall_soul"),
    TALL_SOUL_TO_SHORT("tall_soul_to_short"),
    TALL_TO_SHORT_SOUL("tall_to_short_soul"),
    SHORT_SOUL_TO_TALL("short_soul_to_tall"),
    SHORT_TO_TALL_SOUL("short_to_tall_soul");
    private final String name;

    FireSwich(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
