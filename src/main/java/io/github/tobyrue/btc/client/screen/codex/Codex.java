package io.github.tobyrue.btc.client.screen.codex;

import io.github.tobyrue.btc.AdvancementParser;
import io.github.tobyrue.btc.client.screen.codex.style.Color;
import io.github.tobyrue.btc.client.screen.codex.style.EnumHelper;
import io.github.tobyrue.btc.client.screen.codex.style.Margins;
import io.github.tobyrue.btc.client.screen.codex.style.UnitValue;
import io.github.tobyrue.xml.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.io.Reader;
import java.util.*;

@XML.Root
public record Codex(@XML.Children(allow = {Page.class}) XMLNodeCollection<Page> children) implements XMLNode {
    public static final XMLParser.AttributeParser ATTRIBUTE_PARSER;

    public static final XMLParser<Codex> XML_PARSER;
    static {
        try {
            XML_PARSER = new XMLParser<>(Codex.class, ATTRIBUTE_PARSER = new XMLParser.AttributeParser(
                    XMLParser.AttributeParser::parseString,
                    XMLParser.AttributeParser::parseByte,
                    XMLParser.AttributeParser::parseShort,
                    XMLParser.AttributeParser::parseInteger,
                    XMLParser.AttributeParser::parseLong,
                    XMLParser.AttributeParser::parseFloat,
                    XMLParser.AttributeParser::parseDouble,
                    XMLParser.AttributeParser::parseBoolean,
                    Codex::parseExpression,
                    Codex::parseFormatting,
                    Codex::parseTextAlignment,
                    Codex::parsePosition,
                    Margins::parseMargins,
                    UnitValue.DistanceValue::parse,
                    Color::parseRgb
            ));
        } catch (final XMLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static final class Pages {
        private final TreeMap<String, Page> pages;

        Pages(TreeMap<String, Page> pages) {
            this.pages = pages;
        }

        Pages(XMLNodeCollection<Page> pages) {
            this.pages = new TreeMap<>();
            pages.forEach(page -> this.pages.put(page.id, page));
        }

        public Page getPage(String id) {
            return this.pages.get(id);
        }
        public Page getPage(int index) {
            return this.pages.values().stream().skip(index - 1).findFirst().orElse(null);
        }
        public int size() {
            return this.pages.size();
        }
    }

    private enum Alignment {
        LEFT,
        RIGHT,
        CENTER,
        TEXT_LOCALE
    }

    private static AdvancementParser.Expression parseExpression(final String text) throws XMLException {
        try {
            return AdvancementParser.parse(text);
        } catch (Exception e) {
            throw new XMLException(e.getMessage());
        }
    }
    private static Formatting[] parseFormatting(final String text) throws XMLException {
        try {
            return Arrays.stream(text.split(",")).map(t -> Objects.requireNonNull(Formatting.byName(t))).toArray(Formatting[]::new);
        } catch (Exception e) {
            throw new XMLException(e.getMessage());
        }
    }

    private static Position parsePosition(final String text) throws XMLException {
        try {
            return Objects.requireNonNull(EnumHelper.byName(Position.class, text));
        } catch (Exception e) {
            throw new XMLException(e.getMessage());
        }
    }

    private static Alignment parseTextAlignment(final String text) throws XMLException {
        try {
            return Objects.requireNonNull(EnumHelper.byName(Alignment.class, text));
        } catch (Exception e) {
            throw new XMLException(e.getMessage());
        }
    }

    public static Codex parse(Reader reader) throws XMLException {
        return XML_PARSER.parse(reader);
    }

    public interface TextContent extends XMLNode {
        net.minecraft.text.Text toText();
    }

    public enum Position {
        ABSOLUTE,
        STATIC
    }

    public interface BlockContent extends XMLNode, RenderContent {
        Margins getMargins();
        Position getPosition();
        int getHeight();
        int getWidth();
    }

    public interface RenderContent {
        void render(final ServerPlayerEntity player, final int x, final int y, final int width, final int height);
    }

    public interface ConditionalContent extends XMLNode {
        boolean isRequirementMet(ServerPlayerEntity player);
    }

    public Pages getPages() {
        return new Pages(this.children);
    }

    public record Page(
            @XML.Children(allow = {BlockContent.class}) XMLNodeCollection<BlockContent> children,
            @XML.Attribute(fallBack = "true") AdvancementParser.Expression requires,
            String id
    ) implements XMLNode, ConditionalContent, RenderContent {
        @Override
        public boolean isRequirementMet(ServerPlayerEntity player) {
            return requires.evaluate(player);
        }

        @Override
        public void render(final ServerPlayerEntity player, final int x, final int y, final int width, final int height) {
            int dy = 0;
            for (var c : this.children) {
                if (c instanceof ConditionalContent maybe && !maybe.isRequirementMet(player)) continue;
                switch (c.getPosition()) {
                    case STATIC -> {
//                        var m = c.getMargins();
//                        var w = c.getWidth();
//                        var h = c.getHeight();
//                        var left = m.left().getPxDistance();
//                        var right = m.right().getPxDistance();
//                        var top = m.top().getPxDistance();
//                        var bottom = m.bottom().getPxDistance();
//                        c.render(player, x + left, y + dy + top, width, height);
//                        dy += top + height + bottom;
                    }
                    case ABSOLUTE -> {

                    }
                }
            }
        }

        @XML.Name("hr")
        public record HorizontalLine() implements BlockContent {

            @Override
            public Margins getMargins() {
                return null;
            }

            @Override
            public Position getPosition() {
                return null;
            }

            @Override
            public int getHeight() {
                return 0;
            }

            @Override
            public int getWidth() {
                return 0;
            }

            @Override
            public void render(ServerPlayerEntity player, int x, int y, int width, int height) {

            }
        }
        //TODO ^ v finish
        @XML.Name("br")
        public record BreakLine() implements BlockContent {
            @Override
            public Margins getMargins() {
                return null;
            }

            @Override
            public Position getPosition() {
                return null;
            }

            @Override
            public int getHeight() {
                return 0;
            }

            @Override
            public int getWidth() {
                return 0;
            }

            @Override
            public void render(ServerPlayerEntity player, int x, int y, int width, int height) {

            }
        }

        @XML.Name("img")
        public record TranslatedText(String key) implements BlockContent {

            @Override
            public Margins getMargins() {
                return null;
            }

            @Override
            public Position getPosition() {
                return null;
            }

            @Override
            public int getHeight() {
                return 0;
            }

            @Override
            public int getWidth() {
                return 0;
            }

            @Override
            public void render(ServerPlayerEntity player, int x, int y, int width, int height) {

            }
        }

        @XML.Name("p")
        public record Paragraph(
                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
                @XML.Attribute(fallBack = "true") AdvancementParser.Expression requires,
                @XML.Attribute(fallBack = "text_locale") Alignment align,
                @XML.Attribute(fallBack = "0.5u, 0u, 1u, 0u") Margins margin,
                @XML.Attribute(fallBack = "static") Position position,
                @XML.Attribute(fallBack = "0") Integer top,
                @XML.Attribute(fallBack = "0") Integer bottom,
                @XML.Attribute(fallBack = "0") Integer left,
                @XML.Attribute(fallBack = "0") Integer right,
                @XML.Attribute(fallBack = "-1px") UnitValue.DistanceValue height,
                @XML.Attribute(fallBack = "-1px") UnitValue.DistanceValue width

        ) implements BlockContent, ConditionalContent {

            @Override
            public boolean isRequirementMet(ServerPlayerEntity player) {
                return requires.evaluate(player);
            }

            //TODO
            @Override
            public Margins getMargins() {
                return margin;
            }

            @Override
            public Position getPosition() {
                return position;
            }

            @Override
            public int getWidth() {
                return 0;
            }

            @Override
            public int getHeight() {
                return 0;
            }

            @Override
            public void render(final ServerPlayerEntity player, final int x, final int y, final int width, final int height) {

            }
        }
    }

    @XML.Root
    public record Text(@XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children) implements XMLNode {
        public static final XMLParser<Text> XML_PARSER;

        static {
            try {
                XML_PARSER = new XMLParser<>(Text.class, ATTRIBUTE_PARSER);
            } catch (final XMLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public net.minecraft.text.Text toText() {
            return concat(this.children);
        }

        public static net.minecraft.text.Text parse(Reader reader) throws XMLException {
            return XML_PARSER.parse(reader).toText();
        }

        public static MutableText concat(final XMLNodeCollection<TextContent> nodes) {
            var text = net.minecraft.text.Text.empty();
            for (var node : nodes) {
                text.append(node.toText());
            }
            return text;
        }

        @XML.Name(XML.Name.TEXT)
        public record TextNode(String text) implements TextContent {
            @Override
            public String toString() {
                return this.text;
            }

            @Override
            public net.minecraft.text.Text toText() {
                return net.minecraft.text.Text.literal(this.text);
            }
        }
        @XML.Name("i")
        public record Italic(@XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children, @XML.Attribute(fallBack = "italic") Formatting[] style) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(children).formatted(style);
            }
        }
        @XML.Name("b")
        public record Bold(@XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children, @XML.Attribute(fallBack = "bold") Formatting[] style) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(children).formatted(style);
            }
        }
        @XML.Name("x")
        public record Obfuscated(@XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children, @XML.Attribute(fallBack = "obfuscated") Formatting[] style) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(children).formatted(style);
            }
        }
        @XML.Name("u")
        public record UnderLine(@XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children, @XML.Attribute(fallBack = "underline") Formatting[] style) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(children).formatted(style);
            }
        }
        @XML.Name("s")
        public record StrikeThrough(@XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children, @XML.Attribute(fallBack = "strikethrough") Formatting[] style) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(children).formatted(style);
            }
        }
        @XML.Name("t")
        public record TranslatedText(String key) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return net.minecraft.text.Text.translatable("item.btc.spell.codex." + key);
            }
        }
        @XML.Name("fmt")
        public record FormatedText(
                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
                @XML.Attribute(fallBack = "reset") Formatting[] style,
                @XML.Attribute(fallBack = "[null]") Color color
        ) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                if (color != null) {
                    var base = concat(this.children).formatted(style);
                    return color == null ? base : base.withColor(color.color());
                } else {
                    return concat(this.children).formatted(style);
                }
            }
        }
//        @XML.Name("highlight")
//        public record HighlightText(
//                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
//                @XML.Attribute(fallBack = "yellow") TextColor highlightColor
//        ) implements TextContent {
//            @Override
//            public net.minecraft.text.Text toText() {
//                return concat(this.children).styled(s -> s.withColor(highlightColor));
//            }
//        }
        @XML.Name("a")
        public record Anchor(
                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
                @XML.Attribute(fallBack = "") String title,
                @XML.Attribute(fallBack = "") String href,
                @XML.Attribute(fallBack = "") String onclick,
                @XML.Attribute(fallBack = "blue, underline") Formatting[] style
        ) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                final ClickEvent e;
                final var href = this.href.strip();
                final var onclick = this.onclick.strip();

                if (href.startsWith("#")) {
                    e = new ClickEvent(ClickEvent.Action.CHANGE_PAGE, href.substring(1).strip());
                } else if (href.toLowerCase(Locale.ROOT).startsWith("file:")) {
                    e = new ClickEvent(ClickEvent.Action.OPEN_FILE, href.substring(5).strip());
                } else if (!href.isEmpty()) {
                    e = new ClickEvent(ClickEvent.Action.OPEN_URL, href);
                } else if (onclick.toLowerCase(Locale.ROOT).startsWith("copy:")) {
                    e = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, onclick.substring(5).strip());
                } else if (onclick.toLowerCase(Locale.ROOT).startsWith("?")) {
                    e = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + onclick.substring(1).strip());
                } else if (onclick.toLowerCase(Locale.ROOT).startsWith("/")) {
                    e = new ClickEvent(ClickEvent.Action.RUN_COMMAND, onclick);
                } else {
                    e = null;
                }

                return concat(this.children).formatted(style).styled(s -> s.withClickEvent(e).withHoverEvent(title.isBlank() ? null : new HoverEvent(HoverEvent.Action.SHOW_TEXT, net.minecraft.text.Text.translatable(title))));
            }
        }
    }
}
