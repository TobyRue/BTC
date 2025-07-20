package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum WindStaffAttacks implements StringIdentifiable {
    WIND_CHARGE ("wind_charge", "wind_charge"),
    CLUSTER_WIND_CHARGE ("cluster_wind_charge", "cluster_wind_charge"),
    TEMPESTS_CALL ("tempests_call", "tempests_call"),
    STORM_PUSH ("storm_push", "storm_push"),
    WIND_TORNADO ("wind_tornado", "wind_tornado");

    private final String name;

    private final String cooldownKey;

    WindStaffAttacks(String name, String cooldownKey) {
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

    public static WindStaffAttacks next(WindStaffAttacks current) {
        WindStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
