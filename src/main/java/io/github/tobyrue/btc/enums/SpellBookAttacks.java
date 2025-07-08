package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum SpellBookAttacks implements StringIdentifiable {
    WATER_BLAST ("water_blast", "water_blast"),
    FIREBALL ("fireball", "fire_ball"),
    DRAGON_FIREBALL ("dragon_fireball", "dragon_fireball"),
    WIND_CHARGE ("wind_charge", "wind_charge"),
    EARTH_SPIKE ("earth_spike", "earth_spike"),
    REGENERATION ("regeneration", "regeneration"),
    CREEPER_WALL_BLOCK ("creeper_wall_block", "creeper_wall_block");

    private final String name;

    private final String cooldownKey;

    SpellBookAttacks(String name, String cooldownKey) {
        this.name = name;
        this.cooldownKey = cooldownKey;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public String getCooldownKey() {
        return cooldownKey;
    }

    public static SpellBookAttacks next(SpellBookAttacks current) {
        SpellBookAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
