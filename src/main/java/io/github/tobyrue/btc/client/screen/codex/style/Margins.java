package io.github.tobyrue.btc.client.screen.codex.style;

import io.github.tobyrue.xml.XMLException;

import static io.github.tobyrue.btc.client.screen.codex.style.UnitValue.DistanceValue.parse;

public record Margins(UnitValue.DistanceValue top, UnitValue.DistanceValue right, UnitValue.DistanceValue bottom, UnitValue.DistanceValue left) {
    public static Margins parseMargins(final String text) throws XMLException {
        try {
            var margins = text.split("(,|\\s)+");
            return switch (margins.length) {
                case 1 -> new Margins(parse(margins[0]), parse(margins[0]), parse(margins[0]), parse(margins[0]));
                case 2 -> new Margins(parse(margins[0]), parse(margins[1]), parse(margins[0]), parse(margins[1]));
                case 3 -> new Margins(parse(margins[0]), parse(margins[1]), parse(margins[2]), parse(margins[1]));
                case 4 -> new Margins(parse(margins[0]), parse(margins[1]), parse(margins[2]), parse(margins[3]));
                default -> throw new XMLException(String.format("Expected 1 to 4 margin values but got %d", margins.length));
            };
        } catch (NumberFormatException e) {
            throw new XMLException(String.format("Unable to parse margin values '%s'", text), e);
        }
    }
}
