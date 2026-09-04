package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.block.entities.StructureCoreBlockEntity;
import io.github.tobyrue.btc.packets.StructureCoreUpdatePayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class StructureCoreScreen extends Screen {
    private static final Text TITLE_TEXT = Text.literal("Edit Structure Core Data");
    private final StructureCoreBlockEntity blockEntity;

    private TextFieldWidget inputDataField;
    private TextFieldWidget inputNoteField;

    public StructureCoreScreen(StructureCoreBlockEntity blockEntity) {
        super(TITLE_TEXT);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.inputDataField = new TextFieldWidget(this.textRenderer, centerX - 150, 75, 300, 20, Text.literal("Data Input"));
        this.inputDataField.setMaxLength(512);

        if (!this.blockEntity.getFunctions().isEmpty()) {
            String initialData = "[" + this.blockEntity.getFunctions().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ")) + "]";
            this.inputDataField.setText(initialData);
        }

        this.addSelectableChild(this.inputDataField);

        this.inputNoteField = new TextFieldWidget(this.textRenderer, centerX - 150, 125, 300, 20, Text.literal("Note Input"));
        this.inputNoteField.setMaxLength(256);
        this.inputNoteField.setText(this.blockEntity.getNote() != null ? this.blockEntity.getNote() : "");

        this.addSelectableChild(this.inputNoteField);
        this.setInitialFocus(this.inputDataField);

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.saveAndClose())
                .dimensions(centerX - 154, 180, 150, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> this.close())
                .dimensions(centerX + 4, 180, 150, 20)
                .build());
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String currentData = this.inputDataField != null ? this.inputDataField.getText() : null;
        String currentNote = this.inputNoteField != null ? this.inputNoteField.getText() : null;

        this.init(client, width, height);

        if (currentData != null) {
            this.inputDataField.setText(currentData);
        }
        if (currentNote != null) {
            this.inputNoteField.setText(currentNote);
        }
    }

    private void saveAndClose() {
        if (this.client != null) {
            String text = this.inputDataField.getText();
            String note = this.inputNoteField.getText();

            String payloadText = text + (note.isEmpty() ? "" : " #" + note);
            ClientPlayNetworking.send(new StructureCoreUpdatePayload(this.blockEntity.getPos(), payloadText));
        }
        this.close();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 257 || keyCode == 335) {
            this.saveAndClose();
            return true;
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        context.drawTextWithShadow(this.textRenderer, Text.literal("Functions [namespace:path, namespace:path]"), this.width / 2 - 150, 60, 0xA0A0A0);
        this.inputDataField.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer, Text.literal("Notes (Ignored during execution)"), this.width / 2 - 150, 110, 0xA0A0A0);
        this.inputNoteField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}