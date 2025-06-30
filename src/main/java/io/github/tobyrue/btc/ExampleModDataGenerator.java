package io.github.tobyrue.btc;

import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.SpellScrollItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.*;
import net.minecraft.data.client.*;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ExampleModDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(ModModelProvider::new);
//        pack.addProvider(AdvancementsProvider::new);

        // Adding a provider example:
    }
    public static class ModModelProvider extends FabricModelProvider {
        public ModModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            for (SpellScrollItem spell : ModItems.SPELL_ITEMS.values()) {
                if (!spell.spellType.isStartingSpell) {
                    Models.HANDHELD.upload(
                            ModelIds.getItemModelId(spell),
                            TextureMap.layer0(BTC.identifierOf(String.format("item/%s_scroll", spell.spellType.getSpellType().toString()))),
                            itemModelGenerator.writer
                    );
                }
            }
        }

    }
//    static class AdvancementsProvider extends FabricAdvancementProvider {
//        public AdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
//            super(output, registryLookup);
//        }
//
//        @Override
//        public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
//            AdvancementEntry rootAdvancement = Advancement.Builder.create()
//                    .display(
//                            Items.DIRT, // The display icon
//                            Text.literal("Your First Dirt Block"), // The title
//                            Text.literal("Now make a three by three"), // The description
//                            Identifier.of("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
//                            AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
//                            true, // Show toast top right
//                            true, // Announce to chat
//                            false // Hidden in the advancement tab
//                    )
//
//                    // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
//                    .criterion("got_dirt", UsingItemCriterion.Conditions.items(Items.DIRT))
//                    .build(consumer, "your_mod_id_please_change_me" + "/root");
//        }
//    }

}
