package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum SpellRegistryEnum implements StringIdentifiable {
    FIRE_BALL_WEAK(0, "fire_ball_weak", 20, SpellTypes.FIRE),
    FIRE_BALL_STRONG(1, "fire_ball_strong", 40, SpellTypes.FIRE);



    private static final IntFunction<SpellRegistryEnum> BY_ID = ValueLists.createIdToValueFunction((SpellRegistryEnum attackType) -> {
        return attackType.id;
    }, values(), ValueLists.OutOfBoundsHandling.ZERO);

    public final int id;
    public final String name;
    public final double cooldown;
    public final SpellTypes spellTypes;

    SpellRegistryEnum(int id, String name, double cooldown, SpellTypes spellTypes) {
        this.id = id;
        this.name = name;
        this.cooldown = cooldown;
        this.spellTypes = spellTypes;
    }

    @Override
    public String asString() {
        return this.name;
    }
    public static SpellRegistryEnum byId(int id) {
        return (SpellRegistryEnum)BY_ID.apply(id);
    }
}
