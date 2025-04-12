package io.github.tobyrue.btc;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModPotions;
import io.github.tobyrue.btc.regestries.ModSounds;
import io.github.tobyrue.btc.status_effects.MinerMishapEffect;
import io.github.tobyrue.btc.status_effects.BuilderBlunderEffect;
import io.github.tobyrue.btc.status_effects.DragonScalesEffect;
import io.github.tobyrue.btc.status_effects.DrowningEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.client.particle.GustParticle;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class BTC implements ModInitializer {
    public static String MOD_ID = "btc";

    public static final StatusEffect BUILDER_BLUNDER;
    public static final StatusEffect MINER_MISHAP;
    public static final StatusEffect DRAGON_SCALES;
    public static final StatusEffect DROWNING;
    public static final TagKey<Block> WRENCH_BLACKLIST = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "wrench_blacklist"));

    static {
        BUILDER_BLUNDER = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "builder_blunder"), new BuilderBlunderEffect());
        MINER_MISHAP = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "miner_mishap"), new MinerMishapEffect());
        DRAGON_SCALES = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "dragon_scales"), new DragonScalesEffect());
        DROWNING = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "drowning"), new DrowningEffect());
    }
    public static final SimpleParticleType WATER_BLAST = FabricParticleTypes.simple();

    // Register our custom particle type in the mod initializer.
    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();
        ModBlockEntities.initialize();
        ModPotions.initialize();
        ModSounds.initialize();
        FabricDefaultAttributeRegistry.register(ModEntities.ELDRITCH_LUMINARY, EldritchLuminaryEntity.createEldritchLuminaryAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.COPPER_GOLEM, CopperGolemEntity.createCopperGolemAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TUFF_GOLEM, TuffGolemEntity.createTuffGolemAttributes());

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(BTC.BUILDER_BLUNDER)) && !player.isCreative()) {
                ItemStack stack = player.getStackInHand(hand);
                if (stack.getItem() instanceof BlockItem) {
                    return ActionResult.FAIL; // Block placement is prevented
                }
            }
            return ActionResult.PASS; // Other interactions (like opening chests, using tools) are allowed
        });

        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "water_blast"), WATER_BLAST);
        ParticleFactoryRegistry.getInstance().register(BTC.WATER_BLAST, GustParticle.Factory::new);

        //INGREDIENTS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.RUBY_TRIAL_KEY));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.DRAGON_ROD));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModBlocks.CHISELED_COPPER_BRICKS));

        //FUNCTIONAL
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.OMINOUS_BEACON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.PEDESTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.COPPER_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.ANTIER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_FIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.KEY_DISPENSER_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.FIRE_DISPENSER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModItems.IRON_WRENCH));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModItems.GOLD_WRENCH));

        //COMBAT
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.WIND_STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.FIRE_STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.DRAGON_STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.SPELL_BOOK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.WATER_BLAST));

        //REDSTONE
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.COPPER_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.FIRE_DISPENSER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_DOOR));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_PRESSURE_PLATE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.KEY_DISPENSER_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModItems.IRON_WRENCH));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModItems.GOLD_WRENCH));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.UNOXIDIZED_COPPER_BUTTON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.EXPOSED_COPPER_BUTTON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WEATHERED_COPPER_BUTTON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.OXIDIZED_COPPER_BUTTON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON));

        //SPAWN EGGS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> itemGroup.add(ModItems.ELDRITCH_LUMINARY_SPAWN_EGG));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> itemGroup.add(ModItems.COPPER_GOLEM_SPAWN_EGG));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> itemGroup.add(ModItems.TUFF_GOLEM_SPAWN_EGG));

        //BUILDING BLOCKS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((itemGroup) -> itemGroup.add(ModBlocks.CHISELED_COPPER_BRICKS));
    }
    public static void println(Object... args) {
        System.out.println(String.join(" ", java.util.Arrays.stream(args).map(Object::toString).toArray(String[]::new)));
    }
}
