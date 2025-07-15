package io.github.tobyrue.btc.client.screen.codex.style;

import io.github.tobyrue.xml.XMLException;
import net.minecraft.text.Text;

import java.util.Arrays;

public record Margins(int top, int right, int bottom, int left) {
    public static Margins parseMargins(final String text) throws XMLException {
        try {
            var margins = Arrays.stream(text.split("(,|\\s)+")).map(t -> Integer.parseInt(t.trim())).toArray(Integer[]::new);
            return switch (margins.length) {
                case 1 -> new Margins(margins[0], margins[0], margins[0], margins[0]);
                case 2 -> new Margins(margins[0], margins[1], margins[0], margins[1]);
                case 3 -> new Margins(margins[0], margins[1], margins[2], margins[1]);
                case 4 -> new Margins(margins[0], margins[1], margins[2], margins[3]);
                default -> throw new XMLException(String.format("Expected 1 to 4 margin values but got %d", margins.length));
            };
        } catch (NumberFormatException e) {
            throw new XMLException(String.format("Unable to parse margin values '%s'", text), e);
        }
    }
}
