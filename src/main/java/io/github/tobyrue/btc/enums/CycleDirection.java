package io.github.tobyrue.btc.enums;

import com.mojang.serialization.Codec;

public enum CycleDirection {
    UP, DOWN, NORTH, EAST, SOUTH, WEST;

    public static final Codec<CycleDirection> CODEC = Codec.STRING.xmap(
            CycleDirection::valueOf,
            CycleDirection::name
    );

    public CycleDirection next(boolean reverse) {
        int len = values().length;
        int idx = this.ordinal();
        return values()[(idx + (reverse ? -1 : 1) + len) % len];
    }
}