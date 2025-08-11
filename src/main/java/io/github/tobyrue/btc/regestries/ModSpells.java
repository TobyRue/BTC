package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.SpellScrollItem;
import io.github.tobyrue.btc.spells.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ModSpells {
    public static final Map<Spell, SpellScrollItem> SCROLLS = new HashMap<>();

    public static <T extends Spell> T register(String name, T spell, boolean shouldRegisterItem) {
        if (shouldRegisterItem) {
            SCROLLS.put(spell, Registry.register(Registries.ITEM, Identifier.of(BTC.MOD_ID, name + "_scroll"), new SpellScrollItem(spell)));
        }

        return Registry.register(ModRegistries.SPELL, Identifier.of(BTC.MOD_ID, name), spell);
    }

    public static <T extends Spell> T register(String name, T spell) {
        return register(name, spell, true);
    }


    public static final Spell FIREBALL = register("fireball", new FireballSpell());

    public static final Spell FIRE_STORM = register("fire_storm", new FireStormSpell());

    public static final Spell POTION = register("potion", new PotionSpell());
    public static final Spell POTION_AREA_EFFECT = register("potion_area_effect", new PotionAreaEffectSpell());

    //TODO ETERNAL FIRE

    public static final Spell WIND_CHARGE = register("wind_charge", new WindChargeSpell());
    public static final Spell CLUSTER_WIND_CHARGE = register("cluster_wind_charge", new ClusterWindChargeSpell());
    public static final Spell TEMPESTS_CALL = register("tempests_call", new TempestsCallSpell());
    public static final Spell STORM_PUSH = register("storm_push", new StormPushSpell());
    public static final Spell LOCALIZED_STORM_PUSH = register("localized_storm_push", new LocalizedStormPushSpell());
    public static final Spell WIND_TORNADO = register("wind_tornado", new WindTornadoSpell());


    public static final Spell ENDER_PEARL = register("ender_pearl", new EnderPearlSpell());
    public static final Spell DRAGONS_BREATH = register("dragon_fireball", new DragonFireballSpell());
    public static final Spell LIFE_STEAL = register("life_steal", new LifeStealSpell());
    public static final Spell ENDER_CHEST = register("ender_chest", new EnderChestSpell());

    public static final Spell EARTH_SPIKE_LINE = register("earth_spike_line", new EarthSpikeLineSpell());
    public static final Spell CREEPER_WALL_CIRCLE = register("creeper_wall_circle", new CreeperWallCircleSpell());
    public static final Spell CREEPER_WALL_EXPLOSIVE_TRAP = register("creeper_wall_explosive_trap", new CreeperWallExplosiveTrapSpell());
    public static final Spell CREEPER_WALL_BLOCK = register("creeper_wall_block", new CreeperWallBlockSpell());

    public static final Spell ICE_BLOCK = register("ice_block", new IceBlockSpell());
    public static final Spell WATER_WAVE = register("water_wave", new WaterWaveSpell());
    public static final Spell WATER_BLAST = register("water_blast", new WaterBlastSpell());
    public static final Spell GEYSER_STEP = register("geyser_step", new GeyserStepSpell());
    public static final Spell MIST_VEIL = register("mist_veil", new MistVeilSpell());


    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            for (var item : SCROLLS.values()) {
                content.addAfter(ModItems.ENCHANTED_PAPER, item);
            }
        });
    }
}
