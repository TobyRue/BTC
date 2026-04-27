package io.github.tobyrue.btc.util;

public class TickCounter {
    private float currentValue;
    private final float startValue;
    private final float endValue;
    private final int totalTicks;
    private int elapsedTicks = 0;

    public TickCounter(float start, float end, int durationTicks) {
        this.startValue = start;
        this.endValue = end;
        this.totalTicks = durationTicks;
        this.currentValue = start;
    }

    /**
     * Call this every tick.
     * @return true if the counter has reached the end value.
     */
    public boolean tick() {
        if (elapsedTicks < totalTicks) {
            elapsedTicks++;

            float progress = (float) elapsedTicks / totalTicks;

            currentValue = startValue + (endValue - startValue) * progress;
        }
        return elapsedTicks >= totalTicks;
    }

    public float getValue() {
        return currentValue;
    }
}