package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;

public enum WaterStaffAttacks implements StringIdentifiable {
    WATER_BLAST ("water_blast", "water_blast"),
    ICE_FREEZE ("ice_freeze", "ice_freeze"),
    FROST_WALKER ("frost_walker", "frost_walker"),
    DOLPHINS_GRACE ("dolphins_grace", "effect"),
    CONDUIT_POWER ("conduit_power", "effect");

    private final String name;

    private final String cooldownKey;

    WaterStaffAttacks(String name, String cooldownKey) {
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

    public static WaterStaffAttacks next(WaterStaffAttacks current) {
        WaterStaffAttacks[] values = values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
