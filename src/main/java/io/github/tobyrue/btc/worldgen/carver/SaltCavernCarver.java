package io.github.tobyrue.btc.worldgen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.chunk.AquiferSampler;

import java.util.function.Function;

public class SaltCavernCarver extends CaveCarver {

    public SaltCavernCarver(Codec<CaveCarverConfig> codec) {
        super(codec);
    }

    @Override
    public boolean shouldCarve(CaveCarverConfig config, Random random) {
        return random.nextFloat() <= config.probability;
    }

    @Override
    public boolean carve(CarverContext carverContext, CaveCarverConfig caveCarverConfig, Chunk chunk,
                         Function<BlockPos, RegistryEntry<Biome>> posToBiome, Random random,
                         AquiferSampler aquiferSampler, ChunkPos chunkPos, CarvingMask carvingMask) {

        int branchLengthFactor = ChunkSectionPos.getBlockCoord(this.getBranchFactor());
        int caveCount = random.nextInt(2) + 1;

        for (int k = 0; k < caveCount; ++k) {
            double x = (double) chunkPos.getOffsetX(random.nextInt(16));
            double y = (double) caveCarverConfig.y.get(random, carverContext);
            double z = (double) chunkPos.getOffsetZ(random.nextInt(16));

            double hScale = caveCarverConfig.horizontalRadiusMultiplier.get(random);
            double vScale = caveCarverConfig.verticalRadiusMultiplier.get(random);
            double floorLvl = caveCarverConfig.floorLevel.get(random);

            double noiseOffsetX = random.nextDouble() * 1000.0;
            double noiseOffsetZ = random.nextDouble() * 1000.0;

            Carver.SkipPredicate skipPredicate = (context, scaledX, scaledY, scaledZ, absoluteY) -> {
                double canyonDist = scaledX * scaledX + scaledZ * scaledZ;
                if (canyonDist >= 1.0D || scaledY <= floorLvl) {
                    return true;
                }

                double worldX = x + scaledX * hScale + noiseOffsetX;
                double worldZ = z + scaledZ * hScale + noiseOffsetZ;

                // tighter frequency + lower threshold = more, closer-together columns
                double pillarNoise = Math.sin(worldX * 0.35D) * Math.cos(worldZ * 0.35D)
                        + Math.sin(worldX * 0.15D + worldZ * 0.15D) * 0.4D;

                if (pillarNoise > 0.0D) {
                    return true;
                }

                double ledgeNoise = Math.sin(worldX * 0.1D) + Math.cos(worldZ * 0.1D);
                boolean isNearLedgeHeight = (absoluteY % 10 == 0) || (absoluteY == -32 || absoluteY == 0);
                if (isNearLedgeHeight && ledgeNoise > 0.35D && canyonDist > 0.40D) {
                    return true;
                }

                return false;
            };

            double yScale = caveCarverConfig.yScale.get(random);

            // big chambers: was a guaranteed-but-small chamber before; now there's a
            // chance per cave of a noticeably larger chamber, so large caves show up
            // more often and read as more distinct in size from the tunnels
            if (random.nextInt(3) != 0) {
                float caveWidth = 6.0F + random.nextFloat() * 10.0F;
                this.carveCave(carverContext, caveCarverConfig, chunk, posToBiome, aquiferSampler, x, y, z, caveWidth, yScale, carvingMask, skipPredicate);
            } else {
                float caveWidth = 12.0F + random.nextFloat() * 14.0F;
                this.carveCave(carverContext, caveCarverConfig, chunk, posToBiome, aquiferSampler, x, y, z, caveWidth, yScale, carvingMask, skipPredicate);
            }

            int branches = 3 + random.nextInt(3);
            for (int p = 0; p < branches; ++p) {
                float yaw = random.nextFloat() * ((float) Math.PI * 2F);
                float pitch = (random.nextFloat() - 0.5F) * 0.2F;
                float width = 2.0F + random.nextFloat() * 3.5F;
                int branchLen = branchLengthFactor + random.nextInt(branchLengthFactor);

                this.carveTunnels(carverContext, caveCarverConfig, chunk, posToBiome, random.nextLong(),
                        aquiferSampler, x, y, z, hScale, vScale, width, yaw, pitch, 0, branchLen,
                        this.getTunnelSystemHeightWidthRatio(), carvingMask, skipPredicate);
            }
        }

        return true;
    }

    @Override
    protected int getMaxCaveCount() {
        return 2;
    }

    @Override
    protected float getTunnelSystemWidth(Random random) {
        return random.nextFloat() * 3.0F + 2.0F;
    }

    @Override
    protected double getTunnelSystemHeightWidthRatio() {
        return 3.2D;
    }

    @Override
    protected void carveCave(CarverContext context, CaveCarverConfig config, Chunk chunk,
                             Function<BlockPos, RegistryEntry<Biome>> posToBiome, AquiferSampler aquiferSampler,
                             double x, double y, double z, float width, double yScale,
                             CarvingMask mask, Carver.SkipPredicate skipPredicate) {
        double horizontalRadius = 2.0D + (double)(MathHelper.sin(((float) Math.PI / 2F)) * width);
        double verticalRadius = horizontalRadius * yScale;
        this.carveRegion(context, config, chunk, posToBiome, aquiferSampler, x + 1.0D, y, z, horizontalRadius, verticalRadius, mask, skipPredicate);
    }

    @Override
    protected void carveTunnels(CarverContext context, CaveCarverConfig config, Chunk chunk,
                                Function<BlockPos, RegistryEntry<Biome>> posToBiome, long seed,
                                AquiferSampler aquiferSampler, double x, double y, double z,
                                double horizontalScale, double verticalScale, float width, float yaw, float pitch,
                                int branchStartIndex, int branchCount, double yawPitchRatio,
                                CarvingMask mask, Carver.SkipPredicate skipPredicate) {
        Random random = Random.create(seed);

        if (branchCount <= 0 || branchStartIndex >= branchCount) {
            return;
        }

        int splitPoint = random.nextInt(Math.max(1, branchCount / 2)) + branchCount / 4;
        float yawChange = 0.0F;
        float pitchChange = 0.0F;

        for (int j = branchStartIndex; j < branchCount; ++j) {
            double widthProgress = MathHelper.sin((float) Math.PI * (float) j / (float) branchCount);
            double hRad = 1.2D + (double)(widthProgress * width);
            double vRad = hRad * yawPitchRatio;
            float cosPitch = MathHelper.cos(pitch);

            x += (double)(MathHelper.cos(yaw) * cosPitch);
            y += (double)MathHelper.sin(pitch);
            z += (double)(MathHelper.sin(yaw) * cosPitch);

            pitch *= 0.8D;
            pitch += pitchChange * 0.05F;
            yaw += yawChange * 0.1F;
            pitchChange *= 0.8F;
            yawChange *= 0.85F;
            pitchChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 1.5F;
            yawChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 3.0F;

            if (j == splitPoint && width > 1.5F) {
                float splitWidth = width * 0.5F;
                this.carveTunnels(context, config, chunk, posToBiome, random.nextLong(), aquiferSampler, x, y, z,
                        horizontalScale, verticalScale, splitWidth, yaw - ((float) Math.PI / 2F),
                        pitch / 3.0F, j + 1, branchCount, 1.0D, mask, skipPredicate);
                this.carveTunnels(context, config, chunk, posToBiome, random.nextLong(), aquiferSampler, x, y, z,
                        horizontalScale, verticalScale, splitWidth, yaw + ((float) Math.PI / 2F),
                        pitch / 3.0F, j + 1, branchCount, 1.0D, mask, skipPredicate);
                return;
            }

            if (random.nextInt(4) != 0) {
                if (!canCarveBranch(chunk.getPos(), x, z, j, branchCount, width)) {
                    return;
                }
                this.carveRegion(context, config, chunk, posToBiome, aquiferSampler, x, y, z, hRad * horizontalScale, vRad * verticalScale, mask, skipPredicate);
            }
        }
    }
}