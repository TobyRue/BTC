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
                case "px", "" -> px((int) f);
                case "%" -> percent(f);
                case "bgp" -> bgp(f);
                case "em", "u" -> em(f);
                default -> throw new XMLException(String.format("Unknown unit '%s'", matcher.group(2)));
            };
        }

        static DistanceValue px(int px) {
            return (dimensionMax, scale, baseValue) -> px;
        }

        static DistanceValue percent(float percent) {
            return (dimensionMax, scale, baseValue) -> (int) (percent * 100 * dimensionMax);
        }

        static DistanceValue bgp(float bgp) {
            return (dimensionMax, scale, baseValue) -> (int) (bgp * scale);
        }

        static DistanceValue em(float em) {
            return (dimensionMax, scale, baseValue) -> (int) (em * baseValue);
        }

        int getPxDistance(int dimensionMax, float scale, int baseValue);
    }
}
