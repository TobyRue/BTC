package io.github.tobyrue.btc.enums;

import io.github.tobyrue.btc.util.AdvancementUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum SpellRegistryEnum implements StringIdentifiable {
    FIREBALL_WEAK(0, "fireball_weak", 20, "fireball", SpellTypes.FIRE, true),
    FIREBALL_STRONG(1, "fireball_strong", 40, "fireball", SpellTypes.FIRE, false),
    FIRE_STORM(2, "fire_storm", 60, "fire_storm", SpellTypes.FIRE, false),
    CONCENTRATED_FIRE_STORM(3, "concentrated_fire_storm", 80, "fire_storm", SpellTypes.FIRE, false),
    EARTH_SPIKES(4, "earth_spike", 60, "earth_spikes", SpellTypes.EARTH, false),
    DRAGON_FIREBALL(5, "dragon_fireball", 80, "dragon_fireball", SpellTypes.ENDER, false),
    FROST_WALKER(6, "frost_walker", 80, "frost_walker", SpellTypes.WATER, false),
    CLUSTER_WIND_SHOT(7, "cluster_wind_shot", 80, "cluster_wind", SpellTypes.WIND, false),
    TEMPESTS_CALL(8, "tempests_call", 100, "tempests_call", SpellTypes.WIND, false),
    STORM_PUSH(9, "storm_push", 100, "storm_push", SpellTypes.WIND, false);


    /**
     * Use code below to detect if player has the spell
     * AdvancementUtils already exists in main mod package
     *
     * Gets first spell that the player has
     * for (SpellRegistryEnum spell : SpellRegistryEnum.values()) {
     *     String path = String.format("adventure/get_%s_scroll", spell.byId());
     *     if (AdvancementUtils.hasAdvancement(player, "btc", path)) {
     *         // Found the first advancement they have!
     *         //DO STUFF HERE
     *         break;  // stop looping once one is found
     *     }
     * }
     *
     * Example of getting the next spell the player has obtained
     * This gets the next unlocked spell after currentSpell
     * If there is no next obtained spell it returns the current spell it is on
     * All methods already exist inside of this
     *
     * SpellRegistryEnum nextSpell = SpellRegistryEnum.nextUnlockedOrCurrent(player, currentSpell);
     * player.sendMessage(Text.literal("Next available spell: " + nextSpell.asString()), false);
     * currentSpell = nextSpell
     *
     *
     */

    private static final IntFunction<SpellRegistryEnum> BY_ID = ValueLists.createIdToValueFunction((SpellRegistryEnum attackType) -> {
        return attackType.id;
    }, values(), ValueLists.OutOfBoundsHandling.ZERO);

    public final int id;
    public final String name;

    public final double cooldown;
    private final String cooldownKey;
    public final SpellTypes spellTypes;
    public final boolean hasNoScroll;

    SpellRegistryEnum(int id, String name, double cooldown, String cooldownKey, SpellTypes spellTypes, boolean hasNoScroll) {
        this.id = id;
        this.name = name;
        this.cooldown = cooldown;
        this.cooldownKey = cooldownKey;
        this.spellTypes = spellTypes;
        this.hasNoScroll = hasNoScroll;
    }

    public double getCooldown() {
        return cooldown;
    }

    public SpellTypes getSpellType() {
        return this.spellTypes;
    }

    public String getCooldownKey() {
        return cooldownKey;
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

    public static boolean hasSpell(ServerPlayerEntity player, SpellRegistryEnum spell) {
        if (spell.hasNoScroll || AdvancementUtils.hasAdvancement(player, "btc", String.format("adventure/get_%s_scroll", spell.asString()))) {
            return true;
        }

        return true;
    }

    public static SpellRegistryEnum nextUnlocked(ServerPlayerEntity player, SpellRegistryEnum current) {
        int total = values().length;
        int index = current.id;

        for (int i = 0; i < total; i++) {
            index = (index + 1) % total;
            SpellRegistryEnum nextSpell = byId(index);

            // unlocked if player has advancement OR it's a starting spell
            if (nextSpell.hasNoScroll || AdvancementUtils.hasAdvancement(player, "btc", String.format("adventure/get_%s_scroll", nextSpell.asString()))) {
                return nextSpell;
            }
        }

        // No unlocked spells found
        return null;
    }

    public static SpellRegistryEnum nextUnlockedOrCurrent(ServerPlayerEntity player, SpellRegistryEnum current) {
        SpellRegistryEnum next = nextUnlocked(player, current);
        return next != null ? next : current;
    }
}
