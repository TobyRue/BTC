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
                    case NORTH:
                        return new StateConfig(Direction.EAST, Direction.EAST);
                    case SOUTH:
                        return new StateConfig(Direction.WEST, Direction.WEST);
                    case EAST:
                        return new StateConfig(Direction.NORTH, Direction.NORTH);
                    case WEST:
                        return new StateConfig(Direction.SOUTH, Direction.SOUTH);
                    default:
                        return new StateConfig(facing, facing);
                }
            default:
                return new StateConfig(facing, facing.getOpposite());
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
            spyglass.setAngles(0.0F, 0.0F);
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

            deltaYaw = MathHelper.clamp(deltaYaw, -SpyGlassBlockEntity.MAX_YAW_LIMIT, SpyGlassBlockEntity.MAX_YAW_LIMIT);
            playerPitch = MathHelper.clamp(playerPitch, -SpyGlassBlockEntity.MAX_PITCH_LIMIT, SpyGlassBlockEntity.MAX_PITCH_LIMIT);

            client.player.setYaw(MathHelper.wrapDegrees(baseYaw + deltaYaw));
            client.player.setPitch(playerPitch);

            float targetBlockPitch = playerPitch;
            float targetBlockYaw = deltaYaw;

            if (location == BlockFace.CEILING) {
                targetBlockYaw = -deltaYaw;
            } else if (location == BlockFace.WALL) {
                targetBlockYaw = playerPitch;
                targetBlockPitch = deltaYaw;
            }

            spyglass.setAngles(targetBlockPitch, targetBlockYaw);
        }
    }

    public static double modifyFieldOfView(double currentFov) {
        return active ? currentFov * 0.1D : currentFov;
    }

    public static Vec3d getCameraPositionOverride(Vec3d originalPos) {
        if (!active || targetBlockPos == null) return originalPos;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return originalPos;

        BlockFace face = client.world.getBlockState(targetBlockPos).get(SpyGlassBlock.FACE);
        Direction facing = client.world.getBlockState(targetBlockPos).get(SpyGlassBlock.FACING);

        StateConfig config = getConfigForState(face, facing);

        double cx = targetBlockPos.getX() + 0.5;
        double cy = targetBlockPos.getY() + 0.5;
        double cz = targetBlockPos.getZ() + 0.5;

        cx += config.offsetDirection.getOffsetX() * 1.42;
        cy += config.offsetDirection.getOffsetY() * 1.42;
        cz += config.offsetDirection.getOffsetZ() * 1.42;

        double radYaw = Math.toRadians(client.player.getYaw());
        double radPitch = Math.toRadians(client.player.getPitch());

        cx += Math.sin(radYaw) * Math.cos(radPitch) * 0.45;
        cy += Math.sin(radPitch) * 0.45;
        cz += -Math.cos(radYaw) * Math.cos(radPitch) * 0.45;

        return new Vec3d(cx, cy, cz);
    }
}