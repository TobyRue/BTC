package io.github.tobyrue.btc;

import io.github.tobyrue.xml.XML;
import io.github.tobyrue.xml.XMLNode;
import io.github.tobyrue.xml.XMLNodeCollection;
import io.github.tobyrue.xml.XMLTextNode;
import net.minecraft.util.Formatting;

@XML.Root
public record Codex(@XML.Children(allow = {CodexContent.class}) XMLNodeCollection<?> children) {
    public interface CodexContent extends XMLNode {}
    public interface PageContent extends XMLNode {}
    public interface TextContent extends XMLNode {
        net.minecraft.text.Text toText();
    }
    public interface ConditionalTag {}

    public record Page(@XML.Children(allow = {PageContent.class}) XMLNodeCollection<?> children) implements CodexContent {
        public record Line(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children) implements PageContent {

        }
    }

    public record If(@XML.Children(allow = {PageContent.class, CodexContent.class}) XMLNodeCollection<?> children, String predicate) {

    }
    public record Unless(@XML.Children(allow = {PageContent.class, CodexContent.class}) XMLNodeCollection<?> children, String predicate) {

    }

    @XML.Root
    public record Text(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children) implements TextContent {
        public static net.minecraft.text.MutableText concat(final XMLNodeCollection<?> nodes) {
            var text = net.minecraft.text.Text.empty();
            for (var node : nodes) {
                if (node instanceof XMLTextNode) {
                    text.append(net.minecraft.text.Text.literal(((XMLTextNode) node).text()));
                } else if (node instanceof TextContent) {
                    text.append(((TextContent) node).toText());
                }
            }
            return text;
        }

        @Override
        public net.minecraft.text.Text toText() {
            return concat(this.children);
        }

        @XML.Name("b")
        public record BoldText(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.BOLD);
            }
        }
        @XML.Name("i")
        public record ItalicText(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.ITALIC);
            }
        }
        @XML.Name("obf")
        public record ObfuscatedText(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.OBFUSCATED);
            }
        }
        @XML.Name("u")
        public record UnderlinedText(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.UNDERLINE);
            }
        }
        @XML.Name("s")
        public record StrikethroughText(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.STRIKETHROUGH);
            }
        }
        @XML.Name("fmt")
        public record FormatedText(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "reset") String style) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.byName(style));
            }
        }
        @XML.Name("t")
        public record TranslatedText(String key) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                return net.minecraft.text.Text.translatable("item.btc.spell.codex." + key);
            }
        }
    }
}
