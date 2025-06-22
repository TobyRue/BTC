package io.github.tobyrue.btc;

import com.google.common.io.Resources;

import javax.xml.stream.XMLInputFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SpellBookXMLParser {
    public static void parse(String xmlText) {

    }
    public static String loadResource() {
        try {
            return Resources.toString(Objects.requireNonNull(SpellBookXMLParser.class.getResource("/spellbook.xml")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String loadResourceTest() {
        try {
            return Resources.toString(Objects.requireNonNull(SpellBookXMLParser.class.getResource("/test.xml")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
