package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum SpellBookAttacks implements StringIdentifiable {
    WATER_BLAST ("water_blast"),
    FIREBALL ("fireball"),
    DRAGON_FIREBALL ("dragon_fireball"),
    WIND_CHARGE ("wind_charge"),
    EARTH_SPIKE ("earth_spike"),
    REGENERATION ("regeneration"),
    CREEPER_WALL_CIRCLE ("creeper_wall_circle");

    private final String name;

    SpellBookAttacks(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static SpellBookAttacks next(SpellBookAttacks current) {
        SpellBookAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
