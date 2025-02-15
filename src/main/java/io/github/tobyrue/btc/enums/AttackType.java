package io.github.tobyrue.btc.enums;

import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum AttackType {
    INVISIBLE(4, 0.9, 0.9, 0.9),
    DRAGON_FIRE_BALL(3, 0.4, 0.0, 0.5),
    FIRE_BALL(2, 0.8, 0.4, 0.0),
    WIND_CHARGE(1, 0.7, 0.7, 0.8),
    NONE(0, 0.0, 0.0, 0.0);

    private static final IntFunction<AttackType> BY_ID = ValueLists.createIdToValueFunction((AttackType attackType) -> {
        return attackType.id;
    }, values(), ValueLists.OutOfBoundsHandling.ZERO);
    public final int id;
    public final double[] color;

    AttackType(final int id, final double red, final double green, final double blue) {
        this.id = id;
        this.color = new double[]{red, green, blue};
    }

    public static AttackType byId(int id) {
        return (AttackType)BY_ID.apply(id);
    }
}
