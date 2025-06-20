package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.BTC;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellSelectorScreen extends Screen {
    public static final Identifier BOOK_TEXTURE = BTC.identifierOf("textures/gui/book.png");

    private final List<Page> pages = new ArrayList<>();
    private int currentPageIndex = 0;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private CrossButton dismissButton;
    public static int t_x = 0, t_y = 0;
    //TODO ADD A HOME BUTTON NORMAL TEXTURE DONE HIGHLIGHTED NEEDED DO LIKE CROSS BUTTON
    @Environment(EnvType.CLIENT)
    private static class CrossButton extends TexturedButtonWidget {
        private static final ButtonTextures TEXTURES = new ButtonTextures(BTC.identifierOf("widget/exit_button_book"), BTC.identifierOf("widget/exit_button_book_highlighted"));
//        private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/cross_button"), Identifier.ofVanilla("widget/cross_button_highlighted"));


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
    static {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            var s = message.getContent().getString();
            sender.sendMessage(Text.literal("Git Gud"), true);
            if (s.startsWith("!")) {
                try {
                    var c = s.substring(1).split(" ");

                    if (c.length < 1) {
                        throw new Exception("Missing command after '!'");
                    }

                    var command = c[0].toLowerCase();
                    var args = Arrays.copyOfRange(c, 1, c.length);

                    switch (command) {
                        case "setx":
                            t_x = Integer.parseInt(args[0], 10);
                            sender.sendMessage(Text.literal("t_x = " + t_x));
                            break;
                        case "sety":
                            t_y = Integer.parseInt(args[0], 10);
                            sender.sendMessage(Text.literal("t_y = " + t_y));
                            break;
                        default:
                            throw new Exception("Unknown command '" + command + "'");
                    }
                } catch (Throwable t) {
                    sender.sendMessage(Text.literal("Error: ").setStyle(Style.EMPTY.withColor(0xFF0000)).append(Text.literal(t.toString())));
                }
            }
        });
    }

    @Override
    protected void init() {
        pages.clear();
        addButtonsOnPage();
        addCurrentPageWidgets();
        refreshWidgets();
        addAllPageWidgets();
    }

    private void addAllPageWidgets() {
        int i = (this.width) / 2;
        int k = (this.height) / 2;
        this.dismissButton = this.addDrawableChild(new CrossButton(i + 150, k - 100, (button) -> {
            this.client.setScreen(null);
        }));
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.currentPageIndex < this.pages.size() - 1;
        this.previousPageButton.visible = this.currentPageIndex > 0;
    }

    private void refreshWidgets() {

        this.clearChildren();

        // Add current page buttons
        for (ButtonWidget button : getCurrentPage().buttons) {
            this.addDrawableChild(button);
        }

        // Add page turn widgets
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
    }

    private void addButtonsOnPage() {


        Page mainPage = new Page("Spell Book");

        mainPage.addButton(ButtonWidget.builder(Text.of("Fire Spells"), (btn) -> {
            setPage(1);
        }).dimensions(this.width / 2 - 150, this.height / 2 - 80, 120, 20).build());

        mainPage.addButton(ButtonWidget.builder(Text.of("Close"), (btn) -> {
            this.client.setScreen(null);
        }).dimensions(this.width / 2 + 30, this.height / 2 + 60, 120, 20).build());


        Page firePage = new Page("Fire Spells");

        firePage.addButton(ButtonWidget.builder(Text.of("Fire Ball"), (btn) -> {
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Fireball!"), Text.of("You cast Fireball."))
            );
        }).dimensions(this.width / 2 - 150, this.height / 2 - 80, 120, 20).build());
        firePage.addButton(ButtonWidget.builder(Text.of("Back"), (btn) -> {
            setPage(0);
        }).dimensions(this.width / 2 + 30, this.height / 2 + 35, 120, 20).build());
        firePage.addButton(ButtonWidget.builder(Text.of("Close"), (btn) -> {
            this.client.setScreen(null);
        }).dimensions(this.width / 2 + 30, this.height / 2 + 60, 120, 20).build());

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
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Draw page title
        String title = getCurrentPage().title;
        int titleWidth = this.textRenderer.getWidth(title);
//        context.drawText(this.textRenderer, title, (this.width - titleWidth) / 2, 20, 0xFFFFFF, true);
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
