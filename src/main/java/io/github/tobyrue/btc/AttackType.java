package io.github.tobyrue.btc;

import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum AttackType {
    INVISIBLE(4, 0.3, 0.3, 0.8),
    DRAGON_FIRE_BALL(3, 0.7, 0.5, 0.2),
    FIRE_BALL(2, 0.4, 0.3, 0.35),
    WIND_CHARGE(1, 0.7, 0.7, 0.8),
    NONE(0, 0.0, 0.0, 0.0);

    private static final IntFunction<AttackType> BY_ID = ValueLists.createIdToValueFunction((AttackType attackType) -> {
        return attackType.id;
    }, values(), ValueLists.OutOfBoundsHandling.ZERO);
    public final int id;
    public final double[] particleVelocity;

    AttackType(final int id, final double particleVelocityX, final double particleVelocityY, final double particleVelocityZ) {
        this.id = id;
        this.particleVelocity = new double[]{particleVelocityX, particleVelocityY, particleVelocityZ};
    }

    public static AttackType byId(int id) {
        return (AttackType)BY_ID.apply(id);
    }
}
