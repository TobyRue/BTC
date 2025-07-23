package io.github.tobyrue.btc;

import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.xml.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

@XML.Root
public record Codex(@XML.Children(allow = {Page.class}) XMLNodeCollection<Page> children) implements XMLNode {


    public interface TextContent extends XMLNode {
        net.minecraft.text.Text toText();
    }

    public interface ConditionalNode {
        Identifier getAdvancement();
        boolean isRequirementMet(ServerPlayerEntity player);
        boolean requirementMet(ServerPlayerEntity player) throws Exception;
        String getRequires();
    }

    public interface Render {
        void render(DrawContext context, int width, int height, float delta);
    }

    public Page getPage(int n) {
        return this.children.getChildren().get(n);
    }

    public static MutableText concat(final XMLNodeCollection<?> nodes) {
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

    public record Page(@XML.Children(allow = {Line.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "true") String requires) implements ConditionalNode, XMLNode, Render  {
        @Override public String getRequires() { return requires.replace(':', '.').replace("&", "-and-").replace("|", "-or-"); }
        static boolean isInvertedAdvancementPage = false;
        @Override
        public Identifier getAdvancement() {
            if (!Objects.equals(requires, "")) {
                int colonIndex = requires.indexOf(':');
                if (colonIndex == -1) {
                    System.err.println("§c[ERROR] Invalid advancement ID '" + requires + "': missing namespace. Defaulting to 'minecraft'");
                    if (requires.startsWith("!")) {
                        return Identifier.of("minecraft", requires.substring(1));
                    } else {
                        return Identifier.of("minecraft", requires);
                    }
                }
                String namespace;
                if (requires.startsWith("!")) {
                    isInvertedAdvancementPage = true;
                    namespace = requires.substring(1, colonIndex);
                } else {
                    isInvertedAdvancementPage = false;
                    namespace = requires.substring(0, colonIndex);
                }
                String path = requires.substring(colonIndex + 1);

                System.out.println("Namespace detected: " + namespace);
                System.out.println("Path detected: " + path);
                System.out.println("Is inverted: " + isInvertedAdvancementPage());

                return Identifier.of(namespace, path);
            } else {
                return null;
            }
        }

        @Override
        public boolean requirementMet(ServerPlayerEntity player) throws Exception {
            var parsed = AdvancementParser.parse(requires);
            return parsed.evaluate(player);
        }

        public boolean isRequirementMet(ServerPlayerEntity player) {
            String reqStr = getRequires();
            if (reqStr == null || reqStr.isEmpty()) {
                System.out.println("[Codex] No requirements — returning true.");
                return true;
            }

            System.out.println("[Codex] Requirement string: " + reqStr);

            List<Boolean> values = new ArrayList<>();
            List<String> operators = new ArrayList<>();

            // Collect operators in order
            for (int i = 0; i < reqStr.length(); i++) {
                char c = reqStr.charAt(i);
                if (c == '*') {
                    operators.add("*");
                    System.out.println("[Codex] Found AND operator '*'");
                } else if (c == '+') {
                    operators.add("+");
                    System.out.println("[Codex] Found OR operator '+'");
                }
            }

            // Split string into individual advancement checks using regex
            String[] parts = reqStr.split("[*+]");

            // Parse each advancement requirement
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;

                boolean inverted = part.startsWith("!");
                if (inverted) {
                    part = part.substring(1);
                    System.out.println("[Codex] Inversion detected for: " + part);
                }

                int colonIndex = part.indexOf(':');
                if (colonIndex == -1) {
                    part = "minecraft:" + part;
                    colonIndex = part.indexOf(':');
                    System.out.println("[Codex] No namespace — defaulted to minecraft: " + part);
                }

                String namespace = part.substring(0, colonIndex);
                String path = part.substring(colonIndex + 1);

                System.out.println("[Codex] Checking advancement: " + namespace + ":" + path);

                Identifier id;
                try {
                    id = Identifier.of(namespace, path);
                } catch (Exception e) {
                    System.err.println("[ERROR] Invalid advancement ID: " + part + " (" + e.getMessage() + ")");
                    values.add(false); // fail-safe: treat invalid as false
                    continue;
                }

                var advancement = player.server.getAdvancementLoader().get(id);
                boolean hasAdvancement = advancement != null && player.getAdvancementTracker().getProgress(advancement).isDone();

                System.out.println("[Codex] " + id + " advancement is " + (hasAdvancement ? "complete" : "incomplete"));

                if (inverted) {
                    hasAdvancement = !hasAdvancement;
                    System.out.println("[Codex] Inverted result: " + hasAdvancement);
                }

                values.add(hasAdvancement);
            }

            System.out.println("[Codex] Values collected: " + values);
            System.out.println("[Codex] Operators collected: " + operators);

            // Now combine the results according to the operators
            while (values.size() > 1) {
                String op = operators.remove(0);
                boolean left = values.remove(0);
                boolean right = values.remove(0);

                boolean result;
                if (op.equals("*")) {
                    result = left && right;
                    System.out.println("[Codex] Evaluating: " + left + " AND " + right + " = " + result);
                } else if (op.equals("+")) {
                    result = left || right;
                    System.out.println("[Codex] Evaluating: " + left + " OR " + right + " = " + result);
                } else {
                    throw new IllegalStateException("[Codex] Unknown operator: " + op);
                }

                // Put result back at the front of the list
                values.add(0, result);
                System.out.println("[Codex] Intermediate values: " + values);
            }

            System.out.println("[Codex] Final requirement result: " + values.get(0));
            return values.get(0);
        }


        public static boolean isInvertedAdvancementPage() {
            return isInvertedAdvancementPage;
        }

        @Override
        public void render(DrawContext context, int width, int height, float delta) {
            //TODO
//            context.drawText()
        }

        public record Line(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "true") String requires,  @XML.Attribute(fallBack = "left") String align, @XML.Attribute(fallBack = "0") Integer alignInt) implements XMLNode, TextContent, ConditionalNode {
            @Override public String getRequires() { return requires.replace(':', '.').replace("&", "-and-").replace("|", "-or-"); }
            static boolean isInvertedAdvancementLine = false;

            @Override
            public net.minecraft.text.Text toText() {
                return concat(this.children);
            }

            @Override
            public Identifier getAdvancement() {
                if (!Objects.equals(requires, "")) {
                    int colonIndex = requires.indexOf(':');
                    if (colonIndex == -1) {
                        System.err.println("§c[ERROR] Invalid advancement ID '" + requires + "': missing namespace. Defaulting to 'minecraft'");
                        if (requires.startsWith("!")) {
                            return Identifier.of("minecraft", requires.substring(1));
                        } else {
                            return Identifier.of("minecraft", requires);
                        }
                    }
                    String namespace;
                    if (requires.startsWith("!")) {
                        isInvertedAdvancementLine = true;
                        namespace = requires.substring(1, colonIndex);
                    } else {
                        isInvertedAdvancementLine = false;
                        namespace = requires.substring(0, colonIndex);
                    }
                    String path = requires.substring(colonIndex + 1);

                    System.out.println("Namespace detected: " + namespace);
                    System.out.println("Path detected: " + path);
                    System.out.println("Is inverted: " + isInvertedAdvancementLine());

                    return Identifier.of(namespace, path);
                } else {
                    return null;
                }
            }
            public static boolean isInvertedAdvancementLine() {
                return isInvertedAdvancementLine;
            }

            @Override
            public boolean requirementMet(ServerPlayerEntity player) throws Exception {
                var parsed = AdvancementParser.parse(requires);
                return parsed.evaluate(player);
            }

            public boolean isRequirementMet(ServerPlayerEntity player) {
                String reqStr = getRequires();
                if (reqStr == null || reqStr.isEmpty()) {
                    System.out.println("[Codex] No requirements — returning true.");
                    return true;
                }

                System.out.println("[Codex] Requirement string: " + reqStr);

                List<Boolean> values = new ArrayList<>();
                List<String> operators = new ArrayList<>();

                // Collect operators in order
                for (int i = 0; i < reqStr.length(); i++) {
                    char c = reqStr.charAt(i);
                    if (c == '*') {
                        operators.add("*");
                        System.out.println("[Codex] Found AND operator '*'");
                    } else if (c == '+') {
                        operators.add("+");
                        System.out.println("[Codex] Found OR operator '+'");
                    }
                }

                // Split string into individual advancement checks using regex
                String[] parts = reqStr.split("[*+]");

                // Parse each advancement requirement
                for (String part : parts) {
                    part = part.trim();
                    if (part.isEmpty()) continue;

                    boolean inverted = part.startsWith("!");
                    if (inverted) {
                        part = part.substring(1);
                        System.out.println("[Codex] Inversion detected for: " + part);
                    }

                    int colonIndex = part.indexOf(':');
                    if (colonIndex == -1) {
                        part = "minecraft:" + part;
                        colonIndex = part.indexOf(':');
                        System.out.println("[Codex] No namespace — defaulted to minecraft: " + part);
                    }

                    String namespace = part.substring(0, colonIndex);
                    String path = part.substring(colonIndex + 1);

                    System.out.println("[Codex] Checking advancement: " + namespace + ":" + path);

                    Identifier id;
                    try {
                        id = Identifier.of(namespace, path);
                    } catch (Exception e) {
                        System.err.println("[ERROR] Invalid advancement ID: " + part + " (" + e.getMessage() + ")");
                        values.add(false); // fail-safe: treat invalid as false
                        continue;
                    }

                    var advancement = player.server.getAdvancementLoader().get(id);
                    boolean hasAdvancement = advancement != null && player.getAdvancementTracker().getProgress(advancement).isDone();

                    System.out.println("[Codex] " + id + " advancement is " + (hasAdvancement ? "complete" : "incomplete"));

                    if (inverted) {
                        hasAdvancement = !hasAdvancement;
                        System.out.println("[Codex] Inverted result: " + hasAdvancement);
                    }

                    values.add(hasAdvancement);
                }

                System.out.println("[Codex] Values collected: " + values);
                System.out.println("[Codex] Operators collected: " + operators);

                // Now combine the results according to the operators
                while (values.size() > 1) {
                    String op = operators.remove(0);
                    boolean left = values.remove(0);
                    boolean right = values.remove(0);

                    boolean result;
                    if (op.equals("*")) {
                        result = left && right;
                        System.out.println("[Codex] Evaluating: " + left + " AND " + right + " = " + result);
                    } else if (op.equals("+")) {
                        result = left || right;
                        System.out.println("[Codex] Evaluating: " + left + " OR " + right + " = " + result);
                    } else {
                        throw new IllegalStateException("[Codex] Unknown operator: " + op);
                    }

                    // Put result back at the front of the list
                    values.add(0, result);
                    System.out.println("[Codex] Intermediate values: " + values);
                }

                System.out.println("[Codex] Final requirement result: " + values.get(0));
                return values.get(0);
            }


        }
    }





    @XML.Root
    //TODO Page and alignment requirements are temp look at SpellScreenTest to see what page boolean does
    public record Text(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "true") String requires, @XML.Attribute(fallBack = "left") String align, @XML.Attribute(fallBack = "0") Integer alignInt, @XML.Attribute(fallBack = "false") Boolean page) implements TextContent, ConditionalNode {
        static boolean isInvertedAdvancementText = false;
        @Override public String getRequires() { return requires.replace(':', '.'); }

        @Override
        public boolean requirementMet(ServerPlayerEntity player) throws Exception {
            var parsed = AdvancementParser.parse(requires);
            return parsed.evaluate(player);
        }
        
        public boolean isRequirementMet(ServerPlayerEntity player) {
            String reqStr = getRequires();
            if (reqStr == null || reqStr.isEmpty()) {
                System.out.println("[Codex] No requirements — returning true.");
                return true;
            }

            System.out.println("[Codex] Requirement string: " + reqStr);

            List<Boolean> values = new ArrayList<>();
            List<String> operators = new ArrayList<>();

            // Collect operators in order
            for (int i = 0; i < reqStr.length(); i++) {
                char c = reqStr.charAt(i);
                if (c == '*') {
                    operators.add("*");
                    System.out.println("[Codex] Found AND operator '*'");
                } else if (c == '+') {
                    operators.add("+");
                    System.out.println("[Codex] Found OR operator '+'");
                }
            }

            // Split string into individual advancement checks using regex
            String[] parts = reqStr.split("[*+]");

            // Parse each advancement requirement
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;

                boolean inverted = part.startsWith("!");
                if (inverted) {
                    part = part.substring(1);
                    System.out.println("[Codex] Inversion detected for: " + part);
                }

                int colonIndex = part.indexOf(':');
                if (colonIndex == -1) {
                    part = "minecraft:" + part;
                    colonIndex = part.indexOf(':');
                    System.out.println("[Codex] No namespace — defaulted to minecraft: " + part);
                }

                String namespace = part.substring(0, colonIndex);
                String path = part.substring(colonIndex + 1);

                System.out.println("[Codex] Checking advancement: " + namespace + ":" + path);

                Identifier id;
                try {
                    id = Identifier.of(namespace, path);
                } catch (Exception e) {
                    System.err.println("[ERROR] Invalid advancement ID: " + part + " (" + e.getMessage() + ")");
                    values.add(false); // fail-safe: treat invalid as false
                    continue;
                }

                var advancement = player.server.getAdvancementLoader().get(id);
                boolean hasAdvancement = advancement != null && player.getAdvancementTracker().getProgress(advancement).isDone();

                System.out.println("[Codex] " + id + " advancement is " + (hasAdvancement ? "complete" : "incomplete"));

                if (inverted) {
                    hasAdvancement = !hasAdvancement;
                    System.out.println("[Codex] Inverted result: " + hasAdvancement);
                }

                values.add(hasAdvancement);
            }

            System.out.println("[Codex] Values collected: " + values);
            System.out.println("[Codex] Operators collected: " + operators);

            // Now combine the results according to the operators
            while (values.size() > 1) {
                String op = operators.remove(0);
                boolean left = values.remove(0);
                boolean right = values.remove(0);

                boolean result;
                if (op.equals("*")) {
                    result = left && right;
                    System.out.println("[Codex] Evaluating: " + left + " AND " + right + " = " + result);
                } else if (op.equals("+")) {
                    result = left || right;
                    System.out.println("[Codex] Evaluating: " + left + " OR " + right + " = " + result);
                } else {
                    throw new IllegalStateException("[Codex] Unknown operator: " + op);
                }

                // Put result back at the front of the list
                values.add(0, result);
                System.out.println("[Codex] Intermediate values: " + values);
            }

            System.out.println("[Codex] Final requirement result: " + values.get(0));
            return values.get(0);
        }


        public static MutableText concat(final XMLNodeCollection<?> nodes) {
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
                    System.err.println("§c[ERROR] Invalid advancement ID '" + requires + "': missing namespace. Defaulting to 'minecraft'");
                    if (requires.startsWith("!")) {
                        return Identifier.of("minecraft", requires.substring(1));
                    } else {
                        return Identifier.of("minecraft", requires);
                    }
                }
                String namespace;
                if (requires.startsWith("!")) {
                    isInvertedAdvancementText = true;
                    namespace = requires.substring(1, colonIndex);
                } else {
                    isInvertedAdvancementText = false;
                    namespace = requires.substring(0, colonIndex);
                }
                String path = requires.substring(colonIndex + 1);

                System.out.println("Namespace detected: " + namespace);
                System.out.println("Path detected: " + path);
                System.out.println("Is inverted: " + isInvertedAdvancementText());

                return Identifier.of(namespace, path);
            } else {
                return null;
            }
        }

        public static boolean isInvertedAdvancementText() {
            return isInvertedAdvancementText;
        }

        @Override
        public net.minecraft.text.Text toText() {
            return concat(this.children);
        }

        @XML.Name("a")
        public record LinkText(
                @XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children,

                @XML.Attribute(fallBack = "") String open,
                @XML.Attribute(fallBack = "") String run,
                @XML.Attribute(fallBack = "") String suggest,
                @XML.Attribute(fallBack = "") String page,
                @XML.Attribute(fallBack = "") String copy,
                @XML.Attribute(fallBack = "blue") String color
        ) implements TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                MutableText linkText = Codex.Text.concat(this.children);

                ClickEvent event;
                String hoverText;

                if (!open.isEmpty()) {
                    event = new ClickEvent(ClickEvent.Action.OPEN_URL, open);
                    hoverText = "Open link";
                } else if (!run.isEmpty()) {
                    event = new ClickEvent(ClickEvent.Action.RUN_COMMAND, run);
                    hoverText = "Run command";
                } else if (!suggest.isEmpty()) {
                    event = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest);
                    hoverText = "Suggest command";
                } else if (!page.isEmpty()) {
                    event = new ClickEvent(ClickEvent.Action.CHANGE_PAGE, page);
                    hoverText = "Go to page";
                } else if (!copy.isEmpty() && copy.startsWith("test:")) {
                    try {
                        ClickEvent.Action action = ClickEvent.Action.valueOf("TEST_COMMAND");
                        event = new ClickEvent(action, copy.substring(5).strip());
                        System.out.println("Thinger: " + action + " Thinger Value: " + copy.substring(5));
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Custom ClickEvent.Action 'TEST_COMMAND' not found.");
                        event = null;
                    }
                    hoverText = "Copy text";
                } else if (!copy.isEmpty()) {
                    event = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy);
                    hoverText = "Copy text";
                } else {
                    event = null;
                    hoverText = null;
                }
                // Apply link-specific style to the whole text chain — preserving child formatting
                ClickEvent finalEvent = event;
                linkText = linkText.copy().styled(style -> style
                        .withColor(Formatting.byName(color.toUpperCase()))
                        .withUnderline(true)
                        .withClickEvent(finalEvent)
                        .withHoverEvent(hoverText != null ? new HoverEvent(HoverEvent.Action.SHOW_TEXT, net.minecraft.text.Text.literal(hoverText)) : null)
                );

                return linkText;
            }
        }

        @XML.Name("h")
        public record HoverText(
                @XML.Children(allow = {XMLTextNode.class, Codex.TextContent.class}) XMLNodeCollection<?> children,
                // Attributes for hover actions
                @XML.Attribute(fallBack = "") String text,
                @XML.Attribute(fallBack = "") String item,
                @XML.Attribute(fallBack = "") String entityType,
                @XML.Attribute(fallBack = "") String entityUUID,
                @XML.Attribute(fallBack = "") String name
        ) implements Codex.TextContent {
            @Override
            public net.minecraft.text.Text toText() {
                MutableText baseText = Codex.Text.concat(this.children);

                HoverEvent hoverEvent;

                if (!text.isEmpty()) {
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, net.minecraft.text.Text.literal(text));

                } else if (!item.isEmpty()) {
                    var itemObj = Registries.ITEM.get(Identifier.of(item));
                    if (itemObj != null) {
                        hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(itemObj.getDefaultStack()));
                    } else {
                        hoverEvent = null;
                        System.err.println("[Codex] Invalid item ID for <h item=\"" + item + "\">");
                    }

                } else if (!entityUUID.isEmpty()) {
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(entityUUID);
                    } catch (IllegalArgumentException e) {
                        System.err.println("[Codex] Invalid UUID for <h entityUUID=\"" + entityUUID + "\">");
                        uuid = null;
                    }

                    if (uuid != null) {
                        EntityType<?> type = null;
                        if (!entityType.isEmpty()) {
                            type = Registries.ENTITY_TYPE.get(Identifier.of(entityType));
                            if (type == null) {
                                System.err.println("[Codex] Invalid entity type for <h entityType=\"" + entityType + "\">");
                            }
                        }

                        MutableText displayName = name.isEmpty() ? null : net.minecraft.text.Text.literal(name);
                        hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(type, uuid, displayName));
                    } else {
                        hoverEvent = null;
                    }
                } else {
                    hoverEvent = null;
                }

                if (hoverEvent != null) {
                    baseText = baseText.styled(style -> style.withHoverEvent(hoverEvent));
                }

                return baseText;
            }
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
