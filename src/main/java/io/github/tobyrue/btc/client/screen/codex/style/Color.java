package io.github.tobyrue.btc.client.screen.codex.style;

import io.github.tobyrue.xml.XMLException;
import net.minecraft.text.TextColor;

import java.util.Locale;

public record Color(int rgb) {

    public static Color parseRgb(final String text) throws XMLException {
        try {
            String trimmed = text.trim().toLowerCase(Locale.ROOT).replaceAll("[\\s_]+", "").replaceFirst("^#", "0x");

            if (trimmed.isEmpty()) {
                return new Color(-1);
            }

            // Handle hex strings: "#RRGGBB" or "0xRRGGBB"
            if (trimmed.startsWith("0x")) {
                return new Color(switch (trimmed.length() - 2) {
                    case 3 -> {
                        var t = Integer.parseInt(trimmed.substring(2), 16);
                        yield ((t & 0xf00) << 12) | ((t & 0x0f0) << 8) | ((t & 0x00f) << 4);
                    }
                    case 6 -> Integer.parseInt(trimmed.substring(2), 16);
                    case 4, 8 -> throw new XMLException(String.format("Cannot parse color '%s' with alpha channel", trimmed));
                    default -> throw new XMLException(String.format("Expected 3 or 6 digit hex color, got %d", trimmed.length() - 2));
                });
            } else if (trimmed.startsWith("rgb(") && trimmed.endsWith(")")) {

                // Handle RGB strings: "R,G,B"
                String[] parts = trimmed.substring(4, trimmed.length() - 1).split(",");
                if (parts.length == 3) {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());

                    if ((r | g | b) < 0 || r > 255 || g > 255 || b > 255) {
                        throw new XMLException("RGB values must be between 0 and 255");
                    }

                    return new Color((r << 16) | (g << 8) | b);
                }
            }

            throw new XMLException(String.format("Invalid color format '%s'", text));
        } catch (NumberFormatException e) {
            throw new XMLException(String.format("Invalid number in color '%s'", text), e);
        }
    }

    public boolean isValid() {
        return this.rgb != -1;
    }

    public TextColor asTextColor() {
        return this.isValid() ? TextColor.fromRgb(this.rgb) : null;
    }

    @Override
    public String toString() {
        return String.format("#%06X", this.rgb & 0xFFFFFF);
    }
}
