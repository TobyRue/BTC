package io.github.tobyrue.btc.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum SpellRegistryEnum implements StringIdentifiable {
    FIREBALL_WEAK(0, "fireball_weak", 20, SpellTypes.FIRE, true),
    FIREBALL_STRONG(1, "fireball_strong", 40, SpellTypes.FIRE, false),
    EARTH_SPIKES(2, "earth_spike", 60, SpellTypes.EARTH, false),
    DRAGON_FIREBALL(3, "dragon_fireball", 80, SpellTypes.ENDER, false),
    FROST_WALKER(4, "frost_walker", 80, SpellTypes.WATER, false),
    CLUSTER_WIND_SHOT(5, "cluster_wind_shot", 80, SpellTypes.WIND, false);



    private static final IntFunction<SpellRegistryEnum> BY_ID = ValueLists.createIdToValueFunction((SpellRegistryEnum attackType) -> {
        return attackType.id;
    }, values(), ValueLists.OutOfBoundsHandling.ZERO);

    public final int id;
    public final String name;
    public final double cooldown;
    public final SpellTypes spellTypes;
    public final boolean isStartingSpell;

    SpellRegistryEnum(int id, String name, double cooldown, SpellTypes spellTypes, boolean isStartingSpell) {
        this.id = id;
        this.name = name;
        this.cooldown = cooldown;
        this.spellTypes = spellTypes;
        this.isStartingSpell = isStartingSpell;
    }

    public SpellTypes getSpellType() {
        return this.spellTypes;
    }

    @Override
    public String asString() {
        return this.name;
    }
    @Override
    public String toString() {
        return this.name;
    }
    public static SpellRegistryEnum byId(int id) {
        return (SpellRegistryEnum)BY_ID.apply(id);
    }
}
