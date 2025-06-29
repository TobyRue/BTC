package io.github.tobyrue.btc;

import io.github.tobyrue.xml.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
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
        boolean isRequirementMet(ServerPlayerEntity player);
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

    public record Page(@XML.Children(allow = {Line.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "") String requires) implements ConditionalNode, XMLNode, Render  {
        @Override public String getRequires() { return requires; }
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


        //TODO MAKE isRequirementMet work for all
        public boolean isRequirementMet(ServerPlayerEntity player) {
            String reqStr = getRequires();
            System.out.println("Raw requires string: " + reqStr);
            for (int i = 0; i < reqStr.length(); i++) {
                System.out.println(i + ": '" + reqStr.charAt(i) + "' (code " + (int)reqStr.charAt(i) + ")");
            }
            if (reqStr == null || reqStr.isEmpty()) {
                // No requirement means always true
                return true;
            }

            // Split by OR groups ("|")
            String[] orGroups = reqStr.split("\\|");
            for (String orGroup : orGroups) {
                boolean allAndPassed = true;

                // Split each OR group into AND conditions ("&")
                String[] andConditions = orGroup.split("&");
                for (String cond : andConditions) {
                    cond = cond.trim();
                    if (cond.isEmpty()) continue;

                    // Support inversion with "!" prefix
                    boolean inverted = cond.startsWith("!");
                    String idString = inverted ? cond.substring(1) : cond;

                    // Ensure namespace present, default to minecraft if missing
                    int colonIndex = idString.indexOf(':');
                    if (colonIndex == -1) {
                        idString = "minecraft:" + idString;
                        colonIndex = idString.indexOf(':');
                    }

                    String namespace = idString.substring(0, colonIndex);
                    String path = idString.substring(colonIndex + 1);

                    Identifier id;
                    try {
                        id = Identifier.of(namespace, path);
                    } catch (Exception e) {
                        System.err.println("[ERROR] Invalid advancement ID: " + idString + " (" + e.getMessage() + ")");
                        allAndPassed = false;
                        break;
                    }

                    var advancement = player.server.getAdvancementLoader().get(id);
                    boolean hasAdvancement = advancement != null &&
                            player.getAdvancementTracker().getProgress(advancement).isDone();

                    if (inverted) hasAdvancement = !hasAdvancement;

                    if (!hasAdvancement) {
                        allAndPassed = false;
                        break; // AND condition failed, no need to check further
                    }
                }

                if (allAndPassed) {
                    // One OR group fully satisfied means requirement met
                    return true;
                }
            }

            // No OR group satisfied
            return false;
        }


        public static boolean isInvertedAdvancementPage() {
            return isInvertedAdvancementPage;
        }

        @Override
        public void render(DrawContext context, int width, int height, float delta) {
            //TODO
//            context.drawText()
        }

        public record Line(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "") String requires) implements XMLNode, TextContent, ConditionalNode {
            @Override public String getRequires() { return requires; }
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
            public boolean isRequirementMet(ServerPlayerEntity player) {
                String reqStr = getRequires();
                if (reqStr == null || reqStr.isEmpty()) {
                    // No requirement means always true
                    return true;
                }

                // Split by OR groups ("|")
                String[] orGroups = reqStr.split("\\|");
                for (String orGroup : orGroups) {
                    boolean allAndPassed = true;

                    // Split each OR group into AND conditions ("&")
                    String[] andConditions = orGroup.split("&");
                    for (String cond : andConditions) {
                        cond = cond.trim();
                        if (cond.isEmpty()) continue;

                        // Support inversion with "!" prefix
                        boolean inverted = cond.startsWith("!");
                        String idString = inverted ? cond.substring(1) : cond;

                        // Ensure namespace present, default to minecraft if missing
                        int colonIndex = idString.indexOf(':');
                        if (colonIndex == -1) {
                            idString = "minecraft:" + idString;
                            colonIndex = idString.indexOf(':');
                        }

                        String namespace = idString.substring(0, colonIndex);
                        String path = idString.substring(colonIndex + 1);

                        Identifier id;
                        try {
                            id = Identifier.of(namespace, path);
                        } catch (Exception e) {
                            System.err.println("[ERROR] Invalid advancement ID: " + idString + " (" + e.getMessage() + ")");
                            allAndPassed = false;
                            break;
                        }

                        var advancement = player.server.getAdvancementLoader().get(id);
                        boolean hasAdvancement = advancement != null &&
                                player.getAdvancementTracker().getProgress(advancement).isDone();

                        if (inverted) hasAdvancement = !hasAdvancement;

                        if (!hasAdvancement) {
                            allAndPassed = false;
                            break; // AND condition failed, no need to check further
                        }
                    }

                    if (allAndPassed) {
                        // One OR group fully satisfied means requirement met
                        return true;
                    }
                }

                // No OR group satisfied
                return false;
            }

        }
    }






    @XML.Root
    public record Text(@XML.Children(allow = {XMLTextNode.class, TextContent.class}) XMLNodeCollection<?> children, @XML.Attribute(fallBack = "") String requires) implements TextContent, ConditionalNode {
        static boolean isInvertedAdvancementText = false;
        @Override public String getRequires() { return requires; }

        public boolean isRequirementMet(ServerPlayerEntity player) {
            String reqStr = getRequires();
            if (reqStr == null || reqStr.isEmpty()) {
                // No requirement means always true
                return true;
            }

            // Split by OR groups ("|")
            String[] orGroups = reqStr.split("\\|");
            for (String orGroup : orGroups) {
                boolean allAndPassed = true;

                // Split each OR group into AND conditions ("&")
                String[] andConditions = orGroup.split("&");
                for (String cond : andConditions) {
                    cond = cond.trim();
                    if (cond.isEmpty()) continue;

                    // Support inversion with "!" prefix
                    boolean inverted = cond.startsWith("!");
                    String idString = inverted ? cond.substring(1) : cond;

                    // Ensure namespace present, default to minecraft if missing
                    int colonIndex = idString.indexOf(':');
                    if (colonIndex == -1) {
                        idString = "minecraft:" + idString;
                        colonIndex = idString.indexOf(':');
                    }

                    String namespace = idString.substring(0, colonIndex);
                    String path = idString.substring(colonIndex + 1);

                    Identifier id;
                    try {
                        id = Identifier.of(namespace, path);
                    } catch (Exception e) {
                        System.err.println("[ERROR] Invalid advancement ID: " + idString + " (" + e.getMessage() + ")");
                        allAndPassed = false;
                        break;
                    }

                    var advancement = player.server.getAdvancementLoader().get(id);
                    boolean hasAdvancement = advancement != null &&
                            player.getAdvancementTracker().getProgress(advancement).isDone();

                    if (inverted) hasAdvancement = !hasAdvancement;

                    if (!hasAdvancement) {
                        allAndPassed = false;
                        break; // AND condition failed, no need to check further
                    }
                }

                if (allAndPassed) {
                    // One OR group fully satisfied means requirement met
                    return true;
                }
            }

            // No OR group satisfied
            return false;
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
            getAdvancement();
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
