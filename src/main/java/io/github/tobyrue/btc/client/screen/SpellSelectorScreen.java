package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.SpellBookXMLParser;
import io.github.tobyrue.btc.client.screen.widget.ClickableTextWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class SpellSelectorScreen extends Screen {
    public static final Identifier BOOK_TEXTURE = BTC.identifierOf("textures/gui/book.png");
    private final List<TextClickEntry> clickableTextWidgets = new ArrayList<>();

    private record TextClickEntry(TextWidget widget, Consumer<Page> action, Page page) {}
    private final List<Page> pages = new ArrayList<>();
    private int currentPageIndex = 0;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private CrossButton dismissButton;
    private HomeButton homeButton;
    public static int t_x = 0, t_y = 0;
    @Environment(EnvType.CLIENT)
    private static class HomeButton extends TexturedButtonWidget {
        private static final ButtonTextures TEXTURES = new ButtonTextures(BTC.identifierOf("widget/home_menu_button"), BTC.identifierOf("widget/home_menu_button_highlighted"));


        protected HomeButton(int x, int y, ButtonWidget.PressAction onPress) {
            super(x, y, 14, 14, TEXTURES, onPress);
        }

        public void playDownSound(SoundManager soundManager) {
            soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
    @Environment(EnvType.CLIENT)
    private static class CrossButton extends TexturedButtonWidget {
        private static final ButtonTextures TEXTURES = new ButtonTextures(BTC.identifierOf("widget/exit_button_book"), BTC.identifierOf("widget/exit_button_book_highlighted"));


        protected CrossButton(int x, int y, ButtonWidget.PressAction onPress) {
            super(x, y, 14, 14, TEXTURES, onPress);
        }

        public void playDownSound(SoundManager soundManager) {
            soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    public SpellSelectorScreen(Text title) {
        super(title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (TextClickEntry entry : clickableTextWidgets) {
            if (entry.page() == getCurrentPage() && entry.widget().isMouseOver(mouseX, mouseY)) {
                entry.action().accept(getCurrentPage());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    protected void init() {
        pages.clear();

        addButtonsOnPage();
        addCurrentPageWidgets();
        refreshWidgets();
    }
    private void addAllPageWidgets() {
        int i = (this.width) / 2;
        int k = (this.height) / 2;
        this.dismissButton = this.addDrawableChild(new CrossButton(i + 150, k - 100, (button) -> {
            this.client.setScreen(null);
        }));
        this.homeButton = this.addDrawableChild(new HomeButton(i + 135, k - 100, (button) -> {
            this.setPage(0);
        }));
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.currentPageIndex < this.pages.size() - 1;
        this.previousPageButton.visible = this.currentPageIndex > 0;
    }

    private void refreshWidgets() {
        this.clearChildren();

        // Add dismiss and home buttons
        this.addAllPageWidgets();

        // Add current page buttons
        for (ButtonWidget button : getCurrentPage().buttons) {
            this.addDrawableChild(button);
        }

        // Add current page text widgets
        for (ClickableTextWidget textWidget : getCurrentPage().texts) {
            this.addDrawableChild(textWidget);
        }

        // Add page turn buttons
        int i = (this.width) / 2;
        int k = (this.height) / 2;
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 128, k + 85, true, (button) -> {
            this.goToNextPage();
        }, true));

        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i - 152, k + 85, false, (button) -> {
            this.goToPreviousPage();
        }, true));

        this.updatePageButtons();
    }


    @Override
    public boolean shouldPause() {
        return false;
    }

    protected void goToNextPage() {
        if (this.currentPageIndex < pages.size() - 1) {
            ++this.currentPageIndex;
            this.refreshWidgets();
        }
    }

    protected void goToPreviousPage() {
        if (this.currentPageIndex > 0) {
            --this.currentPageIndex;
            this.refreshWidgets();
        }
    }


    private void addCurrentPageWidgets() {
        this.clearChildren();
        for (ButtonWidget button : getCurrentPage().buttons) {
            this.addDrawableChild(button);
        }
        for (ClickableTextWidget textWidget : getCurrentPage().texts) {
            this.addDrawableChild(textWidget);
        }
    }

    private void addButtonsOnPage() {


        Page mainPage = new Page("codex");

        mainPage.addText(new ClickableTextWidget(this.width / 2 - 150, this.height / 2 - 70, Text.translatable("item.btc.spell.codex.title.codex"), this.textRenderer,
                (widget) -> {
                    setPage(1);
                }
        ));

        Page firePage = new Page("fire_spells");

        firePage.addText(new ClickableTextWidget(this.width / 2 - 150, this.height / 2 - 70, Text.translatable("item.btc.spell.codex.fire.fireball"), this.textRenderer,
                (widget) -> {
                    this.client.getToastManager().add(
                            SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Fireball!"), Text.of("You cast Fireball."))
                    );
                }
        ));


        pages.add(mainPage);

        pages.add(firePage);
    }

    private Page getCurrentPage() {
        return pages.get(currentPageIndex);
    }

    private void setPage(int index) {
        currentPageIndex = index;
        this.refreshWidgets();
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        var xmlString = SpellBookXMLParser.loadResource();
        var reader = new StringReader(xmlString);
        var inputSource = new InputSource(reader);
        Document document;
        try {
            document = builder.parse(inputSource);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        var root = document.getDocumentElement();
        var pages = root.getElementsByTagName("page");

        for (int i = 0; i < pages.getLength(); i++) {
            var page = (Element) pages.item(i);

            // Get attribute id

            var id = page.getAttribute("id");
            System.out.println("Page ID: " + id);

            // Get all <line> elements inside this page
            var lines = page.getElementsByTagName("line");
            if (t_x != 0 && width != 0) {
                context.drawText(this.textRenderer, Text.literal(page.getElementsByTagName("line").toString()), this.width / t_x, this.height / 2, 0x000000, false);
            }
            for (int j = 0; j < lines.getLength(); j++) {
                var line = (Element) lines.item(j);
                String align = line.hasAttribute("align") ? line.getAttribute("align") : "left";
                String textContent = line.getTextContent().trim();
                System.out.println("  Line (align=" + align + "): " + textContent);
            }
        }
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Draw page title
        String title = getCurrentPage().title;
        int titleWidth = this.textRenderer.getWidth(title);

        var i = (this.width - titleWidth) / 2;
        context.drawText(this.textRenderer, Text.translatable("item.btc.spell.codex.title." + title), this.width / 2 - 117, this.height / 2 - 95, 0x000000, true);
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
