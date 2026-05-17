package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class ClientLensHandler {

    private static Frustum activeFrustum = null;
    private static final double MAX_XRAY_DISTANCE_SQUARED = 32.0 * 32.0;

    public static void setActiveFrustum(Frustum frustum) {
        activeFrustum = frustum;
    }

    public static boolean shouldMobGlow(Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || activeFrustum == null) return false;

        boolean holdingLens = client.player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.AMETHYST_LENS)
                || client.player.getStackInHand(Hand.OFF_HAND).isOf(ModItems.AMETHYST_LENS);

        if (!holdingLens) {
            activeFrustum = null;
            return false;
        }

        if (entity == client.player) return false;

        if (client.player.getPos().squaredDistanceTo(entity.getPos()) > MAX_XRAY_DISTANCE_SQUARED) {
            return false;
        }

        return activeFrustum.isVisible(entity.getBoundingBox());
    }
}