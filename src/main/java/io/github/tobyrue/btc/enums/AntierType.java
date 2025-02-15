package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum AntierType implements StringIdentifiable {
    BOTH("both"),
    NO_MINE("no_mine"),
    NO_BUILD("no_build");

    private final String name;

    AntierType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}

