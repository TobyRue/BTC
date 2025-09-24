package io.github.tobyrue.btc.client.screen;

import net.minecraft.text.Text;

import java.util.List;

public class HexagonNoHoverValues {
    public record SuffixValueNoHover(Text display, String suffixClick) {}
    public record PrefixValueNoHover(Text display, String commandClick, List<SuffixValueNoHover> suffixValues) {}
    public record ValueNoHover(Text display, String commandClick) {}
}
