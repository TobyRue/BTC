package io.github.tobyrue.xml;

public record XMLTextNode(String text) implements XMLNode {
    @Override
    public String toString() {
        return text;
    }
}
