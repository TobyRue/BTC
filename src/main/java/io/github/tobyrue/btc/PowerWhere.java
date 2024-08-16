package io.github.tobyrue.btc;

import net.minecraft.util.StringIdentifiable;

public enum PowerWhere implements StringIdentifiable {
    NONE("none"),
    UP("up"),
    DOWN("down"),
    NORTH("north"),
    SOUTH("south"),
    WEST("west"),
    EAST("east");

    private final String name;

    PowerWhere(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
