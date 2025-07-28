package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.SpellScrollItem;
import io.github.tobyrue.btc.spells.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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


    public static final Spell WEAK_FIREBALL = register("weak_fireball", new FireballSpell(1));
    public static final Spell STRONG_FIREBALL = register("strong_fireball", new FireballSpell(5));

    public static final Spell CONCENTRATED_FIRE_STORM = register("concentrated_fire_storm", new FireStormSpell(2, 8, 400));
    public static final Spell FIRE_STORM = register("fire_storm", new FireStormSpell(4, 16, 800));

    public static final Spell RESISTANCE_I = register("resistance_1", new PotionSpell(StatusEffects.RESISTANCE, 0x0, 300, 0, 600));
    public static final Spell RESISTANCE_II = register("resistance_2", new PotionSpell(StatusEffects.RESISTANCE, 0x0, 300, 1, 800));
    public static final Spell RESISTANCE_III = register("resistance_3", new PotionSpell(StatusEffects.RESISTANCE, 0x0, 300, 2, 1200));

    public static final Spell STRENGTH_I = register("strength_1", new PotionSpell(StatusEffects.STRENGTH, 0x0, 300, 0, 400));
    public static final Spell STRENGTH_II = register("strength_2", new PotionSpell(StatusEffects.STRENGTH, 0x0, 300, 1, 600));
    public static final Spell STRENGTH_III = register("strength_3", new PotionSpell(StatusEffects.STRENGTH, 0x0, 300, 2, 800));

    //TODO ETERNAL FIRE

    public static final Spell WIND_CHARGE = register("wind_charge", new WindChargeSpell());
    public static final Spell CLUSTER_WIND_CHARGE = register("cluster_wind_charge", new ClusterWindChargeSpell(8, 0.2));





    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            for (var item : SCROLLS.values()) {
                content.addAfter(ModItems.ENCHANTED_PAPER, item);
            }
        });
    }
}
