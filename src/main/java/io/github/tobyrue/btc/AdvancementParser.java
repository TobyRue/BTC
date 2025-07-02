package io.github.tobyrue.btc;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancementParser {
    public interface Expression {
        boolean evaluate(ServerPlayerEntity player);
    }
    private record AdvancementExpression(Identifier advancement) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return player.getAdvancementTracker().getProgress(player.server.getAdvancementLoader().get(advancement)).isDone();
        }

        @Override
        public String toString() {
            return String.format("Advancement[%s]", advancement);
        }
    }
    private record NotExpression(Expression e) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return !e.evaluate(player);
        }
        @Override
        public String toString() {
            return String.format("Not[%s]", e);
        }
    }
    private record AndExpression(Expression lhs, Expression rhs) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return lhs.evaluate(player) && rhs.evaluate(player);
        }
        @Override
        public String toString() {
            return String.format("And[%s, %s]", lhs, rhs);
        }
    }
    private record OrExpression(Expression lhs, Expression rhs) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return lhs.evaluate(player) || rhs.evaluate(player);
        }
        @Override
        public String toString() {
            return String.format("Or[%s, %s]", lhs, rhs);
        }
    }
    private record LiteralExpression(boolean b) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return b;
        }
        @Override
        public String toString() {
            return String.format("Literal[%s]", b);
        }
    }
    @FunctionalInterface
    private interface ReplaceCallback {
        String apply(Matcher m);
    }

    private static String replace(final String input, final Pattern regex, final ReplaceCallback callback) {
        final StringBuffer s = new StringBuffer();
        final Matcher m = regex.matcher(input);
        while (m.find()) m.appendReplacement(s, callback.apply(m));
        m.appendTail(s);

        return s.toString();
    }

    public static Expression parse(String text) throws Exception {
        if (text.contains("$")) {
            throw new Exception("Expression can not contain '$'");
        }
        return parse(text.toLowerCase().replace("\\s+", ""), new ArrayList<>());
    }
    private static final Pattern GROUP_PATTERN = Pattern.compile("\\((?<contents>[^(]*?)\\)");


    public static Expression parse(String text, ArrayList<Expression> groups)  {
        var last = text;
        while (!last.equals(text = replace(text, GROUP_PATTERN, t -> {
            groups.add(parse(t.group("contents"), groups));
            return String.format("\\$%d", groups.size() - 1);
        }))) {
            last = text;
        }
        Expression d1 = new LiteralExpression(false);
        for (var p1 : text.split("\\+|\\|{1,2}")) {
            Expression d2 = new LiteralExpression(true);
            for (var p2 : p1.split("\\*|&{1,2}")) {
                if (p2.startsWith("!") || p2.startsWith("~")) {
                    d2 = new AndExpression(d2, new NotExpression(parseLiteral(p2.substring(1), groups)));
                } else {
                    d2 = new AndExpression(d2, parseLiteral(p2, groups));
                }
            }
            d1 = new OrExpression(d1, d2);
        }

        return d1;
    }
    private static Expression parseLiteral(final String text, final ArrayList<Expression> groups) {
        System.out.println(text);
        if (text.startsWith("$")) {
            return groups.get(Integer.parseInt(text.substring(1)));
        }
        return switch (text) {
            case "true" -> new LiteralExpression(true);
            case "false" -> new LiteralExpression(false);
            default -> new AdvancementExpression(parseIdentifier(text));
        };
    }
    private static Identifier parseIdentifier(final String text) {
        var parts = text.split(":", 2);
        if (parts.length == 2) {
            return Identifier.of(parts[0], parts[1]);
        } else {
            return Identifier.of(text);
        }
    }
}
