package io.github.tobyrue.btc.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        BlockPos groundPos = findGroundLevel(world, origin, 24);
        if (groundPos == null) return false;

        double baseRadius = config.radius() * 1.2D;
        double rx = baseRadius + (random.nextDouble() * 1.5 - 0.75);
        double rz = baseRadius + (random.nextDouble() * 1.5 - 0.75);

        if (!isLocationValid(world, groundPos)) {
            return false;
        }

        int tiers = 2 + random.nextInt(2);
        boolean placed = false;

        int offsetX = 0;
        int offsetZ = 0;

        for (int tier = 0; tier < tiers; tier++) {
            if (tier > 0) {
                offsetX += (random.nextBoolean() ? 1 : -1) * (2 + random.nextInt(2));
                offsetZ += (random.nextBoolean() ? 1 : -1) * (2 + random.nextInt(2));
            }

            double tierRx = rx * (1.0 - (tier * 0.15));
            double tierRz = rz * (1.0 - (tier * 0.15));

            int maxSearchX = MathHelper.ceil(tierRx + 2.0);
            int maxSearchZ = MathHelper.ceil(tierRz + 2.0);

            Map<BlockPos, Integer> bowlDepths = new HashMap<>();
            Set<BlockPos> rimPositions = new HashSet<>();
            Set<BlockPos> blendPositions = new HashSet<>();

            BlockPos unshiftedCenter = groundPos.add(offsetX, -(tier * 2), offsetZ);

            for (int x = -maxSearchX; x <= maxSearchX; x++) {
                for (int z = -maxSearchZ; z <= maxSearchZ; z++) {
                    double normX = x / tierRx;
                    double normZ = z / tierRz;

                    double noise = (Math.sin((x + offsetX) * 0.3D) + Math.cos((z + offsetZ) * 0.3D)) * 0.1D;
                    double distSq = (normX * normX + normZ * normZ) + noise;

                    if (distSq > 1.35D) continue;

                    BlockPos targetPos = unshiftedCenter.add(x, 0, z);

                    if (distSq <= 0.55D) {
                        int depth = distSq < 0.2D ? 3 : 2;
                        bowlDepths.put(targetPos, depth);
                    } else if (distSq <= 1.0D) {
                        rimPositions.add(targetPos);
                    } else {
                        blendPositions.add(targetPos);
                    }
                }
            }

            if (bowlDepths.isEmpty()) continue;

            int maxRimYAboveGround = 0;
            for (BlockPos rimPos : rimPositions) {
                BlockPos floor = findGroundLevel(world, rimPos, 4);
                int floorY = (floor != null) ? floor.getY() : unshiftedCenter.getY();

                int heightAboveGround = rimPos.getY() - floorY;
                if (heightAboveGround > maxRimYAboveGround) {
                    maxRimYAboveGround = heightAboveGround;
                }
            }

            int sinkY = Math.min(2, Math.max(1, maxRimYAboveGround));

            Map<BlockPos, Integer> sunkBowlDepths = new HashMap<>();
            for (Map.Entry<BlockPos, Integer> entry : bowlDepths.entrySet()) {
                sunkBowlDepths.put(entry.getKey().down(sinkY), entry.getValue());
            }

            Set<BlockPos> sunkRimPositions = new HashSet<>();
            for (BlockPos pos : rimPositions) {
                sunkRimPositions.add(pos.down(sinkY));
            }

            Set<BlockPos> sunkBlendPositions = new HashSet<>();
            for (BlockPos pos : blendPositions) {
                sunkBlendPositions.add(pos.down(sinkY));
            }

            for (Map.Entry<BlockPos, Integer> entry : sunkBowlDepths.entrySet()) {
                BlockPos pos = entry.getKey();
                int depth = entry.getValue();

                for (int h = 1; h <= sinkY + 2; h++) {
                    world.setBlockState(pos.up(h), Blocks.AIR.getDefaultState(), 2);
                }

                for (int d = 0; d < depth; d++) {
                    world.setBlockState(pos.down(d), Blocks.AIR.getDefaultState(), 2);
                }
            }

            for (BlockPos rimPos : sunkRimPositions) {
                buildSolidRimWall(world, rimPos, config, random);
            }

            for (BlockPos bowlPos : sunkBowlDepths.keySet()) {
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    BlockPos neighbor = bowlPos.offset(dir);
                    if (!sunkBowlDepths.containsKey(neighbor)) {
                        buildSolidRimWall(world, neighbor, config, random);
                    }
                }
            }

            for (Map.Entry<BlockPos, Integer> entry : sunkBowlDepths.entrySet()) {
                BlockPos corePos = entry.getKey();
                int depth = entry.getValue();

                BlockPos floorPos = corePos.down(depth);
                BlockState floorState = SaltSpringFeatureConfig.getRandomState(config.rimStates(), random, Blocks.CALCITE.getDefaultState());
                world.setBlockState(floorPos, floorState, 2);
                world.setBlockState(floorPos.down(), floorState, 2);
            }

            for (BlockPos blendPos : sunkBlendPositions) {
                BlockPos floor = findGroundLevel(world, blendPos, 3);
                if (floor != null) {
                    BlockState blendState = SaltSpringFeatureConfig.getRandomState(config.rimStates(), random, Blocks.CALCITE.getDefaultState());
                    world.setBlockState(floor, blendState, 2);
                }
            }

            for (Map.Entry<BlockPos, Integer> entry : sunkBowlDepths.entrySet()) {
                BlockPos corePos = entry.getKey();
                int depth = entry.getValue();

                for (int d = 1; d < depth; d++) {
                    BlockPos fluidPos = corePos.down(d);
                    world.setBlockState(fluidPos, config.fluidState(), 2);
                    world.scheduleFluidTick(fluidPos, config.fluidState().getFluidState().getFluid(), 1);
                }

                placed = true;
            }

            List<SaltSpringFeatureConfig.WeightedBlockState> crystals = config.crystalStates();
            if (!crystals.isEmpty()) {
                for (BlockPos bowlPos : sunkBowlDepths.keySet()) {
                    for (Direction dir : Direction.Type.HORIZONTAL) {
                        BlockPos wallPos = bowlPos.offset(dir);
                        if (!sunkBowlDepths.containsKey(wallPos) && random.nextFloat() < 0.35f) {
                            BlockState chosenCrystal = SaltSpringFeatureConfig.getRandomState(crystals, random, Blocks.AMETHYST_CLUSTER.getDefaultState());
                            placeOrientedWallCrystal(world, bowlPos, dir, chosenCrystal);
                        }
                    }
                }
            }
        }

        return placed;
    }

    private boolean isLocationValid(StructureWorldAccess world, BlockPos center) {
        BlockState current = world.getBlockState(center);
        BlockState below = world.getBlockState(center.down());

        if (below.isOf(Blocks.LAVA) || current.isOf(Blocks.LAVA)) return false;
        return !below.isAir();
    }

    private BlockPos findGroundLevel(StructureWorldAccess world, BlockPos origin, int maxSearchDepth) {
        BlockPos.Mutable mutable = origin.mutableCopy();
        for (int i = 0; i < maxSearchDepth; i++) {
            BlockState current = world.getBlockState(mutable);
            BlockState below = world.getBlockState(mutable.down());

            if (below.isOf(Blocks.LAVA) || current.isOf(Blocks.LAVA)) return null;

            boolean isSolidGround = below.isIn(BlockTags.BASE_STONE_OVERWORLD)
                    || below.isOf(Blocks.TUFF)
                    || below.isOf(Blocks.DEEPSLATE)
                    || below.isOf(Blocks.CALCITE)
                    || below.isOf(Blocks.DIRT)
                    || below.isOf(Blocks.GRAVEL)
                    || below.isOf(Blocks.GRANITE)
                    || below.isOf(Blocks.DIORITE)
                    || below.isOf(Blocks.ANDESITE);

            if ((current.isAir() || current.isOf(Blocks.WATER)) && isSolidGround) {
                return mutable.toImmutable();
            }
            mutable.move(Direction.DOWN);
        }
        return null;
    }

    private void buildSolidRimWall(StructureWorldAccess world, BlockPos pos, SaltSpringFeatureConfig config, Random random) {
        BlockState rimState = SaltSpringFeatureConfig.getRandomState(config.rimStates(), random, Blocks.CALCITE.getDefaultState());

        world.setBlockState(pos.up(), rimState, 2);
        world.setBlockState(pos, rimState, 2);

        BlockPos.Mutable mutable = pos.down().mutableCopy();
        for (int i = 0; i < 2; i++) {
            world.setBlockState(mutable, rimState, 2);
            mutable.move(Direction.DOWN);
        }
    }

    private void placeOrientedWallCrystal(StructureWorldAccess world, BlockPos targetAirPos, Direction wallDir, BlockState crystalState) {
        BlockState current = world.getBlockState(targetAirPos);
        if (!current.isAir() && !current.isOf(Blocks.WATER)) return;

        BlockPos wallPos = targetAirPos.offset(wallDir);
        BlockState wallState = world.getBlockState(wallPos);

        if (wallState.isOpaqueFullCube(world, wallPos)) {
            Direction facing = wallDir.getOpposite();

            BlockState orientedState = crystalState;
            if (crystalState.contains(Properties.FACING)) {
                orientedState = crystalState.with(Properties.FACING, facing);
            } else if (crystalState.contains(Properties.HORIZONTAL_FACING) && facing.getAxis().isHorizontal()) {
                orientedState = crystalState.with(Properties.HORIZONTAL_FACING, facing);
            }

            world.setBlockState(targetAirPos, orientedState, 2);
        }
    }
}