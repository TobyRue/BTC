package io.github.tobyrue.btc.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SaltPatchFeature extends Feature<SaltPatchFeatureConfig> {

    public SaltPatchFeature(Codec<SaltPatchFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<SaltPatchFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        SaltPatchFeatureConfig config = context.getConfig();

        double rx = config.radiusX() + random.nextDouble() * 2.0 - 1.0;
        double ry = config.radiusY() + random.nextDouble() * 2.0 - 1.0;
        double rz = config.radiusZ() + random.nextDouble() * 2.0 - 1.0;

        int maxX = MathHelper.ceil(rx + 2);
        int maxY = MathHelper.ceil(ry + 2);
        int maxZ = MathHelper.ceil(rz + 2);

        boolean placedAny = false;

        for (BlockPos pos : BlockPos.iterate(origin.add(-maxX, -maxY, -maxZ), origin.add(maxX, maxY, maxZ))) {

            if (!world.isValidForSetBlock(pos)) {
                continue;
            }

            double dx = (pos.getX() - origin.getX()) / rx;
            double dy = (pos.getY() - origin.getY()) / ry;
            double dz = (pos.getZ() - origin.getZ()) / rz;

            double noise = (Math.sin(pos.getX() * 0.3) + Math.cos(pos.getZ() * 0.3) + Math.sin(pos.getY() * 0.5)) * 0.12;
            double distSq = (dx * dx + dy * dy + dz * dz) + noise;

            if (distSq > 1.2) continue;

            BlockState current = world.getBlockState(pos);

            if (!current.isIn(BlockTags.BASE_STONE_OVERWORLD)) {
                continue;
            }

            BlockState stateToPlace;
            if (distSq <= 0.35) {
                stateToPlace = config.saltState();
            } else if (distSq <= 0.35 + (config.saltyCalciteThickness() * 0.08)) {
                stateToPlace = config.saltyCalciteState();
            } else if (distSq <= 0.85) {
                stateToPlace = config.calciteState();
            } else if (distSq <= 1.15) {
                stateToPlace = config.tuffState();
            } else {
                continue;
            }

            world.setBlockState(pos, stateToPlace, 2);
            placedAny = true;
        }

        return placedAny;
    }
}