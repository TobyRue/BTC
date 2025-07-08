package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum DragonStaffAttacks implements StringIdentifiable {
    ENDER_PEARL ("ender_pearl", "ender_pearl"),
    DRAGONS_BREATH ("dragons_breath", "dragons_breath"),
    LIFE_STEAL ("life_steal", "life_steal"),
    DRAGON_SCALES_1 ("dragon_scales_1", "scales"),
    DRAGON_SCALES_3 ("dragon_scales_3", "scales"),
    DRAGON_SCALES_5 ("dragon_scales_5", "scales");

    private final String name;

    private final String cooldownKey;

    DragonStaffAttacks(String name, String cooldownKey) {
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

    public static DragonStaffAttacks next(DragonStaffAttacks current) {
        DragonStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
