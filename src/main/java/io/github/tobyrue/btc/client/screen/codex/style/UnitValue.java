package io.github.tobyrue.btc.client.screen.codex.style;

import io.github.tobyrue.xml.XMLException;

import java.util.Locale;
import java.util.regex.Pattern;

public interface UnitValue {
    @FunctionalInterface
    interface DistanceValue extends UnitValue {
        Pattern PATTERN = Pattern.compile("^(\\d+(?:\\.\\d*)?([a-z_]*))$", Pattern.CASE_INSENSITIVE);
        static DistanceValue parse(final String text) throws XMLException {
            var matcher = PATTERN.matcher(text.strip().toLowerCase(Locale.ROOT));
            var f = Float.parseFloat(matcher.group(1));
            return switch (matcher.group(2)) {
                case "px", "" -> (dimensionMax, scale, baseValue) -> (int) f;
                case "%" -> (dimensionMax, scale, baseValue) -> (int) (f * dimensionMax);
                case "tpx" -> (dimensionMax, scale, baseValue) -> (int) (f * scale);
                case "em", "u" -> (dimensionMax, scale, baseValue) -> (int) (f * baseValue);
                default -> throw new XMLException(String.format("Unknown unit '%s'", matcher.group(2)));
            };
        }
        int getPxDistance(int dimensionMax, float scale, int baseValue);
    }
}
