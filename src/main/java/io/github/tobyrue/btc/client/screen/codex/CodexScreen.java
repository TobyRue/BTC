package io.github.tobyrue.btc.client.screen.codex;

import io.github.tobyrue.btc.BTC;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CodexScreen extends Screen {
    public static final Identifier BOOK_TEXTURE = BTC.identifierOf("textures/gui/book_text_area.png");
    public static Codex codex;
    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 160;

    public CodexScreen() {
        super(Text.empty());
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

//        int x = 0;
//        int y = 0;
//        int width = 0;
//        int height = 0;

        super.render(context, mouseX, mouseY, delta);
    }

    public RenderHelper getRenderHelper(DrawContext context) {
        return new RenderHelper(context, textRenderer, width, height, IMAGE_WIDTH, IMAGE_HEIGHT, 1.5f);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
        var h = getRenderHelper(context);
        h.renderDebugBackground();

        Vec2i b = h.getBackgroundOrigin();

//        context.getMatrices().push();
//        context.getMatrices().translate(b.x, b.y, 0);
//        context.getMatrices().scale(h.scale(), h.scale(), 1.0f);

        context.drawTexture(
                BOOK_TEXTURE,
                b.x, b.y,
                0, 0,
                h.scaled(h.imageWidth()), h.scaled(h.imageHeight()),
                h.scaled(h.imageWidth()), h.scaled(h.imageHeight())
        );

//        context.getMatrices().pop();
    }
}
