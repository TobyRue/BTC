package io.github.tobyrue.btc;

import io.github.tobyrue.xml.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Objects;

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

    public record Page(@XML.Children(allow = {Line.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "") String requires) implements ConditionalNode, XMLNode, Render  {
        @Override
        public Identifier getAdvancement() {
            if (!Objects.equals(requires, "")) {
                int colonIndex = requires.indexOf(':');
                if (colonIndex == -1) {
                    // No namespace, default to "minecraft"
                    System.err.println("§c[ERROR] Invalid advancement ID '" + requires + "': missing namespace. Defaulting to 'minecraft'");
                    return Identifier.of("minecraft", requires);
                }

                String namespace = requires.substring(0, colonIndex);
                String path = requires.substring(colonIndex + 1);

                System.out.println("Namespace detected: " + namespace);
                System.out.println("Path detected: " + path);

                return Identifier.of(namespace, path);
            } else {
                return null;
            }
        }


        @Override
        public void render(DrawContext context, int width, int height, float delta) {
            //TODO
//            context.drawText()
        }

        public record Line(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "") String requires) implements XMLNode, TextContent, ConditionalNode {
            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children);
            }

            @Override
            public Identifier getAdvancement() {
                if (!Objects.equals(requires, "")) {
                    int colonIndex = requires.indexOf(':');
                    if (colonIndex == -1) {
                        // No namespace, default to "minecraft"
                        System.err.println("§c[ERROR] Invalid advancement ID '" + requires + "': missing namespace. Defaulting to 'minecraft'");
                        return Identifier.of("minecraft", requires);
                    }

                    String namespace = requires.substring(0, colonIndex);
                    String path = requires.substring(colonIndex + 1);

                    System.out.println("Namespace detected: " + namespace);
                    System.out.println("Path detected: " + path);

                    return Identifier.of(namespace, path);
                } else {
                    return null;
                }
            }
        }
    }






    @XML.Root
    public record Text(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "") String requires) implements TextContent, ConditionalNode {
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
            if (!Objects.equals(requires, "")) {
                int colonIndex = requires.indexOf(':');
                if (colonIndex == -1) {
                    // No namespace, default to "minecraft"
                    System.err.println("§c[ERROR] Invalid advancement ID '" + requires + "': missing namespace. Defaulting to 'minecraft'");
                    return Identifier.of("minecraft", requires);
                }

                String namespace = requires.substring(0, colonIndex);
                String path = requires.substring(colonIndex + 1);

                System.out.println("Namespace detected: " + namespace);
                System.out.println("Path detected: " + path);

                return Identifier.of(namespace, path);
            } else {
                return null;
            }
        }

        @Override
        public net.minecraft.text.Text toText() {
            getAdvancement();
            System.out.println(requires);
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
            public static Formatting parseFormatting(String text) throws XMLException {
                Formatting fmt = Formatting.byName(text);
                if (fmt == null)
                    throw new XMLException("Invalid formatting name: " + text);
                return fmt;
            }

            private static final XMLParser.AttributeParser customParser;
            static {
                try {
                    customParser = new XMLParser.AttributeParser(Formatting::byName);
                } catch (XMLException e) {
                    throw new RuntimeException(e);
                }
            }

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
