package io.github.tobyrue.btc.client.screen.codex;

import io.github.tobyrue.btc.AdvancementParser;
import io.github.tobyrue.btc.client.screen.codex.style.Margins;
import io.github.tobyrue.xml.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

@XML.Root
public record Codex(@XML.Children(allow = {Page.class}) XMLNodeCollection<Page> children) implements XMLNode {
    public static final XMLParser<Codex> XML_PARSER;
    static {
        try {
            XML_PARSER = new XMLParser<>(Codex.class, new XMLParser.AttributeParser(
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
                    Margins::parseMargins
            ));
        } catch (final XMLException e) {
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

    private enum TextAlignment {
        LEFT,
        RIGHT,
        CENTER,
        ABSOLUTE;

        private static final Map<String, TextAlignment> BY_NAME;


        @Nullable
        public static TextAlignment byName(@Nullable String name) {
            if (name == null) {
                return null;
            }
            return BY_NAME.get(TextAlignment.sanitize(name));
        }

        static {
            BY_NAME = Arrays.stream(TextAlignment.values()).collect(Collectors.toMap(f -> TextAlignment.sanitize(f.name()), f -> f));
        }
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

    private static TextAlignment parseTextAlignment(final String text) throws XMLException {
        try {
            return Objects.requireNonNull(TextAlignment.byName(text));
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

    public interface BlockContent extends XMLNode {
        Margins getMargins();
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
    ) implements XMLNode, ConditionalContent {
        @Override
        public boolean isRequirementMet(ServerPlayerEntity player) {
            return requires.evaluate(player);
        }

        @XML.Name("p")
        public record Paragraph(
                @XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children,
                @XML.Attribute(fallBack = "true") AdvancementParser.Expression requires,
                @XML.Attribute(fallBack = "left") TextAlignment align,
                @XML.Attribute(fallBack = "0") Integer x,
                @XML.Attribute(fallBack = "0") Integer y
        ) implements BlockContent, ConditionalContent {

            @Override
            public boolean isRequirementMet(ServerPlayerEntity player) {
                return requires.evaluate(player);
            }

            //TODO
            @Override
            public Margins getMargins() {
                return null;
            }
        }
    }

    @XML.Root
    public record Text(@XML.Children(allow = {TextContent.class}) XMLNodeCollection<TextContent> children) implements XMLNode {
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
    }
}
