package io.github.tobyrue.btc.client.screen;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class HexagonValues {
    public record SuffixValue(Text display, String suffixHover, String suffixClick) {}
    public record PrefixValue(Text display, String commandHover, String commandClick, List<SuffixValue> suffixValues) {}
    public record Value(Text display, String commandHover, String commandClick) {}
    public record DoubleInt(int mouseX, int mouseY) {}
    public record RadialIdentifiers(Identifier backgroundOutlineTexture, float backgroundOutlineTransparency, Identifier backgroundTexture, float backgroundTransparency, Identifier highlightedShapeTexture, float highlightedShapeTransparency, int textRadius, int centerRadius, int maxTextWidth, int sectors, boolean textShadow, boolean titleShadow, int textureHeight, int textureWidth, float imageScale) {}
}
