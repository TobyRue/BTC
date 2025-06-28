package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum SpellTypes implements StringIdentifiable {
    FIRE("fire"),
    WIND("wind"),
    WATER("water"),
    EARTH("earth"),
    ENDER("ender");

    public final String name;

    SpellTypes(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
