package io.github.tobyrue.btc.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientOreRadar {
    private static final List<BlockPos> cachedOres = new ArrayList<>();
    private static int remainingTrackingTicks = 0;
    private static final int maxOres = 1;

    public static void startScanning(List<BlockPos> ores, int durationTicks) {
        cachedOres.clear();
        cachedOres.addAll(ores);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            Vec3d playerPos = client.player.getPos();
            cachedOres.sort(java.util.Comparator.comparingDouble(pos ->
                    playerPos.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
            ));
        }

        if (cachedOres.size() > maxOres) {
            cachedOres.subList(maxOres, cachedOres.size()).clear();
        }

        remainingTrackingTicks = durationTicks;
    }

    public static void clientTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || remainingTrackingTicks <= 0) {
            return;
        }

        remainingTrackingTicks--;

        Vec3d startPos = client.player.getPos().add(0, 0.25, 0);

        for (BlockPos orePos : cachedOres) {
            Vec3d endPos = new Vec3d(orePos.getX() + 0.5, orePos.getY() + 0.5, orePos.getZ() + 0.5);

            double distance = startPos.distanceTo(endPos);
            if (distance > 24.0) continue;

            int particleCount = (int) (distance * 2);
            for (int i = 0; i <= particleCount; i++) {
                float delta = (float) i / particleCount;
                double px = startPos.x + (endPos.x - startPos.x) * delta;
                double py = startPos.y + (endPos.y - startPos.y) * delta;
                double pz = startPos.z + (endPos.z - startPos.z) * delta;

                client.world.addParticle(
                        ParticleTypes.GLOW,
                        px, py, pz,
                        0.0, 0.0, 0.0
                );
            }
        }
    }
}