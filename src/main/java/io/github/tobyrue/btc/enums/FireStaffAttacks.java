package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum FireStaffAttacks implements StringIdentifiable {
    WEAK_FIREBALL ("weak_fireball"),
    STRONG_FIREBALL ("strong_fireball"),
    CONCENTRATED_FIRE_STORM ("concentrated_fire_storm"),
    FIRE_STORM ("fire_storm"),
    STRENGTH ("strength"),
    RESISTANCE ("resistance");

    private final String name;

    FireStaffAttacks(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static FireStaffAttacks next(FireStaffAttacks current) {
        FireStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
