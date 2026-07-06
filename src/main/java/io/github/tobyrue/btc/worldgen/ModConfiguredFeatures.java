package io.github.tobyrue.btc.worldgen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> SALT_CONFIG_KEY = registerKey("ore_salt");

    public static void configure(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> saltConfig =
                List.of(
                        OreFeatureConfig.createTarget(stoneReplaceables, Blocks.CALCITE.getDefaultState()),
                        OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.SALT_BLOCK.getDefaultState()),
                        OreFeatureConfig.createTarget(deepReplaceables, ModBlocks.SALT_BLOCK.getDefaultState())
                );

        register(context, SALT_CONFIG_KEY, Feature.ORE, new OreFeatureConfig(saltConfig, 48));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(BTC.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}