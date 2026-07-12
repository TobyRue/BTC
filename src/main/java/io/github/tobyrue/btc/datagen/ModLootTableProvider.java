package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModComponents;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModLootTableProvider extends SimpleFabricLootTableProvider {

    public ModLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup, LootContextTypes.CHEST);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter) {

        var a = new NbtCompound();
        a.putInt("level", 1);
        var b = new NbtCompound();
        b.putInt("duration", 200);
        b.putInt("amplifier", 1);
        b.putString("effect", "minecraft:regeneration");

        LootTable.Builder boatChest = LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.SPELLSTONE)
                                .apply(applySpell("btc:disspell", 300, new NbtCompound()))
                                ))

                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.SPELLSTONE)
                                .apply(applySpell("btc:fireball", 300, a))
                                ))

                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.SPELLSTONE)
                                .apply(applySpell("btc:potion", 1200, b))
                                ))

                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                        .with(EmptyEntry.builder().weight(60))
                        .with(ItemEntry.builder(ModItems.SUN_ARMOR_TRIM).weight(10))
                        .with(ItemEntry.builder(ModItems.ELDRITCH_ARMOR_TRIM).weight(10)))

                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                        .with(EmptyEntry.builder().weight(5))
                        .with(ItemEntry.builder(Items.IRON_PICKAXE).weight(10).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1f, 0.5f))))
                        .with(ItemEntry.builder(Items.IRON_SWORD).weight(10).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.0f, 0.4f))))
                        .with(ItemEntry.builder(Items.IRON_AXE).weight(8).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1f, 0.6f)))))

                .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(5, 8))
                        .with(ItemEntry.builder(Items.COOKED_PORKCHOP).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 12))))
                        .with(ItemEntry.builder(Items.COOKED_BEEF).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 12))))
                        .with(ItemEntry.builder(Items.COOKED_MUTTON).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 8))))
                        .with(ItemEntry.builder(Items.BAKED_POTATO).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(6, 16))))
                        .with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 8))))
                        .with(ItemEntry.builder(Items.APPLE).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6))))
                        .with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))))
                        .with(ItemEntry.builder(Items.GUNPOWDER).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(12, 32))))
                        .with(ItemEntry.builder(Items.RAW_COPPER).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(16, 32))))
                        .with(ItemEntry.builder(Items.RAW_IRON).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 20))))
                        .with(ItemEntry.builder(Items.CHARCOAL).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 16))))
                        .with(ItemEntry.builder(Items.COAL).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(6, 18))))
                        .with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 8))))
                        .with(ItemEntry.builder(Items.FEATHER).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 12))))
                        .with(ItemEntry.builder(Items.FLINT).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6))))
                        .with(ItemEntry.builder(Items.LEATHER).weight(8).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))))
                        .with(ItemEntry.builder(Items.TORCH).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 24))))
                        .with(ItemEntry.builder(Items.AMETHYST_SHARD).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 5))))
                        .with(ItemEntry.builder(Items.LAPIS_LAZULI).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 12))))
                        .with(ItemEntry.builder(ModItems.EMPTY_SCROLL).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))))
                        .with(ItemEntry.builder(ModItems.AMETHYST_LENS).weight(7).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)))));

        exporter.accept(RegistryKey.of(net.minecraft.registry.RegistryKeys.LOOT_TABLE, Identifier.of("btc", "chests/surface_indicator/ocean/boat_supply")), boatChest);


        LootTable.Builder explosivesCache = LootTable.builder()
                .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(3, 6))
                        .with(ItemEntry.builder(Items.GUNPOWDER).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 16))))
                        .with(ItemEntry.builder(Items.TNT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3))))
                        .with(ItemEntry.builder(Items.FIREWORK_ROCKET).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 8))))
                        .with(ItemEntry.builder(Items.SAND).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 24))))
                        .with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6)))));

        exporter.accept(RegistryKey.of(net.minecraft.registry.RegistryKeys.LOOT_TABLE, Identifier.of("btc", "chests/surface_indicator/ocean/explosive_cache")), explosivesCache);


        LootTable.Builder fishBarrel = LootTable.builder()
                .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(2, 5))
                        .with(ItemEntry.builder(Items.COD).weight(25).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6))))
                        .with(ItemEntry.builder(Items.SALMON).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))))
                        .with(ItemEntry.builder(Items.TROPICAL_FISH).weight(5))
                        .with(ItemEntry.builder(Items.PUFFERFISH).weight(5))
                        .with(ItemEntry.builder(ModItems.TRIAL_JERKY).weight(20).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5, 16))))
                        .with(ItemEntry.builder(ModItems.COOKED_MEAT_CLUB).weight(10))
                        .with(ItemEntry.builder(Items.KELP).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 12))))
                        .with(ItemEntry.builder(Items.FISHING_ROD).weight(5).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.0f, 0.7f)))));

        exporter.accept(RegistryKey.of(net.minecraft.registry.RegistryKeys.LOOT_TABLE, Identifier.of("btc", "chests/surface_indicator/ocean/fish_barrel")), fishBarrel);


        LootTable.Builder treasureLootRoom = LootTable.builder()
                .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(3, 6))
                        .with(ItemEntry.builder(Items.GOLD_INGOT).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 9))))
                        .with(ItemEntry.builder(Items.GOLD_BLOCK).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))))
                        .with(ItemEntry.builder(Items.DIAMOND).weight(8).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3))))
                        .with(ItemEntry.builder(Items.EMERALD).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 12))))
                        .with(ItemEntry.builder(Items.TRIAL_KEY).weight(5))
                        .with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 6)))));

        exporter.accept(RegistryKey.of(net.minecraft.registry.RegistryKeys.LOOT_TABLE, Identifier.of("btc", "chests/surface_indicator/ocean/treasure_loot_room")), treasureLootRoom);


        LootTable.Builder potDrops = LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                        .with(EmptyEntry.builder().weight(30))
                        .with(ItemEntry.builder(Items.BRICK).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3))))
                        .with(ItemEntry.builder(Items.IRON_NUGGET).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 5))))
                        .with(ItemEntry.builder(Items.GOLD_NUGGET).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 7))))
                        .with(ItemEntry.builder(Items.GLOWSTONE_DUST).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))))
                        .with(ItemEntry.builder(Items.BONE_MEAL).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)))));

        exporter.accept(RegistryKey.of(net.minecraft.registry.RegistryKeys.LOOT_TABLE, Identifier.of("btc", "archaeology/surface_indicator/ocean/pot_assorted")), potDrops);

        LootTable.Builder commonChest = LootTable.builder()
                .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(2, 4))
                        .with(ItemEntry.builder(Items.IRON_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 5))))
                        .with(ItemEntry.builder(Items.IRON_NUGGET).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4, 9))))
                        .with(ItemEntry.builder(Items.BONE).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))))
                        .with(ItemEntry.builder(Items.ARROW).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 8)))))

                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                        .with(EmptyEntry.builder().weight(40))
                        .with(ItemEntry.builder(Items.IRON_HELMET).weight(10).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1f, 0.5f))))
                        .with(ItemEntry.builder(Items.IRON_BOOTS).weight(10).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1f, 0.5f))))
                        .with(ItemEntry.builder(Items.BUCKET).weight(15))
                        .with(ItemEntry.builder(Items.COMPASS).weight(5)));

        exporter.accept(RegistryKey.of(net.minecraft.registry.RegistryKeys.LOOT_TABLE, Identifier.of("btc", "chests/surface_indicator/ocean/common_chest")), commonChest);
    }
    private static net.minecraft.loot.function.LootFunction.Builder applySpell(String name, int cooldown, NbtCompound args) {
        return SetComponentsLootFunction.builder(ModComponents.SPELL_COMPONENT, createSpellComponent(name, cooldown, args));
    }

    private static NbtComponent createSpellComponent(String name, int cooldown, NbtCompound args) {
        NbtCompound spellTag = new NbtCompound();
        spellTag.putString("name", name);

        args.putInt("cooldown", cooldown);

        spellTag.put("args", args);
        spellTag.put("cooldowns", new NbtCompound());

        return NbtComponent.of(spellTag);
    }
}