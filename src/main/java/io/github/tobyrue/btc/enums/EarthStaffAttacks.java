package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum EarthStaffAttacks implements StringIdentifiable {
    EARTH_SPIKES ("earth_spike_line"),
    CREEPER_WALL_CIRCLE ("creeper_wall_circle_and_regeneration"),
    CREEPER_WALL_TRAP_WITH_EXPLOSIVE ("creeper_wall_trap_with_explosive"),
    CREEPER_WALL_BLOCK ("creeper_wall_block"),
    POISON ("poison");

    private final String name;

    EarthStaffAttacks(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static EarthStaffAttacks next(EarthStaffAttacks current) {
        EarthStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
