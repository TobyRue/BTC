package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.SpyGlassBlock;
import io.github.tobyrue.btc.block.entities.SpyGlassBlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpyGlassCameraController {
    private static boolean active = false;
    private static BlockPos targetBlockPos = null;

    private static float baseYaw = 0.0F;
    private static float basePitch = 0.0F;
    private static float originalPlayerYaw = 0.0F;
    private static float originalPlayerPitch = 0.0F;

    private static double zoomFactor = 0.5D;
    private static final double MIN_ZOOM = 0.1D;
    private static final double MAX_ZOOM = 1.0D;
    private static final double SCROLL_SENSITIVITY = 0.05D;


    public static boolean handleMouseScroll(double amount) {
        if (!active) return false;

        zoomFactor -= amount * SCROLL_SENSITIVITY;
        zoomFactor = MathHelper.clamp(zoomFactor, MIN_ZOOM, MAX_ZOOM);

        return true;
    }

    public static void startZooming(BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        active = true;
        targetBlockPos = pos;
        zoomFactor = 0.5D;

        originalPlayerYaw = client.player.getYaw();
        originalPlayerPitch = client.player.getPitch();

        baseYaw = client.player.getHeadYaw();
        basePitch = MathHelper.clamp(client.player.getPitch(), -SpyGlassBlockEntity.MAX_PITCH_LIMIT, SpyGlassBlockEntity.MAX_PITCH_LIMIT);

        client.player.setYaw(baseYaw);
        client.player.setPitch(basePitch);

        if (client.world.getBlockEntity(pos) instanceof SpyGlassBlockEntity spyglass) {
            spyglass.syncAngles(0.0F, 0.0F);
        }
    }

    public static void stopZooming() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.setYaw(originalPlayerYaw);
            client.player.setPitch(originalPlayerPitch);
        }
        active = false;
        targetBlockPos = null;
    }

    public static boolean isActive() {
        return active;
    }

    public static void updateTelescopeRotation() {
        if (!active || targetBlockPos == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        if (client.world.getBlockEntity(targetBlockPos) instanceof SpyGlassBlockEntity spyglass) {
            BlockFace location = client.world.getBlockState(targetBlockPos).get(SpyGlassBlock.FACE);

            float deltaYaw = MathHelper.wrapDegrees(client.player.getYaw() - baseYaw);
            float playerPitch = client.player.getPitch();

            if (deltaYaw > SpyGlassBlockEntity.MAX_YAW_LIMIT) {
                baseYaw = MathHelper.wrapDegrees(client.player.getYaw() - SpyGlassBlockEntity.MAX_YAW_LIMIT);
                deltaYaw = SpyGlassBlockEntity.MAX_YAW_LIMIT;
            } else if (deltaYaw < -SpyGlassBlockEntity.MAX_YAW_LIMIT) {
                baseYaw = MathHelper.wrapDegrees(client.player.getYaw() + SpyGlassBlockEntity.MAX_YAW_LIMIT);
                deltaYaw = -SpyGlassBlockEntity.MAX_YAW_LIMIT;
            }

            playerPitch = MathHelper.clamp(playerPitch, -SpyGlassBlockEntity.MAX_PITCH_LIMIT, SpyGlassBlockEntity.MAX_PITCH_LIMIT);

            client.player.setYaw(MathHelper.wrapDegrees(baseYaw + deltaYaw));
            client.player.setPitch(playerPitch);

            Direction facing = client.world.getBlockState(targetBlockPos).get(SpyGlassBlock.FACING);
            float absoluteYawOffset = MathHelper.wrapDegrees(client.player.getYaw() - facing.asRotation());

            float targetBlockYaw = absoluteYawOffset;
            float targetBlockPitch = playerPitch;

            if (location == BlockFace.CEILING) {
                targetBlockYaw = -targetBlockYaw;
            } else if (location == BlockFace.WALL) {
                targetBlockYaw = playerPitch;
                targetBlockPitch = absoluteYawOffset + 90;
            }

            spyglass.syncAngles(targetBlockPitch, targetBlockYaw);
        }
    }

    public static double modifyFieldOfView(double currentFov) {
        return active ? currentFov * zoomFactor : currentFov;
    }

    public static Vec3d getCameraPositionOverride(Vec3d originalPos) {
        if (!active || targetBlockPos == null) return originalPos;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return originalPos;

        double cx = targetBlockPos.getX() + 0.5;
        double cy = targetBlockPos.getY() + 0.5;
        double cz = targetBlockPos.getZ() + 0.5;

        double radYaw = Math.toRadians(client.player.getYaw());
        double radPitch = Math.toRadians(client.player.getPitch());

        double totalForwardOffset = 0.1;

        cx += -Math.sin(radYaw) * Math.cos(radPitch) * totalForwardOffset;
        cy += -Math.sin(radPitch) * totalForwardOffset;
        cz += Math.cos(radYaw) * Math.cos(radPitch) * totalForwardOffset;

        return new Vec3d(cx, cy, cz);
    }
}