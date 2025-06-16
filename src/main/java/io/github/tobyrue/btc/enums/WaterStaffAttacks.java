package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum WaterStaffAttacks implements StringIdentifiable {
    WATER_BLAST ("water_blast"),
    ICE_FREEZE ("ice_freeze"),
    FROST_WALKER ("frost_walker"),
    DOLPHINS_GRACE ("dolphins_grace"),
    CONDUIT_POWER ("conduit_power");

    private final String name;

    WaterStaffAttacks(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static WaterStaffAttacks next(WaterStaffAttacks current) {
        WaterStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
