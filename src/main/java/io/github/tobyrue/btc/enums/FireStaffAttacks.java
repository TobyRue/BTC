package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum FireStaffAttacks implements StringIdentifiable {
    WEAK_FIREBALL ("weak_fireball", "fireball"),
    STRONG_FIREBALL ("strong_fireball", "fireball"),
    CONCENTRATED_FIRE_STORM ("concentrated_fire_storm", "fire_storm"),
    FIRE_STORM ("fire_storm", "fire_storm"),
    ETERNAL_FIRE ("eternal_fire", "eternal_fire"),
    STRENGTH ("strength", "strong_effect"),
    RESISTANCE ("resistance", "strong_effect");

    private final String name;

    private final String cooldownKey;

    FireStaffAttacks(String name, String cooldownKey) {
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

    public static FireStaffAttacks next(FireStaffAttacks current) {
        FireStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
