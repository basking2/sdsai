package com.github.basking2.sdsai.micrometer;

import java.time.Duration;

/**
 * This is a weighted moving average designed to average millisecond time durations.
 *
 * This is intended to feed Micrometer metrics.
 *
 * To lighten the work done, a sampling rate is used.
 *
 * The convergence time is the time duration, in milliseconds, over which events will be counted.
 * The count of events is used as the event rate.
 * The event rate is used to combine a new value with the existing value.
 * The exact formula is ((event_rate-1)/event_rate * current_value) + (1 / event_rate * new_value).
 *
 * So, if 10 events are observed during our convergence time, then after we observe 10 events we will have
 * converged the current value on the average of those 10 events.
 *
 * There is a bias to use more events, and thus slow convergence. This decision was made mostly for simpler code
 * and to have a non-zero value for event_rate as the first convergence period of events was measured.
 *
 */
public class TimeEventMovingAverage extends Number {

    private final double samplingRate;
    private final long convergenceTime;
    private long convergenceStartTime = System.currentTimeMillis();
    private double value = 0L;
    private long eventRate = 0;
    private long eventCount = 0;

    /**
     * Constructor.
     * @param samplingRate The percentage of events that will be used to measure. 1 is 100%, 0.5 is 50%, etc.
     * @param convergenceTime The duration (resolution in milliseconds) over which events will be counted.
     */
    public TimeEventMovingAverage(final double samplingRate, final Duration convergenceTime) {
        this.convergenceTime = convergenceTime.toMillis();
        this.samplingRate = samplingRate;
    }

    public TimeEventMovingAverage() {
        this(0.1, Duration.ofMillis(60000));
    }

    /**
     * Used to update the value.
     * @param duration The value to update.
     */
    public void update(final double duration) {
        // Apply the sampling rate. Skip values that don't "score" below our sampling rate.
        if (samplingRate < Math.random()) {
            return;
        }

        // System time call. Do this outside of the synchronized block.
        final long now = System.currentTimeMillis();

        synchronized (this) {

            // If the convergence time window has elapsed, commit the current observed event rate.
            if (now - convergenceStartTime > convergenceTime) {
                // The next convergence windows start time is "now."
                convergenceStartTime = now;
                // Set the event rate to the count of seen events minus the one that is outside the convergence time.
                eventRate = eventCount-1;

                // Event count is set to 1.
                // This is important, we do *not* set it to 0.
                // First, the event that triggers the window shift happens outside the window, and so is counted as part
                // of the next window to count events across.
                // Second, if events happen so infrequently that no two events ever happen in the same convergence window,
                // we do not want to set eventRate (the line above) to 0, we want to set it to at *least* 1.
                eventCount = 1;
            } else {

                // Normal case. We are measuring events in the current event window.
                eventCount++;

                // If we ever run at a rate that is faster than our current rate, eagerly adjust the eventRate up.
                // We do this to handle init situations. During our first convergence window we will want a non-zero
                // eventRate so the average can be calculated as we build the first event window count of events.
                if (eventCount > eventRate) {
                    eventRate = eventCount;
                }
            }

            // After all that event counting logic, combine the during and value using the eventRate.
            // The duration contributes 1/eventRate of its value to the combined value.
            // The value contributes (eventRate-1)/eventRate of its value to the combined value.
            value = combine(duration, value, eventRate);
        }
    }

    private double combine(double newValue, double oldValue, double count) {
        return (newValue * (1D /count)) + (oldValue * ((count-1D) / count));
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return (double) value;
    }
}
