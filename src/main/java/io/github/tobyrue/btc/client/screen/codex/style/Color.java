package io.github.tobyrue.btc.client.screen.codex.style;

import io.github.tobyrue.xml.XMLException;
import net.minecraft.text.TextColor;

public record Color(Integer color) {

    public static Color parseRgb(final String text) throws XMLException {
        try {
            String trimmed = text.trim();

            if (trimmed.equalsIgnoreCase("[null]") || trimmed.isEmpty()) {
                // Return null or a default color (e.g., transparent or no color)
                return null; // or return new Color(0) for a default color (like black)
            }

            // Handle hex strings: "#RRGGBB" or "RRGGBB"
            if (trimmed.startsWith("#")) {
                trimmed = trimmed.substring(1);
            }

            if (trimmed.matches("(?i)[0-9a-f]{6}")) {
                return new Color(Integer.parseInt(trimmed, 16));
            }

            // Handle RGB strings: "R,G,B"
            String[] parts = trimmed.split(",");
            if (parts.length == 3) {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());

                if ((r | g | b) < 0 || r > 255 || g > 255 || b > 255) {
                    throw new IllegalArgumentException("RGB values must be between 0 and 255");
                }

                return new Color((r << 16) | (g << 8) | b);
            }

            throw new IllegalArgumentException("Invalid color format: " + text);
        } catch (Exception e) {
            throw new XMLException("Invalid RGB/hex color: " + text + " - " + e.getMessage(), e);
        }
    }

    public TextColor asTextColor() {
        return TextColor.fromRgb(this.color);
    }

    @Override
    public String toString() {
        return String.format("#%06X", this.color);
    }
}
