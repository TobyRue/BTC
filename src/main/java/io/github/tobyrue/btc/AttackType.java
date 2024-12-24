package io.github.tobyrue.btc;

import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum AttackType {
    DRAGON_FIRE_BALL(1),
    WIND_CHARGE(2),
    FIRE_BALL(1),
    NONE(0);

    private static final IntFunction<AttackType> BY_ID = ValueLists.createIdToValueFunction((AttackType type) -> {
        return type.id;
    }, values(), ValueLists.OutOfBoundsHandling.ZERO);
    public final int id;

    AttackType(final int id) {
        this.id = id;
    }

    public static AttackType byId(int id) {
        return (AttackType)BY_ID.apply(id);
    }
}
