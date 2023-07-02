/**
 * Copyright (c) 2020-2023 Sam Baskinger
 */

package com.github.basking2.sdsai.marchinesquares;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PointTest {
    @Test
    public void testContains1() {
        final Point p = new Point(0, 0, (byte) 0);

        assertFalse(
                Point.contains(
                        p,
                        Arrays.asList(p).iterator()
                )
        );

    }

    @Test
    public void testContains2() {
        final Point p = new Point(0, 0, (byte) 0);

        assertTrue(
                Point.contains(
                        p,
                        Arrays.asList(
                                new Point(0, 1, (byte) 0),
                                new Point(1, 0, (byte) 0),
                                new Point(0, -1, (byte) 0),
                                new Point(-1, 0, (byte) 0),
                                new Point(0, 1, (byte) 0)
                        ).iterator()
                )
        );
    }

    @Test
    public void testContains3() {
        final Point p = new Point(1, 1, (byte) 0);

        assertFalse(
                Point.contains(
                        p,
                        Arrays.asList(
                                new Point(0, 1, (byte) 0),
                                new Point(1, 0, (byte) 0),
                                new Point(0, -1, (byte) 0),
                                new Point(-1, 0, (byte) 0),
                                new Point(0, 1, (byte) 0)
                        ).iterator()
                )
        );
    }

    @Test
    public void testContains4() {
        final Point p = new Point(1.5, 2, (byte) 0);

        assertFalse(
                Point.contains(
                        p,
                        Arrays.asList(
                                new Point(0, 1, (byte) 0),
                                new Point(1, 0, (byte) 0),
                                new Point(0, -1, (byte) 0),
                                new Point(-1, 0, (byte) 0),
                                new Point(0, 1, (byte) 0)
                        ).iterator()
                )
        );
    }
}
