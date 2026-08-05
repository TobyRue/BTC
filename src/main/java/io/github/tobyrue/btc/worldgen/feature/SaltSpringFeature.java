package io.github.tobyrue.btc.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.List;

public class SaltSpringFeature extends Feature<SaltSpringFeatureConfig> {

    public SaltSpringFeature(Codec<SaltSpringFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<SaltSpringFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        SaltSpringFeatureConfig config = context.getConfig();

        BlockPos.Mutable mutablePos = origin.mutableCopy();
        boolean foundGround = false;

        for (int i = 0; i < 40; i++) {
            BlockState current = world.getBlockState(mutablePos);
            BlockState below = world.getBlockState(mutablePos.down());

            if (below.isOf(Blocks.LAVA) || current.isOf(Blocks.LAVA)) {
                return false;
            }

            if ((current.isAir() || current.isOf(Blocks.WATER)) && below.isIn(BlockTags.BASE_STONE_OVERWORLD)) {
                foundGround = true;
                break;
            }
            mutablePos.move(0, -1, 0);
        }

        if (!foundGround) return false;

        BlockPos groundPos = mutablePos.toImmutable();

        double baseRadius = config.radius() * 1.2D;
        double rx = baseRadius + (random.nextDouble() * 2.5 - 1.25);
        double rz = baseRadius + (random.nextDouble() * 2.5 - 1.25);

        int maxSearchX = MathHelper.ceil(rx + 3.0);
        int maxSearchZ = MathHelper.ceil(rz + 3.0);

        if (!hasBasicGroundSupport(world, groundPos, rx, rz, maxSearchX, maxSearchZ)) {
            return false;
        }

        if (!isBasinFullyContained(world, groundPos, rx, rz, maxSearchX, maxSearchZ)) {
            return false;
        }

        int layers = 2 + random.nextInt(2);
        boolean placed = false;
        List<BlockState> crystals = config.crystalStates();

        for (int layer = 0; layer < layers; layer++) {
            double layerRx = rx * (1.0 - (layer * 0.22));
            double layerRz = rz * (1.0 - (layer * 0.22));

            int maxDepth = 1 + layer;

            for (int x = -maxSearchX; x <= maxSearchX; x++) {
                for (int z = -maxSearchZ; z <= maxSearchZ; z++) {
                    double normX = x / layerRx;
                    double normZ = z / layerRz;

                    double noise = (Math.sin(x * 0.35) + Math.cos(z * 0.35)) * 0.18;
                    double distSq = (normX * normX + normZ * normZ) + noise;

                    if (distSq > 1.15) continue;

                    BlockPos localGround = getLocalFloor(world, groundPos.add(x, layer, z));
                    if (localGround == null) continue;

                    BlockPos poolSurface = localGround.up();

                    if (!world.isValidForSetBlock(poolSurface)) continue;

                    if (distSq <= 0.50) {
                        for (int d = 0; d < maxDepth; d++) {
                            BlockPos depthPos = poolSurface.down(d);
                            world.setBlockState(depthPos, config.fluidState(), 2);
                            world.scheduleFluidTick(depthPos, config.fluidState().getFluidState().getFluid(), 1);

                            sealBasinAndBuildWalls(world, depthPos, config.rimState());
                        }

                        world.setBlockState(poolSurface.down(maxDepth), config.rimState(), 2);
                        placed = true;

                    } else if (distSq <= 1.0) {
                        world.setBlockState(poolSurface, config.rimState(), 2);
                        world.setBlockState(poolSurface.down(), config.rimState(), 2);

                        buildFoundationDownSlope(world, poolSurface, config.rimState());

                        BlockPos abovePos = poolSurface.up();
                        if (world.getBlockState(abovePos).isAir() && !crystals.isEmpty() && random.nextFloat() < 0.40f) {
                            BlockState chosenCrystal = crystals.get(random.nextInt(crystals.size()));
                            world.setBlockState(abovePos, chosenCrystal, 2);
                        }
                        placed = true;
                    }
                }
            }
        }

        return placed;
    }

    /**
     * Pre-generation containment test: Verifies that no fluid block will sit next to an unsealed air gap or cliff edge.
     */
    private boolean isBasinFullyContained(StructureWorldAccess world, BlockPos origin, double rx, double rz, int maxX, int maxZ) {
        for (int x = -maxX; x <= maxX; x++) {
            for (int z = -maxZ; z <= maxZ; z++) {
                double normX = x / rx;
                double normZ = z / rz;
                double distSq = normX * normX + normZ * normZ;

                if (distSq <= 0.50) {
                    BlockPos localFloor = getLocalFloor(world, origin.add(x, 0, z));
                    if (localFloor == null) return false;

                    BlockPos fluidPos = localFloor.up();

                    for (Direction dir : Direction.Type.HORIZONTAL) {
                        BlockPos checkPos = fluidPos.offset(dir);
                        BlockState state = world.getBlockState(checkPos);
                        BlockState stateBelow = world.getBlockState(checkPos.down());

                        if (state.isAir() && stateBelow.isAir()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean hasBasicGroundSupport(StructureWorldAccess world, BlockPos origin, double rx, double rz, int maxX, int maxZ) {
        int solidFloorCount = 0;
        int totalChecked = 0;

        for (int x = -maxX; x <= maxX; x += 2) {
            for (int z = -maxZ; z <= maxZ; z += 2) {
                if ((x / rx) * (x / rx) + (z / rz) * (z / rz) <= 1.0) {
                    totalChecked++;
                    BlockPos floor = getLocalFloor(world, origin.add(x, 0, z));
                    if (floor != null) {
                        solidFloorCount++;
                    }
                }
            }
        }
        return totalChecked > 0 && ((double) solidFloorCount / totalChecked) >= 0.80D;
    }

    private BlockPos getLocalFloor(StructureWorldAccess world, BlockPos start) {
        BlockPos.Mutable mutable = start.mutableCopy();
        for (int i = 0; i < 4; i++) {
            BlockState state = world.getBlockState(mutable);
            BlockState below = world.getBlockState(mutable.down());
            if (below.isIn(BlockTags.BASE_STONE_OVERWORLD) || below.isOf(Blocks.TUFF) || below.isOf(Blocks.DEEPSLATE)) {
                return mutable.down().toImmutable();
            }
            if (state.isOf(Blocks.LAVA) || below.isOf(Blocks.LAVA)) {
                return null;
            }
            mutable.move(0, -1, 0);
        }
        return null;
    }

    /**
     * Builds retaining stone walls directly around any placed fluid block if a neighbor is empty/air.
     */
    private void sealBasinAndBuildWalls(StructureWorldAccess world, BlockPos fluidPos, BlockState rimState) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos neighborPos = fluidPos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isAir()) {
                world.setBlockState(neighborPos, rimState, 2);
            }
        }
    }

    /**
     * Fills down into open air beneath rim blocks to create solid retaining foundations.
     */
    private void buildFoundationDownSlope(StructureWorldAccess world, BlockPos rimPos, BlockState rimState) {
        BlockPos.Mutable downCheck = rimPos.down().mutableCopy();
        for (int drop = 0; drop < 4; drop++) {
            if (world.getBlockState(downCheck).isAir()) {
                world.setBlockState(downCheck, rimState, 2);
                downCheck.move(Direction.DOWN);
            } else {
                break;
            }
        }
    }

    //TODO Fix terracing
}