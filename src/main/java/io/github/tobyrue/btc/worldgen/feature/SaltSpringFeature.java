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

        BlockPos startPos = findGroundLevel(world, origin);
        if (startPos == null) return false;
        BlockPos groundPos = startPos.down(3);

        double baseRadius = config.radius() * 1.3D;
        double rx = baseRadius + (random.nextDouble() * 2.0 - 1.0);
        double rz = baseRadius + (random.nextDouble() * 2.0 - 1.0);

        int tiers = 2 + random.nextInt(2);
        boolean placed = false;

        int offsetX = 0;
        int offsetZ = 0;

        for (int tier = 0; tier < tiers; tier++) {
            if (tier > 0) {
                offsetX += (random.nextBoolean() ? 1 : -1) * (2 + random.nextInt(2));
                offsetZ += (random.nextBoolean() ? 1 : -1) * (2 + random.nextInt(2));
            }

            int tierYOffset = -(tier * 2);
            BlockPos tierCenter = groundPos.add(offsetX, tierYOffset, offsetZ);

            double tierRx = rx * (1.0 - (tier * 0.15));
            double tierRz = rz * (1.0 - (tier * 0.15));

            Map<BlockPos, Integer> bowlDepths = new HashMap<>();
            Set<BlockPos> rimShellPositions = new HashSet<>();

            int maxSearchX = MathHelper.ceil(tierRx + 3.0);
            int maxSearchZ = MathHelper.ceil(tierRz + 3.0);

            for (int x = -maxSearchX; x <= maxSearchX; x++) {
                for (int z = -maxSearchZ; z <= maxSearchZ; z++) {
                    double normX = x / tierRx;
                    double normZ = z / tierRz;

                    double noise = (Math.sin((x + offsetX) * 0.4D) + Math.cos((z + offsetZ) * 0.4D)) * 0.15D;
                    double distSq = (normX * normX + normZ * normZ) + noise;

                    if (distSq > 1.25D) continue;

                    BlockPos targetPos = tierCenter.add(x, 0, z);

                    if (distSq <= 0.60D) {
                        int depth = distSq < 0.25D ? 3 : 2;
                        bowlDepths.put(targetPos, depth);
                    } else if (distSq <= 1.10D) {
                        rimShellPositions.add(targetPos);
                    }
                }
            }

            if (bowlDepths.isEmpty()) continue;

            for (Map.Entry<BlockPos, Integer> entry : bowlDepths.entrySet()) {
                BlockPos pos = entry.getKey();
                int depth = entry.getValue();

                for (int h = 1; h <= 3; h++) {
                    world.setBlockState(pos.up(h), Blocks.AIR.getDefaultState(), 2);
                }

                for (int d = 0; d < depth; d++) {
                    world.setBlockState(pos.down(d), Blocks.AIR.getDefaultState(), 2);
                }
            }

            for (BlockPos rimPos : rimShellPositions) {
                buildThickRimShell(world, rimPos, config.rimState());
            }

            for (BlockPos bowlPos : bowlDepths.keySet()) {
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    BlockPos neighbor = bowlPos.offset(dir);
                    if (!bowlDepths.containsKey(neighbor)) {
                        buildThickRimShell(world, neighbor, config.rimState());
                    }
                }
            }

            for (BlockPos rimPos : rimShellPositions) {
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    BlockPos outerNeighbor = rimPos.offset(dir);
                    if (!rimShellPositions.contains(outerNeighbor) && !bowlDepths.containsKey(outerNeighbor)) {
                        BlockState current = world.getBlockState(outerNeighbor);
                        if (current.isIn(BlockTags.BASE_STONE_OVERWORLD)) {
                            world.setBlockState(outerNeighbor, config.rimState(), 2);
                        }
                    }
                }
            }

            for (Map.Entry<BlockPos, Integer> entry : bowlDepths.entrySet()) {
                BlockPos corePos = entry.getKey();
                int depth = entry.getValue();

                for (int d = 1; d < depth; d++) {
                    BlockPos fluidPos = corePos.down(d);
                    world.setBlockState(fluidPos, config.fluidState(), 2);
                    world.scheduleFluidTick(fluidPos, config.fluidState().getFluidState().getFluid(), 1);
                }

                world.setBlockState(corePos.down(depth), config.rimState(), 2);
                placed = true;
            }

            for (BlockPos rimPos : rimShellPositions) {
                growSupportPillarIfNeeded(world, rimPos.down(3), config.rimState());
            }

            List<BlockState> crystals = config.crystalStates();
            if (!crystals.isEmpty()) {
                for (BlockPos bowlPos : bowlDepths.keySet()) {
                    for (Direction dir : Direction.Type.HORIZONTAL) {
                        BlockPos wallPos = bowlPos.offset(dir);
                        if (!bowlDepths.containsKey(wallPos) && random.nextFloat() < 0.35f) {
                            BlockState chosenCrystal = crystals.get(random.nextInt(crystals.size()));
                            placeOrientedWallCrystal(world, bowlPos, dir, chosenCrystal);
                        }
                    }
                }
            }
        }

        return placed;
    }

    private BlockPos findGroundLevel(StructureWorldAccess world, BlockPos origin) {
        BlockPos.Mutable mutable = origin.mutableCopy();
        for (int i = 0; i < 40; i++) {
            BlockState current = world.getBlockState(mutable);
            BlockState below = world.getBlockState(mutable.down());

            if (below.isOf(Blocks.LAVA) || current.isOf(Blocks.LAVA)) return null;

            if ((current.isAir() || current.isOf(Blocks.WATER)) && below.isIn(BlockTags.BASE_STONE_OVERWORLD)) {
                return mutable.toImmutable();
            }
            mutable.move(Direction.DOWN);
        }
        return null;
    }

    /**
     * Constructs a multi-block thick shell (raising the lip and sealing bottom gaps).
     */
    private void buildThickRimShell(StructureWorldAccess world, BlockPos pos, BlockState rimState) {
        // Raised Dam Lip (+1 Y)
        world.setBlockState(pos.up(), rimState, 2);
        world.setBlockState(pos, rimState, 2);

        // Deep foundation backfill to prevent bottom leaks
        BlockPos.Mutable mutable = pos.down().mutableCopy();
        for (int i = 0; i < 4; i++) {
            BlockState state = world.getBlockState(mutable);
            if (state.isIn(BlockTags.BASE_STONE_OVERWORLD) || state.isOf(Blocks.TUFF) || state.isOf(Blocks.DEEPSLATE)) {
                world.setBlockState(mutable, rimState, 2);
                break;
            }
            world.setBlockState(mutable, rimState, 2);
            mutable.move(Direction.DOWN);
        }
    }

    /**
     * Grows a structural stone support column downward if the spring overhangs a cave void.
     */
    private void growSupportPillarIfNeeded(StructureWorldAccess world, BlockPos startPos, BlockState rimState) {
        BlockPos.Mutable mutable = startPos.mutableCopy();
        for (int i = 0; i < 6; i++) {
            BlockState state = world.getBlockState(mutable);
            if (state.isAir()) {
                world.setBlockState(mutable, rimState, 2);
            } else if (state.isIn(BlockTags.BASE_STONE_OVERWORLD) || state.isOf(Blocks.TUFF) || state.isOf(Blocks.DEEPSLATE)) {
                break;
            }
            mutable.move(Direction.DOWN);
        }
    }

    /**
     * Places crystals directly onto vertical rim walls with exact FACING directions.
     */
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