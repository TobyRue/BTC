package io.github.tobyrue.btc;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PredicateParser {
    private interface Expression {
        boolean evaluate(ServerPlayerEntity player);
    }
    private record AdvancementExpression(Identifier advancement) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return player.getAdvancementTracker().getProgress(player.server.getAdvancementLoader().get(advancement)).isDone();
        }
    }
    private record NotExpression(Expression e) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return !e.evaluate(player);
        }
    }
    private record AndExpression(Expression lhs, Expression rhs) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return lhs.evaluate(player) && rhs.evaluate(player);
        }
    }
    private record OrExpression(Expression lhs, Expression rhs) implements Expression {
        @Override
        public boolean evaluate(ServerPlayerEntity player) {
            return lhs.evaluate(player) || rhs.evaluate(player);
        }
    }
    public static Expression parse(String text) {

    }
}
