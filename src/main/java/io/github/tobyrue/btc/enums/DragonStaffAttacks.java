package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum DragonStaffAttacks implements StringIdentifiable {
    ENDER_PEARL ("ender_pearl"),
    DRAGONS_BREATH ("dragons_breath"),
    LIFE_STEAL ("life_steal"),
    DRAGON_SCALES_1 ("dragon_scales_1"),
    DRAGON_SCALES_3 ("dragon_scales_3"),
    DRAGON_SCALES_5 ("dragon_scales_5");

    private final String name;

    DragonStaffAttacks(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static DragonStaffAttacks next(DragonStaffAttacks current) {
        DragonStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
