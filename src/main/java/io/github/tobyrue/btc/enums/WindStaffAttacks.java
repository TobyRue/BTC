package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum WindStaffAttacks implements StringIdentifiable {
    WIND_CHARGE ("wind_charge"),
    CLUSTER_WIND_CHARGE ("cluster_wind_charge"),
    TEMPESTS_CALL ("tempests_call"),
    STORM_PUSH ("storm_push");

    private final String name;

    WindStaffAttacks(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static WindStaffAttacks next(WindStaffAttacks current) {
        WindStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
