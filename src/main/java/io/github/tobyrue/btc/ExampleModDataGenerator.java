package io.github.tobyrue.btc;

import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.SpellScrollItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

public class ExampleModDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(ModModelProvider::new);

        // Adding a provider example:
        //
        // pack.addProvider(AdvancementsProvider::new);
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
}
