package io.github.tobyrue.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public final class XMLNodeCollection<T extends XMLNode> implements Iterable<T> {
    private final List<T> list = new ArrayList<>();
    void add(T t) {
        this.list.add(t);
    }
    public List<T> getChildren() {
        return this.list.stream().toList();
    }
    public Stream<T> stream() {
        return this.list.stream();
    }
    @Override
    public Iterator<T> iterator() {
        return this.list.iterator();
    }
}