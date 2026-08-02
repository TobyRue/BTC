package io.github.tobyrue.btc.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AnimatedTextureHelper {

    /**
     * Creates a SpriteIdentifier that hooks your texture into the main Block/Item Atlas.
     * Note: In the atlas path, omit "textures/" and ".png".
     * e.g., "item/wind_staff" points to "assets/btc/textures/item/wind_staff.png"
     */
    public static SpriteIdentifier createItemSpriteId(Identifier textureIdentifier) {
        String path = textureIdentifier.getPath();
        if (path.startsWith("textures/")) {
            path = path.substring("textures/".length());
        }
        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
        }

        Identifier atlasTextureId = Identifier.of(textureIdentifier.getNamespace(), path);
        return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, atlasTextureId);
    }

    /**
     * Gets the animated Sprite for the identifier.
     */
    public static Sprite getSprite(SpriteIdentifier spriteId) {
        return spriteId.getSprite();
    }

    /**
     * Gets a VertexConsumer for rendering the atlas texture with translucency support.
     */
    public static VertexConsumer getBuffer(VertexConsumerProvider vertexConsumers, SpriteIdentifier spriteId) {
        return spriteId.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucent);
    }
}