package io.github.tobyrue.btc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tobyrue.btc.BTC;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HexagonRadialMenu extends Screen {
    private static final Identifier BACKGROUND = BTC.identifierOf("textures/gui/honeycomb.png");
    private static final Identifier BACKGROUND_STONE = BTC.identifierOf("textures/gui/honeycomb_stone.png");

    private static final int TEX_WIDTH = 603;
    private static final int TEX_HEIGHT = 582;

    private int centerX;
    private int centerY;

    public HexagonRadialMenu(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

//        int hovered = getHoveredHex(mouseX, mouseY);
//
//        if (hovered >= 0) {
//            System.out.println("Hovered: " + hovered);
//        }

        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * @return -1 if center, 0-5 for surrounding hexagons clockwise starting at top
     */
    private int getHoveredHex(int mouseX, int mouseY) {
        if ((Math.pow((mouseX-centerX), 2) + Math.pow((mouseY-centerY), 2)) > 900) {
            int angle = (int) Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX));
            angle += 90;
            if (angle < 0) angle += 360;
            int sector = (int) Math.floor(angle / 60);
            System.out.println("Sector: " + sector);
            return sector;
        }
        return -1;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int sector = getHoveredHex((int) mouseX, (int) mouseY);
        if (sector != -1) {
            System.out.println("Clicked on hexagon: " + sector );
            this.close();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
        int sector = getHoveredHex(mouseX, mouseY);

        int imageWidth = TEX_WIDTH;
        int imageHeight = TEX_HEIGHT;
        float scale = 0.3f;

        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);

        int x = (this.width - scaledWidth) / 2;
        int y = (this.height - scaledHeight) / 2;

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);

        // Draw base background
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        context.drawTexture(
                BACKGROUND,
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );

        // Draw overlay with transparency
        RenderSystem.setShaderColor(1f, 1f, 1f, 200f / 255f); // ~70% opacity
        context.drawTexture(
                BACKGROUND_STONE,
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );
        RenderSystem.setShaderColor(1f, 1f, 1f, 150f / 255f); // ~70% opacity
        context.drawTexture(
                BTC.identifierOf("textures/gui/honeycomb_sector_" + (sector + 1) + ".png"),
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );
        // Reset state
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        context.getMatrices().pop();
    }
}



