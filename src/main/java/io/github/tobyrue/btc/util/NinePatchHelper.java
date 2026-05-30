package io.github.tobyrue.btc.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class NinePatchHelper {
    public static void renderNinePatchPlate(DrawContext context, Identifier texture, int x, int y, int width, int height, int cornerSize, int texW, int texH) {

        // Top Left
        context.drawTexture(texture, x, y, 0, 0, cornerSize, cornerSize, texW, texH);
        // Top Right
        context.drawTexture(texture, x + width - cornerSize, y, texW - cornerSize, 0, cornerSize, cornerSize, texW, texH);
        // Bottom Left
        context.drawTexture(texture, x, y + height - cornerSize, 0, texH - cornerSize, cornerSize, cornerSize, texW, texH);
        // Bottom Right
        context.drawTexture(texture, x + width - cornerSize, y + height - cornerSize, texW - cornerSize, texH - cornerSize, cornerSize, cornerSize, texW, texH);

        // Top Edge (stretches from corner to corner)
        context.drawTexture(texture, x + cornerSize, y, width - (cornerSize * 2), cornerSize, cornerSize, 0, texW - (cornerSize * 2), cornerSize, texW, texH);
        // Bottom Edge
        context.drawTexture(texture, x + cornerSize, y + height - cornerSize, width - (cornerSize * 2), cornerSize, cornerSize, texH - cornerSize, texW - (cornerSize * 2), cornerSize, texW, texH);
        // Left Edge
        context.drawTexture(texture, x, y + cornerSize, cornerSize, height - (cornerSize * 2), 0, cornerSize, cornerSize, texH - (cornerSize * 2), texW, texH);
        // Right Edge
        context.drawTexture(texture, x + width - cornerSize, y + cornerSize, cornerSize, height - (cornerSize * 2), texW - cornerSize, cornerSize, cornerSize, texH - (cornerSize * 2), texW, texH);

        // The big middle area that fills everything else
        context.drawTexture(texture, x + cornerSize, y + cornerSize, width - (cornerSize * 2), height - (cornerSize * 2), cornerSize, cornerSize, texW - (cornerSize * 2), texH - (cornerSize * 2), texW, texH);
    }
}
