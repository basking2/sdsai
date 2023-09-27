package com.github.basking2.sdsai.util;

/**
 * This class maintains a running count of samples since a start time and computes how long to delay (or accelerate)
 * to reach a sample rate.
 *
 * As the count of samples increases, the sensitivity of this class to changes in sample rate decreases as they
 * are averaged over a larger period of time. To recover sensitivity to rate changes, this class allows
 * for pruning the time window to cover a specific count of samples.
 */
public class AverageRateLimiter {

    /**
     * Count of samples seen. The rate is computed over this value.
     */
    double samples;

    /**
     * Start time of the first sample.
     */
    double startTime;

    /**
     * Target rate in requests per millisecond.
     */
    private final double targetRate;

    /**
     * The target number of samples to compute the rate over.
     */
    private final double targetSamples;

    /**
     * Constructor.
     * @param targetSamples The number of samples the target rate is computed by.
     * @param per Divide the targetSamples by this number of milliseconds to get the target rate.
     */
    public AverageRateLimiter(final double targetSamples, final long per) {
        this.startTime = System.currentTimeMillis();
        this.samples = 0d;
        this.targetSamples = targetSamples;
        // Convert to requests per millisecond.
        this.targetRate = targetSamples / ((double)per);
    }

    /**
     * Add 1 to the current count of samples and compute how time should be adjusted to achieve the target rate.
     *
     * The {@code per} value used is in milliseconds. This will return a positive
     * value representing the number of milliseconds that the caller should wait before issuing the next sample to
     * achieve the target rate.
     *
     * If the returned value is negative, then the current rate is below the target and the caller may call faster.
     *
     * @return The number of time units to delay or accelerate to achieve the target rate.
     */
    public long adjust() {
        final long nowMillis = System.currentTimeMillis();
        samples += 1d;
        final double delta_t = (nowMillis - startTime);
        final double rateAdjust = samples / targetRate - delta_t;

        return (long)rateAdjust;
    }

    /**
     * Call {@link #setSamples(double)} with the target samples this instances was constructed with.
     */
    public void setSamples() {
        setSamples(targetSamples);
    }

    /**
     * Set the current count of samples to this value and adjust the start time to cover those samples at the current rate.
     * @param samples Set the current count of samples to this value and adjust the start time to cover those samples at the current rate.
     */
    public void setSamples(double samples) {
        final long nowMillis = System.currentTimeMillis();
        final double delta_t = (nowMillis - startTime);
        final double currentRate = this.samples / delta_t;
        this.startTime = nowMillis - samples / currentRate;
        this.samples = samples;
    }
}
