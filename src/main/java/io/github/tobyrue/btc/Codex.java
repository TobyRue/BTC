package io.github.tobyrue.btc;

import io.github.tobyrue.xml.XML;
import io.github.tobyrue.xml.XMLNode;
import io.github.tobyrue.xml.XMLNodeCollection;
import io.github.tobyrue.xml.XMLTextNode;
import net.minecraft.text.Text;

public record Codex() {
    public interface TextNode extends XMLNode {
        net.minecraft.text.Text toText();
    }

    @XML.Root
    public record Text(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
        @Override
        public net.minecraft.text.Text toText() {
            return null;
        }

        @XML.Name("b")
        public record Bold(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return null;
            }
        }
        @XML.Name("i")
        public record Italic(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return null;
            }
        }

        public record Font(@XML.Children(allow = {XMLTextNode.class, TextNode.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "black") String color) implements TextNode {
            @Override
            public net.minecraft.text.Text toText() {
                return null;
            }
        }
    }
}
