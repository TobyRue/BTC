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
    private static float originalPlayerYaw = 0.0F;
    private static float originalPlayerPitch = 0.0F;

    private static class StateConfig {
        final Direction lookDirection;
        final Direction offsetDirection;

        StateConfig(Direction lookDirection, Direction offsetDirection) {
            this.lookDirection = lookDirection;
            this.offsetDirection = offsetDirection;
        }
    }

    private static StateConfig getConfigForState(BlockFace face, Direction facing) {
        switch (face) {
            case FLOOR:
                return new StateConfig(facing, facing);
            case CEILING:
                return new StateConfig(facing, facing);
            case WALL:
                switch (facing) {
                    case SOUTH:
                        return new StateConfig(Direction.EAST, Direction.EAST);
                    case NORTH:
                        return new StateConfig(Direction.WEST, Direction.WEST);
                    case EAST:
                        return new StateConfig(Direction.NORTH, Direction.NORTH);
                    case WEST:
                        return new StateConfig(Direction.SOUTH, Direction.SOUTH);
                    default:
                        return new StateConfig(facing, facing);
                }
            default:
                return new StateConfig(facing, facing);
        }
    }

    public static void startZooming(BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        active = true;
        targetBlockPos = pos;

        originalPlayerYaw = client.player.getYaw();
        originalPlayerPitch = client.player.getPitch();

        BlockFace face = client.world.getBlockState(pos).get(SpyGlassBlock.FACE);
        Direction facing = client.world.getBlockState(pos).get(SpyGlassBlock.FACING);

        StateConfig config = getConfigForState(face, facing);

        baseYaw = config.lookDirection.asRotation();

        client.player.setYaw(baseYaw);
        client.player.setPitch(0.0F);

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
        return active ? currentFov * 0.1D : currentFov;
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

        double totalForwardOffset = 1.25;

        // FIXED MATH: Sign inversion pulls the position forward out of the lens barrel
        cx += -Math.sin(radYaw) * Math.cos(radPitch) * totalForwardOffset;
        cy += -Math.sin(radPitch) * totalForwardOffset;
        cz += Math.cos(radYaw) * Math.cos(radPitch) * totalForwardOffset;

        return new Vec3d(cx, cy, cz);
    }
}