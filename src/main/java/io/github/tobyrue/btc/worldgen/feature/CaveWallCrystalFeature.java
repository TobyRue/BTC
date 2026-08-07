package io.github.tobyrue.btc.worldgen.feature;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class CaveWallCrystalFeature extends Feature<CaveWallCrystalFeatureConfig> {

    public CaveWallCrystalFeature(Codec<CaveWallCrystalFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<CaveWallCrystalFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        CaveWallCrystalFeatureConfig config = context.getConfig();

        boolean anyPlaced = false;
        int placementCount = config.placementAttempts();

        for (int i = 0; i < placementCount; i++) {
            BlockPos targetPos = origin.add(
                    random.nextInt(16) - 8,
                    random.nextInt(16) - 8,
                    random.nextInt(16) - 8
            );

            BlockState current = world.getBlockState(targetPos);
            if (!current.isAir() && !current.isOf(Blocks.WATER)) {
                continue;
            }

            for (Direction wallDir : Direction.values()) {
                BlockPos wallPos = targetPos.offset(wallDir);
                BlockState wallState = world.getBlockState(wallPos);

                if (wallState.isOpaqueFullCube(world, wallPos)) {
                    BlockState crystalState = CaveWallCrystalFeatureConfig.getRandomState(config.crystalStates(), random);
                    if (crystalState == null) break;

                    Direction facing = wallDir.getOpposite();
                    if (crystalState.contains(Properties.FACING)) {
                        crystalState = crystalState.with(Properties.FACING, facing);
                    }

                    if (crystalState.contains(Properties.WATERLOGGED)) {
                        crystalState = crystalState.with(Properties.WATERLOGGED, current.isOf(Blocks.WATER));
                    }

                    if (crystalState.contains(Properties.LIT) && random.nextFloat() < config.litChance()) {
                        crystalState = crystalState.with(Properties.LIT, true);
                    }

                    world.setBlockState(targetPos, crystalState, 2);
                    anyPlaced = true;
                    break;
                }
            }
        }
        return anyPlaced;
    }
}