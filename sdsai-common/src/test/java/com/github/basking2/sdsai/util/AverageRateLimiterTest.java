/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AverageRateLimiterTest {
    @Test
    @Disabled("This takes a long time.")
    public void testRate() throws InterruptedException {
        final long target_samples = 5;
        final long time = 1000;
        final AverageRateLimiter arl = new AverageRateLimiter(target_samples, time);
        final long test_duration = 30000;
        final long start = System.currentTimeMillis();
        long count = 0;
        while (System.currentTimeMillis() - start < test_duration) {
            final long d = arl.adjust();
            count++;
            if (d > 0) {
                Thread.sleep(d);
            }

            if (count % 100 == 0) {
                arl.setSamples();
            }
        }

        assertTrue(Math.abs(150 - count) < 2, "Expected count 150 is too different from the actual count " + count);
    }

}