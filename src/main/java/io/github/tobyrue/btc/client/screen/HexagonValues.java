package io.github.tobyrue.btc.client.screen;

import net.minecraft.text.Text;

import java.util.List;

public class HexagonValues {
    public record SuffixValue(Text display, String suffixHover, String suffixClick) {}
    public record PrefixValue(Text display, String commandHover, String commandClick, List<SuffixValue> suffixValues) {}
    public record Value(Text display, String commandHover, String commandClick) {}
    protected record DoubleInt(int mouseX, int mouseY) {}
}
