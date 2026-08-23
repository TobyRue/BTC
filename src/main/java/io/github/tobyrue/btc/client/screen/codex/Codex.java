package io.github.tobyrue.btc.client.screen.codex;

import io.github.tobyrue.btc.AdvancementParser;
import io.github.tobyrue.btc.client.screen.codex.style.Color;
import io.github.tobyrue.btc.client.screen.codex.style.Margins;
import io.github.tobyrue.btc.client.screen.codex.style.UnitValue;
import io.github.tobyrue.btc.util.EnumHelper;
import io.github.tobyrue.xml.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.*;

@XML.Root
public record Codex(@XML.Children(allow = {Page.class}) XMLNodeCollection<Page> children) implements XMLNode {
    public static final XMLParser.AttributeParser ATTRIBUTE_PARSER;
    public static final XMLParser<Codex> XML_PARSER;

    static {
        try {
            ATTRIBUTE_PARSER = new XMLParser.AttributeParser(
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
            );
            XML_PARSER = new XMLParser<>(Codex.class, ATTRIBUTE_PARSER);
        } catch (final XMLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void sendPageToChat(Codex.Page page, PlayerEntity player) {
        if (page.isRequirementMet(player)) {
            sendBlocksToChat(page.children(), player);
        }
    }

    private static void sendBlocksToChat(XMLNodeCollection<Codex.BlockContent> blocks, PlayerEntity player) {
        for (Codex.BlockContent block : blocks) {
            if (block instanceof Codex.ConditionalContent conditional && !conditional.isRequirementMet(player)) {
                continue;
            }

            if (block instanceof Codex.Page.Line line) {
                player.sendMessage(line.toText(), false);
            } else if (block instanceof Codex.Page.IfCondition ifCond) {
                sendBlocksToChat(ifCond.children(), player);
            } else if (block instanceof Codex.Page.UnlessCondition unlessCond) {
                sendBlocksToChat(unlessCond.children(), player);
            } else if (block instanceof Codex.Page.HorizontalLine) {
                player.sendMessage(net.minecraft.text.Text.literal("--------------------------------").formatted(Formatting.GRAY), false);
            }
        }
    }

    public static void printContent(Codex codex, @Nullable PlayerEntity player) {
        var pages = codex.getPages();
        System.out.println("CODEX CONTENT (Total Pages: " + pages.size(true) + ") ===");

        for (var page : codex.children()) {
            System.out.println("\n[Page ID: " + page.id() + " | Hidden: " + page.hidden() + "]");

            if (player != null && !page.isRequirementMet(player)) {
                System.out.println("  (Page requirement not met for player)");
                continue;
            }

            printBlockContent(page.children(), player, "  ");
        }
    }

    private static void printBlockContent(XMLNodeCollection<Codex.BlockContent> blocks, @Nullable PlayerEntity player, String indent) {
        for (Codex.BlockContent block : blocks) {
            if (block instanceof Codex.ConditionalContent conditional) {
                if (player != null && !conditional.isRequirementMet(player)) {
                    continue;
                }
            }

            if (block instanceof Codex.Page.Line line) {
                net.minecraft.text.Text text = line.toText();
                System.out.println(indent + "Line [" + line.align() + "]: " + text.getString());
            } else if (block instanceof Codex.Page.IfCondition ifCond) {
                System.out.println(indent + "-> IF (Met: " + (player == null || ifCond.isRequirementMet(player)) + "):");
                printBlockContent(ifCond.children(), player, indent + "    ");
            } else if (block instanceof Codex.Page.UnlessCondition unlessCond) {
                System.out.println(indent + "-> UNLESS (Met: " + (player == null || unlessCond.isRequirementMet(player)) + "):");
                printBlockContent(unlessCond.children(), player, indent + "    ");
            } else if (block instanceof Codex.Page.HorizontalLine) {
                System.out.println(indent + "--- (Horizontal Rule)");
            } else if (block instanceof Codex.Page.BreakLine) {
                System.out.println(indent + "(Break)");
            }
        }
    }

    public static final class Pages {
        private final List<Page> allPages = new ArrayList<>();

        private Pages(XMLNodeCollection<Page> pages) {
            pages.forEach(this.allPages::add);
        }

        /**
         * Gets a page by its String ID, verifying player requirements and hidden state.
         */
        public Page getPage(String id, @Nullable PlayerEntity player) {
            Page page = this.allPages.stream()
                    .filter(p -> p.id().equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (page == null) return null;
            return (page.isRequirementMet(player) && !page.hidden()) ? page : null;
        }

        public Page getPage(String id) {
            return getPage(id, null);
        }

        /**
         * Gets a page by index (1-based), filtering out hidden pages or pages
         * where the player does not meet requirements.
         */
        public Page getPage(int index, @Nullable PlayerEntity player) {
            if (index < 1) return null;
            return this.allPages.stream()
                    .filter(p -> !p.hidden() && p.isRequirementMet(player))
                    .skip(index - 1)
                    .findFirst()
                    .orElse(null);
        }

        public Page getPage(int index) {
            return getPage(index, null);
        }

        /**
         * Calculates total pages visible to the given player.
         */
        public int size(@Nullable PlayerEntity player) {
            return (int) this.allPages.stream()
                    .filter(p -> !p.hidden() && p.isRequirementMet(player))
                    .count();
        }

        public int size() {
            return this.size((PlayerEntity) null);
        }

        public int size(boolean includeHidden) {
            return (int) this.allPages.stream()
                    .filter(p -> includeHidden || !p.hidden())
                    .count();
        }
    }

    public enum Alignment {
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
            return Arrays.stream(text.split("[,;]")).filter(t -> !t.isBlank()).map(t -> Objects.requireNonNull(Formatting.byName(t))).toArray(Formatting[]::new);
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
        void render(final PlayerEntity player, final int x, final int y, final int width, final int height);
    }

    public interface ConditionalContent extends XMLNode {
        boolean isRequirementMet(PlayerEntity player);
    }

    public Pages getPages() {
        return new Pages(this.children);
    }

    @XML.Name("page")
    public record Page(
            @XML.Children(allow = {Line.class, ImageContent.class, VideoContent.class, IfCondition.class, UnlessCondition.class, HorizontalLine.class, BreakLine.class}) XMLNodeCollection<BlockContent> children,
            @XML.Attribute(fallBack = "true") AdvancementParser.Expression requires,
            @XML.Attribute(fallBack = "false") Boolean hidden,
            @XML.Attribute(fallBack = "") String id
    ) implements XMLNode, ConditionalContent, RenderContent {
        @Override
        public boolean isRequirementMet(PlayerEntity player) {
            return requires.evaluate(player);
        }

        @Override
        public void render(final PlayerEntity player, final int x, final int y, final int width, final int height) {
            // Render stuff here later
        }

        @XML.Name("img")
        public record ImageContent(
                @XML.Attribute(fallBack = "") String src,
                @XML.Attribute(fallBack = "-1") Integer width,
                @XML.Attribute(fallBack = "-1") Integer height,
                @XML.Attribute(fallBack = "1.0") Float scale,
                @XML.Attribute(fallBack = "0") Integer offsetX,
                @XML.Attribute(fallBack = "0") Integer offsetY,
                @XML.Attribute(fallBack = "0") Integer layer,
                @XML.Attribute(fallBack = "inline") String display,
                @XML.Attribute(fallBack = "0") Integer u,
                @XML.Attribute(fallBack = "0") Integer v,
                @XML.Attribute(fallBack = "16") Integer textureWidth,
                @XML.Attribute(fallBack = "16") Integer textureHeight,
                @XML.Attribute(fallBack = "center") Alignment align,
                @XML.Attribute(fallBack = "0u, 0u, 0u, 0u") Margins margin,
                @XML.Attribute(fallBack = "static") Position position
        ) implements BlockContent {
            @Override public Margins getMargins() { return margin; }
            @Override public Position getPosition() { return position; }
            @Override public int getHeight() { return Math.round(((height != null && height != -1) ? height : getTextureHeight()) * getScale()); }
            @Override public int getWidth() { return Math.round(((width != null && width != -1) ? width : getTextureWidth()) * getScale()); }

            public float getScale() { return scale != null && scale > 0 ? scale : 1.0f; }
            public int getOffsetX() { return offsetX != null ? offsetX : 0; }
            public int getOffsetY() { return offsetY != null ? offsetY : 0; }
            public int getLayer() { return layer != null ? layer : 0; }
            public boolean isInline() { return "inline".equalsIgnoreCase(display); }

            public Alignment getAlign() { return align; }
            public String getSrc() { return src != null ? src.trim() : ""; }
            public int getU() { return u != null ? u : 0; }
            public int getV() { return v != null ? v : 0; }
            public int getTextureWidth() { return Math.round((textureWidth != null ? textureWidth : 256) * getScale()); }
            public int getTextureHeight() { return Math.round((textureHeight != null ? textureHeight : 256) * getScale()); }

            @Override
            public void render(PlayerEntity player, int x, int y, int width, int height) {}
        }

        @XML.Name("video")
        public record VideoContent(
                @XML.Attribute(fallBack = "") String srcs,
                @XML.Attribute(fallBack = "-1") Integer width,
                @XML.Attribute(fallBack = "-1") Integer height,
                @XML.Attribute(fallBack = "1.0") Float scale,
                @XML.Attribute(fallBack = "0") Integer offsetX,
                @XML.Attribute(fallBack = "0") Integer offsetY,
                @XML.Attribute(fallBack = "0") Integer layer,
                @XML.Attribute(fallBack = "inline") String display, // "inline" or "block"
                @XML.Attribute(fallBack = "0") Integer u,
                @XML.Attribute(fallBack = "0") Integer v,
                @XML.Attribute(fallBack = "16") Integer textureWidth,
                @XML.Attribute(fallBack = "16") Integer textureHeight,
                @XML.Attribute(fallBack = "1") Integer frames,
                @XML.Attribute(fallBack = "0") Integer trimFrames,
                @XML.Attribute(fallBack = "4") Integer frameTicks,
                @XML.Attribute(fallBack = "") String frameOrder,
                @XML.Attribute(fallBack = "true") Boolean interpolate,
                @XML.Attribute(fallBack = "center") Alignment align,
                @XML.Attribute(fallBack = "0u, 0u, 0u, 0u") Margins margin,
                @XML.Attribute(fallBack = "static") Position position
        ) implements BlockContent {
            @Override public Margins getMargins() { return margin; }
            @Override public Position getPosition() { return position; }
            @Override public int getHeight() { return Math.round(((height != null && height != -1) ? height : getTextureHeight()) * getScale()); }
            @Override public int getWidth() { return Math.round(((width != null && width != -1) ? width : getTextureWidth()) * getScale()); }

            public float getScale() { return scale != null && scale > 0 ? scale : 1.0f; }
            public int getOffsetX() { return offsetX != null ? offsetX : 0; }
            public int getOffsetY() { return offsetY != null ? offsetY : 0; }
            public int getLayer() { return layer != null ? layer : 0; }
            public boolean isInline() { return "inline".equalsIgnoreCase(display); }

            public Alignment getAlign() { return align; }
            public String getSrcs() { return srcs; }

            public String[] getParsedSources() {
                if (srcs != null && !srcs.isBlank()) {
                    String[] parts = srcs.split(",");
                    List<String> list = new ArrayList<>();
                    for (String p : parts) {
                        if (!p.isBlank()) list.add(p.trim());
                    }
                    if (!list.isEmpty()) return list.toArray(new String[0]);
                }
                return new String[0];
            }

            public int getU() { return u != null ? u : 0; }
            public int getV() { return v != null ? v : 0; }
            public int getTextureWidth() { return Math.round((textureWidth != null ? textureWidth : 256) * getScale()); }
            public int getTextureHeight() { return Math.round((textureHeight != null ? textureHeight : 256) * getScale()); }
            public int getFrames() { return frames != null && frames > 0 ? frames : 1; }
            public int getTrimFrames() { return trimFrames != null && trimFrames >= 0 ? trimFrames : 0; }
            public int getFrameTicks() { return frameTicks != null && frameTicks > 0 ? frameTicks : 1; }
            public boolean isInterpolated() { return interpolate != null && interpolate; }

            public int[] getParsedFrameOrder() {
                if (frameOrder == null || frameOrder.isBlank()) {
                    return new int[0];
                }
                try {
                    String[] parts = frameOrder.replaceAll("[\\[\\]\\s]", "").split(",");
                    int[] order = new int[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        order[i] = Integer.parseInt(parts[i]);
                    }
                    return order;
                } catch (Exception e) {
                    return new int[0];
                }
            }

            @Override
            public void render(PlayerEntity player, int x, int y, int width, int height) {}
        }

        @XML.Name("if")
        public record IfCondition(
                @XML.Children(allow = {Line.class, ImageContent.class, VideoContent.class, IfCondition.class, UnlessCondition.class, HorizontalLine.class, BreakLine.class}) XMLNodeCollection<BlockContent> children,                @XML.Attribute(fallBack = "true") AdvancementParser.Expression predicate
        ) implements BlockContent, ConditionalContent {
            @Override
            public boolean isRequirementMet(PlayerEntity player) {
                return predicate.evaluate(player);
            }

            @Override public Margins getMargins() { return null; }
            @Override public Position getPosition() { return Position.STATIC; }
            @Override public int getHeight() { return 0; }
            @Override public int getWidth() { return 0; }
            @Override public void render(PlayerEntity player, int x, int y, int width, int height) {}
        }

        @XML.Name("unless")
        public record UnlessCondition(
                @XML.Children(allow = {Line.class, ImageContent.class, VideoContent.class, IfCondition.class, UnlessCondition.class, HorizontalLine.class, BreakLine.class}) XMLNodeCollection<BlockContent> children,                @XML.Attribute(fallBack = "false") AdvancementParser.Expression predicate
        ) implements BlockContent, ConditionalContent {
            @Override
            public boolean isRequirementMet(PlayerEntity player) {
                return !predicate.evaluate(player);
            }

            @Override public Margins getMargins() { return null; }
            @Override public Position getPosition() { return Position.STATIC; }
            @Override public int getHeight() { return 0; }
            @Override public int getWidth() { return 0; }
            @Override public void render(PlayerEntity player, int x, int y, int width, int height) {}
        }

        @XML.Name("hr")
        public record HorizontalLine(
                @XML.Attribute(fallBack = "0") Integer inset,
                @XML.Attribute(fallBack = "0x2D2D2D") Color color
        ) implements BlockContent {
            @Override public Margins getMargins() { return null; }
            @Override public Position getPosition() { return Position.STATIC; }
            @Override public int getHeight() { return 0; }
            @Override public int getWidth() { return 0; }
            @Override public void render(PlayerEntity player, int x, int y, int width, int height) {}
        }

        @XML.Name("br")
        public record BreakLine() implements BlockContent {
            @Override public Margins getMargins() { return null; }
            @Override public Position getPosition() { return Position.STATIC; }
            @Override public int getHeight() { return 0; }
            @Override public int getWidth() { return 0; }
            @Override public void render(PlayerEntity player, int x, int y, int width, int height) {}
        }

        @XML.Name("line")
        public record Line(
                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
                @XML.Attribute(fallBack = "text_locale") Alignment align,
                @XML.Attribute(fallBack = "") String size,
                @XML.Attribute(fallBack = "0.5u, 0u, 1u, 0u") Margins margin,
                @XML.Attribute(fallBack = "static") Position position
        ) implements BlockContent, TextContent {

            @Override
            public net.minecraft.text.Text toText() {
                return Text.concat(this.children);
            }

            @Override public Margins getMargins() { return margin; }
            @Override public Position getPosition() { return position; }
            @Override public int getWidth() { return 0; }
            @Override public int getHeight() { return 0; }
            @Override public void render(final PlayerEntity player, final int x, final int y, final int width, final int height) {}
        }
    }

    @XML.Root
    public record Text(@XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children) implements XMLNode {
        public static final XMLParser<Text> XML_PARSER;

        static {
            try {
                XML_PARSER = new XMLParser<>(Text.class, Codex.ATTRIBUTE_PARSER);
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

        public static net.minecraft.text.Text parse(String string) throws XMLException {
            return XML_PARSER.parse(string).toText();
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
        public record TranslatedText(@XML.Attribute(fallBack = "") String key) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return net.minecraft.text.Text.translatable(key);
            }
        }

        @XML.Name("fmt")
        public record FormatedText(
                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
                @XML.Attribute(fallBack = "") Formatting[] style,
                @XML.Attribute(fallBack = "") Color color
        ) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                var base = concat(this.children).formatted(style);
                return (color != null && color.isValid()) ? base.withColor(color.rgb()) : base;
            }
        }

        @XML.Name("font")
        public record FontText(
                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
                @XML.Attribute(fallBack = "") Color color
        ) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                var base = concat(this.children);
                return (color != null && color.isValid()) ? base.withColor(color.rgb()) : base;
            }
        }

        @XML.Name("a")
        public record Anchor(
                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
                @XML.Attribute(fallBack = "") String title,
                @XML.Attribute(fallBack = "") String href,
                @XML.Attribute(fallBack = "") String onclick,
                @XML.Attribute(fallBack = "") String hoverEvent,
                @XML.Attribute(fallBack = "blue, underline") Formatting[] style
        ) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                final ClickEvent ce;
                final var href = this.href.strip();
                final var onclick = this.onclick.strip();
                final var hoverEvent = this.hoverEvent.strip();
                final HoverEvent he;

                if (href.startsWith("#")) {
                    ce = new ClickEvent(ClickEvent.Action.CHANGE_PAGE, href.substring(1).strip());
                } else if (href.toLowerCase(Locale.ROOT).startsWith("file:")) {
                    ce = new ClickEvent(ClickEvent.Action.OPEN_FILE, href.substring(5).strip());
                } else if (!href.isEmpty()) {
                    ce = new ClickEvent(ClickEvent.Action.OPEN_URL, href);
                } else if (onclick.toLowerCase(Locale.ROOT).startsWith("^")) {
                    ce = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, onclick.substring(1).strip());
                } else if (onclick.toLowerCase(Locale.ROOT).startsWith("?")) {
                    ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + onclick.substring(1).strip());
                } else if (onclick.toLowerCase(Locale.ROOT).startsWith("?/")) {
                    ce = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + onclick.substring(2).strip());
                } else if (onclick.toLowerCase(Locale.ROOT).startsWith("/")) {
                    ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, onclick);
                } else {
                    ce = null;
                }
                if (hoverEvent.startsWith("item:")) {
                    String itemId = hoverEvent.substring(5).strip();
                    Identifier id = Identifier.of(itemId);
                    Item item = net.minecraft.registry.Registries.ITEM.get(id);

                    ItemStack stack = new ItemStack(item);
                    HoverEvent.ItemStackContent itemContent = new HoverEvent.ItemStackContent(stack);
                    he = new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemContent);
                } else if (hoverEvent.startsWith("entity:")) {
                    String entityId = hoverEvent.substring(7).strip();
                    Identifier id = Identifier.of(entityId);
                    EntityType<?> entityType = net.minecraft.registry.Registries.ENTITY_TYPE.get(id);

                    HoverEvent.EntityContent entityContent = new HoverEvent.EntityContent(entityType, UUID.fromString("0-0-0-0-0"), (net.minecraft.text.Text) null);
                    he = new HoverEvent(HoverEvent.Action.SHOW_ENTITY, entityContent);
                } else if (hoverEvent.startsWith("text:")) {
                    he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, net.minecraft.text.Text.translatable(hoverEvent.substring(5).strip()));
                } else {
                    he = null;
                }

                return concat(this.children).formatted(style).styled(s -> s.withClickEvent(ce).withHoverEvent(title.isBlank() ? he : new HoverEvent(HoverEvent.Action.SHOW_TEXT, net.minecraft.text.Text.translatable(title))));
            }
        }
    }
    public static class CodexUtils {

        public record AnchorData(String text, String href, String onclick, String title) {}

        /**
         * Collects all Anchor elements across all pages in the Codex.
         */
        public static List<AnchorData> collectAnchors(Codex codex) {
            List<AnchorData> anchors = new ArrayList<>();
            for (Codex.Page page : codex.children()) {
                collectFromBlocks(page.children(), anchors);
            }
            return anchors;
        }

        private static void collectFromBlocks(XMLNodeCollection<Codex.BlockContent> blocks, List<AnchorData> anchors) {
            for (Codex.BlockContent block : blocks) {
                if (block instanceof Codex.Page.Line line) {
                    collectFromTextNodes(line.children(), anchors);
                } else if (block instanceof Codex.Page.IfCondition ifCond) {
                    collectFromBlocks(ifCond.children(), anchors);
                } else if (block instanceof Codex.Page.UnlessCondition unlessCond) {
                    collectFromBlocks(unlessCond.children(), anchors);
                }
            }
        }

        private static void collectFromTextNodes(XMLNodeCollection<Codex.TextContent> nodes, List<AnchorData> anchors) {
            for (Codex.TextContent node : nodes) {
                if (node instanceof Codex.Text.Anchor anchor) {
                    anchors.add(new AnchorData(
                            anchor.toText().getString(),
                            anchor.href(),
                            anchor.onclick(),
                            anchor.title()
                    ));
                }
            }
        }
    }
}