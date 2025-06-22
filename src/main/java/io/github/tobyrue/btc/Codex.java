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
        public record Bold(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.BOLD);
            }
        }
        @XML.Name("i")
        public record Italic(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.ITALIC);
            }
        }

        public record Font(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "black") String color) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children).formatted(Formatting.RED);
            }
        }
    }
}
