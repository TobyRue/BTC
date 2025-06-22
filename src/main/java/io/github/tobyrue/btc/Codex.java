package io.github.tobyrue.btc;

import io.github.tobyrue.xml.XML;
import io.github.tobyrue.xml.XMLNode;
import io.github.tobyrue.xml.XMLNodeCollection;
import io.github.tobyrue.xml.XMLTextNode;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@XML.Root
public record Codex(@XML.Children(allow = {Page.class}) XMLNodeCollection<Page> children) implements XMLNode {
    public interface TextContent extends XMLNode {
        net.minecraft.text.Text toText();
    }
    public interface ConditionalNode {
        Identifier getAdvancement();
    }

    public interface Render {
        void render(DrawContext context, int width, int height, float delta);
    }

    public Page getPage(int n) {
        return this.children.getChildren().get(n);
    }

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



    public record Page(@XML.Children(allow = {Line.class}) XMLNodeCollection<?> children, @XML.Attribute String requires) implements ConditionalNode, XMLNode, Render  {
        @Override
        public Identifier getAdvancement() {
            if (requires != null) {
                return Identifier.of(requires);
            } else {
                return null;
            }
        }

        @Override
        public void render(DrawContext context, int width, int height, float delta) {
            context.drawText()
        }

        public record Line(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute String requires) implements XMLNode, Codex.TextContent, ConditionalNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children);
            }

            @Override
            public Identifier getAdvancement() {
                if (requires != null) {
                    return Identifier.of(requires);
                } else {
                    return null;
                }
            }
        }
    }






    @XML.Root
    public record Text(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute String requires) implements TextContent, ConditionalNode {
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
        public Identifier getAdvancement() {
            if (requires != null) {
                return Identifier.of(requires);
            } else {
                return null;
            }
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
