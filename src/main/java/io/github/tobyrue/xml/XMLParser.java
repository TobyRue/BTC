package io.github.tobyrue.xml;

import io.github.tobyrue.xml.util.Nullable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class XMLParser<T extends Record & XMLNode> {
    private final AttributeParser attributeParser;
    private final boolean strict, ignoreCase;
    private final DiMap<String, Class<?>, ClassData<?>> data;

    public XMLParser(final Class<T> clazz) throws XMLException {
        this(clazz, AttributeParser.DEFAULT);
    }
    public XMLParser(final Class<T> clazz, final AttributeParser attributeParser) throws XMLException {
        this.attributeParser = attributeParser;

        try {
            XML.Root rootInfo = clazz.getAnnotation(XML.Root.class);
            this.strict = rootInfo.strict();
            this.ignoreCase = rootInfo.ignoreCase();
        } catch (Exception e) {
            throw new XMLException(String.format("Class %s is not marked XML.Root", clazz), e);
        }

        this.data = this.getClassData(clazz);
    }

    @SuppressWarnings("unchecked")
    public final T parse(final Reader reader) throws XMLException {
        try {
            final Stack<XMLNode> stack = new Stack<>();
            var stream = XMLInputFactory.newFactory().createXMLStreamReader(new BufferedReader(reader));
            try {
                while (stream.hasNext()) {
                    switch (stream.getEventType()) {
                        case XMLStreamReader.START_ELEMENT -> {
                            final var attributes = new HashMap<String, String>();
                            for (int i = 0; i < stream.getAttributeCount(); i++) {
                                attributes.put(normalize(stream.getAttributeLocalName(i)), stream.getAttributeValue(i));
                            }
                            final var d = Objects.requireNonNull(data.getByK1(normalize(stream.getLocalName())),
                                    () -> String.format("No class found to deserialize tag '%s'", normalize(stream.getLocalName()))
                            );
                            final var parent = stack.isEmpty() ? null : stack.peek();
                            final var node = d.create(parent, attributes);

                            if (parent != null) {
                                data.getByK2(parent.getClass()).addChild(parent, node);
                            }

                            stack.add(node);
                        }
                        case XMLStreamReader.END_ELEMENT -> {
                            var t = stack.pop();
                            if (stack.empty()) {
                                stream.close();
                                return (T) t;
                            }
                        }
                        case XMLStreamReader.CHARACTERS -> {
                            data.getByK2(stack.peek().getClass()).addChild(stack.peek(), new XMLTextNode(stream.getText()));
                        }
                        case XMLStreamReader.ATTRIBUTE, XMLStreamReader.DTD, XMLStreamReader.CDATA, XMLStreamReader.COMMENT, XMLStreamReader.START_DOCUMENT,
                             XMLStreamReader.END_DOCUMENT, XMLStreamReader.ENTITY_DECLARATION, XMLStreamReader.ENTITY_REFERENCE, XMLStreamReader.NAMESPACE,
                             XMLStreamReader.NOTATION_DECLARATION, XMLStreamReader.PROCESSING_INSTRUCTION, XMLStreamReader.SPACE-> {
                        }
                    }
                    stream.next();
                }
            } catch (XMLException e) {
                throw e.appendLocation(stream.getLocation().getLineNumber(), stream.getLocation().getColumnNumber());
            }
        } catch (XMLStreamException e) {
            throw new XMLException("Unable to create stream", e);
        }
        throw new XMLException("Unknown Error");
    }

    public static <T extends Record & XMLNode> T parse(final Reader reader, final Class<T> clazz) throws XMLException {
        return new XMLParser<>(clazz).parse(reader);
    }

    public final T parse(final String text) throws XMLException {
        try (var s = new StringReader(text)) {
            return this.parse(s);
        }
    }

    public static <T extends Record & XMLNode> T parse(final String text, final Class<T> clazz) throws XMLException {
        return new XMLParser<>(clazz).parse(text);
    }

    private String normalize(final String text) {
        if (text == null) {
            throw new NullPointerException();
        } else if (this.ignoreCase) {
            return text.toLowerCase();
        } else {
            return text;
        }
    }
    private boolean areStringsEqual(final String lhs, final String rhs) {
        if (lhs == null || rhs == null) {
            throw new NullPointerException();
        } else {
            return normalize(lhs).equals(normalize(rhs));
        }
    }
    @SuppressWarnings("rawtypes, unchecked")
    private DiMap<String, Class<?>, ClassData<?>> getClassData(Class<? extends T> root) throws XMLException {
        DiMap<String, Class<?>, ClassData<?>> classes = new DiMap<>();
        Queue<Class<?>> q = new LinkedList<>();
        Class<?> clazz;

        q.add(root);

        while ((clazz = q.poll()) != null) {
            if (XMLNode.class.isAssignableFrom(clazz) && clazz.isRecord()) {
                var d = new ClassData(clazz);
                classes.putIfAbsent(d.getName(), clazz, d);
            }
            q.addAll(List.of(clazz.getDeclaredClasses()));
        }
        return classes;
    }

    private final class AttributeData<T> {
        private final int pos;
        private final Class<T> clazz;
        private final String name;
        private final String fallback;

        private AttributeData(final String name, final Class<T> clazz, final int pos, @Nullable final String fallback) throws XMLException {
            if (!attributeParser.canParseType(clazz)) {
                throw new XMLException(String.format(
                        "Parameter '%s' of type %s cannot be parsed as an attribute (%s)",
                        name, clazz,
                        clazz == XMLNode.class ? "Did you mean to mark it as XML.Parent?" : clazz == XMLNodeCollection.class ? "Did you mean to mark it as XML.Children?" : "Do you need to add a custom attribute parser?"
                ));
            }
            this.name = name;
            this.clazz = clazz;
            this.pos = pos;
            this.fallback = fallback;
        }

        public Class<T> getType() {
            return this.clazz;
        }
        public int getPosition() {
            return this.pos;
        }
        public String getName() {
            return this.name;
        }
        public String getFallback() {
            return this.fallback;
        }
    }

    private final class ClassData<T extends Record & XMLNode> {
        private final String name;
        private final Constructor<T> constructor;
        private final Class<T> clazz;
        private final int parentPos;
        private final int childPos;
        private final List<Class<?>> allowedChildren = new ArrayList<>();
        private final List<AttributeData<?>> attributes = new ArrayList<>();

        private ClassData(final Class<T> clazz) throws XMLException {
            this.name = normalize(clazz.isAnnotationPresent(XML.Name.class) ? clazz.getAnnotation(XML.Name.class).value() : clazz.getSimpleName());
            this.clazz = clazz;
            try {
                this.constructor = clazz.getDeclaredConstructor(Arrays.stream(clazz.getRecordComponents()).map(RecordComponent::getType).toArray(Class<?>[]::new));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            final var parameters = this.constructor.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                final var p = parameters[i];
                final var name = normalize(p.isAnnotationPresent(XML.Name.class) ? p.getAnnotation(XML.Name.class).value() : p.getName());
                final boolean xmlParent = p.isAnnotationPresent(XML.Parent.class),
                        xmlChildren = p.isAnnotationPresent(XML.Children.class),
                        xmlAttribute = p.isAnnotationPresent(XML.Attribute.class);

                if ((xmlParent ? 1 : 0) + (xmlChildren ? 1 : 0) + (xmlAttribute ? 1 : 0) > 1) {
                    throw new XMLException(String.format("Parameter '%s' may only be one of XML.Parent, XML.Children, or XML.Attribute", name));
                } else if (xmlParent) {
                    if (p.getType() != XMLNode.class) {
                        throw new XMLException(String.format("Parameter '%s' marked as XML.Parent must be of type XMLNode", name));
                    }
                } else if (xmlChildren) {
                    if (p.getType() != XMLNodeCollection.class) {
                        throw new XMLException(String.format("Parameter '%s' marked as XML.Children must be of type XMLNodeCollection", name));
                    }
                } else {
                    final var fallback = p.isAnnotationPresent(XML.Attribute.class) ? p.getAnnotation(XML.Attribute.class).fallBack() : "";
                    this.attributes.add(new AttributeData<>(name, p.getType(), i, "".equals(fallback) ? null : fallback));
                }
            }

            this.parentPos = IntStream.range(0, parameters.length).filter(i -> parameters[i].isAnnotationPresent(XML.Parent.class)).findFirst().orElse(-1);
            this.childPos = IntStream.range(0, parameters.length).filter(i -> parameters[i].isAnnotationPresent(XML.Children.class)).findFirst().orElse(-1);

            if (this.childPos != -1) {
                this.allowedChildren.addAll(Arrays.asList(parameters[childPos].getAnnotation(XML.Children.class).allow()));
            }
        }

        public T create(@Nullable final XMLNode parent, final Map<String, String> attributes) throws XMLException {
            var args = new Object[this.constructor.getParameters().length];
            if (childPos != -1) {
                args[childPos] = new XMLNodeCollection<>();
            }
            if (parentPos != -1) {
                args[parentPos] = parent;
            }
            if (strict) {
                final var t = attributes.keySet().stream().filter(k -> this.attributes.stream().noneMatch(a -> k.equals(a.getName()))).findFirst();
                if (t.isPresent()) {
                    throw new XMLException(String.format("Unexpected attribute '%s' on tag '%s'", t.get(), this.name));
                }
            }
            for (var a : this.attributes) {
                final String value;
                if (attributes.containsKey(a.getName())) {
                    value = attributes.get(a.getName());
                } else if (a.getFallback() != null) {
                    value = a.getFallback();
                } else {
                    throw new XMLException(String.format("No value or fallback value specified for attribute '%s'", a.getName()));
                }
                args[a.getPosition()] = attributeParser.parse(value, a.getType());
            }
            try {
                return this.constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new XMLException(String.format("Unable to create node '%s'", this.name), e);
            }
        }

        public Class<T> getType() {
            return this.clazz;
        }
        public String getName() {
            return name;
        }
        public Constructor<T> getCanonicalConstructor() {
            return this.constructor;
        }

        @SuppressWarnings("unchecked, rawtypes")
        public void addChild(XMLNode instance, XMLNode child) throws XMLException {
            try {
                if (this.childPos != -1) {
                    if (this.allowedChildren.isEmpty() || this.allowedChildren.stream().anyMatch(c -> c.isAssignableFrom(child.getClass()))) {
                        ((XMLNodeCollection) this.clazz.getRecordComponents()[this.childPos].getAccessor().invoke(instance)).add(child);
                    } else {
                        throw new XMLException(String.format("Node '%s' does not allow child of type '%s' (Do you need to add it to the allowed types of XML.Attribute?)", this.name, data.getByK2(child.getClass()).getName()));
                    }
                } else {
                    throw new XMLException(String.format("Node '%s' does not allow any children", this.name));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new XMLException(String.format("Unable to add child to node '%s'", this.name), e);
            }
        }
    }
    public static final class AttributeParser {
        @FunctionalInterface
        public interface AttributeTypeParser<T> extends Serializable {
            T parse(final String text) throws XMLException;
        }

        public static final AttributeParser DEFAULT;
        static {
            try {
                DEFAULT = new AttributeParser(
                        AttributeParser::parseString,
                        AttributeParser::parseByte,
                        AttributeParser::parseShort,
                        AttributeParser::parseInteger,
                        AttributeParser::parseLong,
                        AttributeParser::parseFloat,
                        AttributeParser::parseDouble,
                        AttributeParser::parseBoolean
                );
            } catch (final XMLException e) {
                throw new RuntimeException(e);
            }
        }

        private final Map<Class<?>, AttributeTypeParser<?>> handlers = new HashMap<>();

        public AttributeParser(final AttributeTypeParser<?>... functions) throws XMLException {
            for (var f : functions) {
                handlers.put(getReturnTypeOf(f), f);
            }
        }
        @SuppressWarnings("unchecked, rawtypes")
        public <T> T parse(final String text, final Class<T> clazz) throws XMLException {
            return (T) this.handlers.getOrDefault(clazz, s -> {
                if (clazz.isEnum()) {
                    return valueOfEnum((Class<Enum>) clazz, text);
                } else {
                    throw new XMLException(String.format("No method to parse %s", clazz));
                }
            }).parse(text);
        }

        private static <T extends Enum<T>> T valueOfEnum(final Class<T> enumClass, final String value) {
            return Enum.valueOf(enumClass, value);
        }

        public Boolean canParseType(final Class<?> clazz) {
            return this.handlers.containsKey(clazz) || clazz.isEnum();
        }
        public static String parseString(final String text) {
            return text;
        }
        public static Byte parseByte(final String text) throws XMLException {
            try {
                return Byte.decode(text);
            } catch (final NumberFormatException e) {
                throw new XMLException(String.format("Cannot parse '%s' as byte", text), e);
            }
        }
        public static Short parseShort(final String text) throws XMLException {
            try {
                return Short.decode(text);
            } catch (final NumberFormatException e) {
                throw new XMLException(String.format("Cannot parse '%s' as short", text), e);
            }
        }
        public static Integer parseInteger(final String text) throws XMLException {
            try {
                return Integer.decode(text);
            } catch (final NumberFormatException e) {
                throw new XMLException(String.format("Cannot parse '%s' as integer", text), e);
            }
        }
        public static Long parseLong(final String text) throws XMLException {
            try {
                return Long.decode(text);
            } catch (final NumberFormatException e) {
                throw new XMLException(String.format("Cannot parse '%s' as long", text), e);
            }
        }
        public static Float parseFloat(final String text) throws XMLException {
            try {
                var t = text.trim();
                if (t.charAt(t.length() - 1) == '%') {
                    return parseFloat(t.substring(0, t.length() - 1)) / 100f;
                } else {
                    return Float.parseFloat(text);
                }
            } catch (final NumberFormatException e) {
                throw new XMLException(String.format("Cannot parse '%s' as float", text), e);
            }
        }
        public static Double parseDouble(final String text) throws XMLException {
            try {
                var t = text.trim();
                if (t.charAt(t.length() - 1) == '%') {
                    return parseDouble(t.substring(0, t.length() - 1)) / 100d;
                } else {
                    return Double.parseDouble(text);
                }
            } catch (final NumberFormatException e) {
                throw new XMLException(String.format("Cannot parse '%s' as double", text), e);
            }
        }
        public static Boolean parseBoolean(final String text) throws XMLException {
            return switch (text.trim().toLowerCase()) {
                case "", "false" -> false;
                case "true" -> true;
                default -> throw new XMLException(String.format("Cannot parse '%s' as boolean", text));
            };
        }

        @SuppressWarnings("unchecked")
        private static <T> Class<T> getReturnTypeOf(final AttributeTypeParser<T> f) throws XMLException {
            try {
                final Method method = f.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(true);
                var lambda = ((SerializedLambda) method.invoke(f));
                // TODO Sacrifice goat here
                return (Class<T>) Arrays.stream(Class.forName(lambda.getImplClass().replace('/', '.')).getDeclaredMethods()).filter(m -> m.getName().equals(lambda.getImplMethodName())).findFirst().orElseThrow().getReturnType();
            } catch (final NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
                throw new XMLException("Unable to get return type of lambda", e);
            }
        }
    }

    private record Tuple<L, R>(L lhs, R rhs) {}

    private static final class DiMap<K1, K2, V> {
        private final Map<K1, V> m1 = new HashMap<>();
        private final Map<K2, V> m2 = new HashMap<>();

        public V putIfAbsent(K1 k1, K2 k2, V v) {
            m1.putIfAbsent(k1, v);
            return m2.putIfAbsent(k2, v);
        }
        public V getByK1(K1 k1) {
            return m1.get(k1);
        }
        public V getByK2(K2 k2) {
            return m2.get(k2);
        }
    }
}
