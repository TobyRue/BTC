package io.github.tobyrue.btc;

import io.github.tobyrue.xml.XML;
import io.github.tobyrue.xml.XMLNode;
import io.github.tobyrue.xml.XMLNodeCollection;
import io.github.tobyrue.xml.XMLTextNode;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record Codex() {
    public interface TextNode extends XMLNode {
        net.minecraft.text.Text toText();
    }

    @XML.Root
    public record Text(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
        public static net.minecraft.text.MutableText concat(final XMLNodeCollection<?> nodes) {
            var text = net.minecraft.text.Text.empty();
            for (var node : nodes) {
                if (node instanceof XMLTextNode) {
                    text.append(net.minecraft.text.Text.literal(((XMLTextNode) node).text()));
                } else if (node instanceof TextNode) {
                    text.append(((TextNode) node).toText());
                }
            }
            return text;
        }

        @Override
        public net.minecraft.text.Text toText() {
            return concat(this.children);
        }

        @XML.Name("b")
        public record BoldText(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.BOLD);
            }
        }
        @XML.Name("i")
        public record ItalicText(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.ITALIC);
            }
        }
        @XML.Name("obf")
        public record ObfuscatedText(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.OBFUSCATED);
            }
        }
        @XML.Name("u")
        public record UnderlinedText(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.UNDERLINE);
            }
        }
        @XML.Name("s")
        public record StrikethroughText(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.STRIKETHROUGH);
            }
        }
        @XML.Name("fmt")
        public record FormatedText(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "reset") String style) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.byName(style));
            }
        }
        @XML.Name("t")
        public record TranslatedText(String key) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return net.minecraft.text.Text.translatable(key);
            }
        }
    }
}
