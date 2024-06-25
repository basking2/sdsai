package com.github.basking2.sdsai.micrometer;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TimeEventMovingAverageTest {
    @Test
    public void testMovingAverage() {
        // 10% over 500 milliseconds.
        final TimeEventMovingAverage avg1 = new TimeEventMovingAverage(0.1, Duration.ofMillis(500));

        // 100% over 100 milliseconds.
        final TimeEventMovingAverage avg2 = new TimeEventMovingAverage(1.0, Duration.ofMillis(100));


        for (
                long start = System.currentTimeMillis(), t = start;
                t - start < 1000;
                t = System.currentTimeMillis()
        ) {
            final long v = (long)(Math.random() * 1000D);
            avg1.update(v);
            avg2.update(v);
        }

        assertEquals(500D, avg1.doubleValue(), 50D);
        assertEquals(500D, avg2.doubleValue(), 50D);
        assertEquals(avg1.doubleValue(), avg2.doubleValue(), 50D);
    }

}