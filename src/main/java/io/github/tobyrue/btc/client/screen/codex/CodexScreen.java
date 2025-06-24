package io.github.tobyrue.btc.client.screen.codex;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Codex;
import io.github.tobyrue.xml.XMLException;
import io.github.tobyrue.xml.XMLParser;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.Objects;

public class CodexScreen extends Screen {
    public static final Identifier BOOK_TEXTURE = BTC.identifierOf("textures/gui/book.png");


    public CodexScreen() {
        super(Text.empty());
    }


    protected static final Codex.Text CODEX;

    static {
        try {
            CODEX = XMLParser.parse(new InputStreamReader(Objects.requireNonNull(CodexScreen.class.getResourceAsStream("/text.xml"))), Codex.Text.class);
        } catch (XMLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, CODEX.toText(), this.width / 2, this.height / 2, 0xFFFFFF, false);
    }


    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);

        int imageWidth = 256;
        int imageHeight = 160;
        float scale = 1.5f;

        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);

        int x = (this.width - scaledWidth) / 2;
        int y = (this.height - scaledHeight) / 2;

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);

        context.drawTexture(
                BOOK_TEXTURE,
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );

        context.getMatrices().pop();
    }
}
