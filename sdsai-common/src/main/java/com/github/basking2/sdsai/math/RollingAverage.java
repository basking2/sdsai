/**
 * Copyright (c) 2018-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.math;

public class RollingAverage {
    private double values;
    private double average;

    public RollingAverage() {
        this(0, 0);
    }

    public RollingAverage(final double values, final double initial) {
        this.values = values;
        this.average = initial;
    }

    /**
     * Get the current average.
     * @return the current average.
     */
    public double getAverage() {
        return average;
    }

    /**
     * Set the current average without changing the values count.
     *
     * @param average To value to set the average to.
     */
    public void setAverage(final double average) {
        this.average = average;
    }

    /**
     * Get the current count of values.
     *
     * This is an integral number despite the type being a double.
     *
     * @return The current count of values.
     */
    public double getValues() {
        return values;
    }

    /**
     * Set the values value to the given number.
     * @param values The new values value.
     */
    public void setValues(final double values) {
        this.values = values;
    }

    /**
     * Update the average.
     *
     * @param update The update.
     * @return The current average.
     */
    public double update(final double update) {

        values++;

        average = (((values-1D) / values) * average) + ((1D / values) * update);

        return average;
    }

    /**
     * Set the number of values to 0. The next update will be 100% of the average.
     */
    public void reset() {
        values = 0;
    }

    /**
     * Reset this to 1 value of the given initial value.
     *
     * @param initial The value to reset this rolling average to.
     */
    public void reset(double initial) {
        values = 1;
        average = initial;
    }
}
