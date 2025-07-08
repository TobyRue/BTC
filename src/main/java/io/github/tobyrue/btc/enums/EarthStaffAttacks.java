package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum EarthStaffAttacks implements StringIdentifiable {
    EARTH_SPIKES ("earth_spike_line", "earth_spike_line"),
    CREEPER_WALL_CIRCLE ("creeper_wall_circle_and_regeneration", "creeper_circle"),
    CREEPER_WALL_TRAP_WITH_EXPLOSIVE ("creeper_wall_trap_with_explosive", "creeper_circle_offense"),
    CREEPER_WALL_BLOCK ("creeper_wall_block", "creeper_wall_block"),
    POISON ("poison", "posion");

    private final String name;

    private final String cooldownKey;

    EarthStaffAttacks(String name, String cooldownKey) {
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
    public static EarthStaffAttacks next(EarthStaffAttacks current) {
        EarthStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
