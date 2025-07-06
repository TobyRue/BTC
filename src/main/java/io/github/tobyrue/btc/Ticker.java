package io.github.tobyrue.btc;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface Ticker {
    boolean tick();


    static Ticker forTicks(final Ticker ticker, final int t) {
        return new Ticker() {
            private int ticks = t;
            @Override
            public boolean tick() {
                return ticker.tick() || --this.ticks == 0;
            }
        };
    }

    static Ticker forTicks(final Runnable runnable, final int t) {
        return forTicks(of(runnable), t);
    }

    static Ticker forTicks(final Supplier<Boolean> supplier, final int t) {
        return forTicks(of(supplier), t);
    }

    static Ticker forTicks(final Consumer<Integer> consumer, final int t) {
        return forTicks(of(consumer), t);
    }

    static Ticker forTicks(final Function<Integer, Boolean> function, final int t) {
        return forTicks(of(function), t);
    }

    static Ticker of(final Runnable runnable) {
        return () -> {
            runnable.run();
            return false;
        };
    }

    static Ticker of(final Supplier<Boolean> supplier) {
        return () -> supplier.get();
    }

    static Ticker of(final Consumer<Integer> consumer) {
        return new Ticker() {
            private int ticks = 0;
            @Override
            public boolean tick() {
                consumer.accept(ticks++);
                return false;
            }
        };
    }

    static Ticker of(final Function<Integer, Boolean> function) {
        return new Ticker() {
            private int ticks = 0;
            @Override
            public boolean tick() {
                return function.apply(ticks++);
            }
        };
    }

    static Ticker forSeconds(final Ticker ticker, final int s) {
        return forTicks(ticker, s * 20);
    }

    static Ticker forSeconds(final Runnable runnable, final int s) {
        return forSeconds(of(runnable), s);
    }

    static Ticker forSeconds(final Supplier<Boolean> supplier, final int s) {
        return forSeconds(of(supplier), s);
    }

    static Ticker forSeconds(final Consumer<Integer> consumer, final int s) {
        return forSeconds(of(consumer), s);
    }

    static Ticker forSeconds(final Function<Integer, Boolean> function, final int s) {
        return forSeconds(of(function), s);
    }

    interface TickerTarget {
        void add(final Ticker ticker);

        default void add(final Runnable runnable) {
            this.add(of(runnable));
        }
        default void add(final Supplier<Boolean> supplier) {
            this.add(of(supplier));
        }
        default void add(final Consumer<Integer> consumer) {
            this.add(of(consumer));
        }
        default void add(final Function<Integer, Boolean> function) {
            this.add(of(function));
        }
    }
}
