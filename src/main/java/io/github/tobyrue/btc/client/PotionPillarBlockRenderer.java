package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.PotionPillar;
import io.github.tobyrue.btc.block.entities.PotionPillarBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.item.SpyglassItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public class PotionPillarBlockRenderer implements BlockEntityRenderer<PotionPillarBlockEntity> {
    private final TextRenderer textRenderer;
    private static final int TICKS_PER_GLYPH = 20;
//    private static final String TEXT = "Insert Bee Movie Script Here";

    public PotionPillarBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    public String getHoliday() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("dd MMM")
                .parseDefaulting(ChronoField.YEAR, Year.now().getValue())
                .toFormatter(Locale.US);

        LocalDate today = LocalDate.now();

        if (LocalDate.parse("25 Dec", formatter).equals(today)) {
            return "Merry Christmas!";
        }
        if (LocalDate.parse("31 Oct", formatter).equals(today)) {
            return "Happy Halloween!";
        }
        if (LocalDate.parse("01 Jan", formatter).equals(today)) {
            return "Happy New Year!";
        }
        if (LocalDate.parse("14 Feb", formatter).equals(today)) {
            return "Happy Valentine's Day!";
        }
        if (LocalDate.parse("17 Mar", formatter).equals(today)) {
            return "Happy St. Patrick's Day!";
        }


        return "[Empty]";
    }

    public boolean isHoliday() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("dd MMM")
                .parseDefaulting(ChronoField.YEAR, Year.now().getValue())
                .toFormatter(Locale.US);

        LocalDate today = LocalDate.now();

        return today.equals(LocalDate.parse("25 Dec", formatter))
                || today.equals(LocalDate.parse("31 Oct", formatter))
                || today.equals(LocalDate.parse("01 Jan", formatter))
                || today.equals(LocalDate.parse("14 Feb", formatter))
                || today.equals(LocalDate.parse("17 Mar", formatter));
    }

    @Override
    public void render(PotionPillarBlockEntity blockEntity, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                       int light, int overlay) {

        matrices.push();

        // Move to block center
        matrices.translate(0.5, 0.5, 0.5);

        // Respect AXIS
        Direction.Axis axis = blockEntity.getCachedState().get(PotionPillar.AXIS);
        if (axis == Direction.Axis.X) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
        }
        if (axis == Direction.Axis.Z) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        }
//        Text text = Text.literal("Insert Bee Movie Script Here")
//                .styled(style -> style.withFont(Identifier.of("minecraft", "illageralt")));
        String rune = RuneTextLoader.get(blockEntity.getRuneIndex());
        long time = blockEntity.getWorld().getTime();
        float t = (time + tickDelta) / TICKS_PER_GLYPH;

        int index = Math.floorMod((int)t, rune.length());
        int alpha = getAlpha(t);

        Text glyph = isHoliday() ? Text.literal(String.valueOf(getHoliday().charAt(index))) : Text.literal(String.valueOf(rune.charAt(index)))
                .styled(s -> s.withFont(Identifier.of("minecraft", "illageralt")));

        for (int i = 0; i < 4; i++) {
            matrices.push();

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F * i));

            matrices.translate(0.0, 0.0, -0.251);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

            // Scale
            float scale = 0.03F;
            matrices.scale(scale, -scale, scale);

            Matrix4f matrix = matrices.peek().getPositionMatrix();

            int color = (alpha << 24) | blockEntity.getColor();

            textRenderer.draw(
                    glyph,
                    -textRenderer.getWidth(glyph) / 2.0F,
                    -4.0F,
                    color,
                    false,
                    matrix,
                    vertexConsumers,
                    TextRenderer.TextLayerType.POLYGON_OFFSET, // depth-respecting
                    0,
                    15728864
            );

            matrices.pop();
        }

        matrices.pop();
    }

    private static int getAlpha(float t) {
        float progress = (float) Math.min(0.99, Math.max(0.1, t % 1.0f));
        if (progress < 0) progress += 1.0f;

        float fadeInEnd = 0.25f;
        float fadeOutStart = 0.75f;

        float fade;
        if (progress < fadeInEnd) {
            fade = progress / fadeInEnd;
        } else if (progress < fadeOutStart) {
            fade = 1.0f;
        } else {
            fade = Math.max(0, 1.0f - (progress - fadeOutStart) / (1.0f - fadeOutStart));
        }
        return MathHelper.clamp((int)(fade * 255), 0, 255);
    }
}
