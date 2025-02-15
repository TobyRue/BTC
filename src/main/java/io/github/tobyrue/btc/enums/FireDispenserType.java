package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum FireDispenserType implements StringIdentifiable {
    NO_FIRE("no_fire"),
    SHORT_FIRE_SOUL("short_fire_soul"),
    TALL_FIRE_SOUL("tall_fire_soul"),
    SHORT_FIRE("short_fire"),
    TALL_FIRE("tall_fire");

    private final String name;

    FireDispenserType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
