package com.github.basking2.sdsai.marchinesquares;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class FeatureTest {
    @Test
    public void testClockwise() {

        LinkedList.Node<Point> points = null;
        for (final Point p : Arrays.asList(
                new Point(0, 0, (byte) 0),
                new Point(0, 1, (byte) 0),
                new Point(1, 0, (byte) 0),
                new Point(0, 0, (byte) 0)
        )) {
            points = new LinkedList.Node<>(p, points);
        }

        points = points.reverse();

        final Feature f = new Feature(points);

        assertFalse(f.isCounterClockwise());
    }

    @Test
    public void testCounterClockwise() {

        LinkedList.Node<Point> points = null;
        for (final Point p : Arrays.asList(
                new Point(0, 0, (byte) 0),
                new Point(0, -1, (byte) 0),
                new Point(1, 0, (byte) 0),
                new Point(0, 0, (byte) 0)
        )) {
            points = new LinkedList.Node<>(p, points);
        }

        points = points.reverse();

        final Feature f = new Feature(points);

        assertTrue(f.isCounterClockwise());
    }

    @Test
    public void testContains1() {
        final Point p = new Point(0, 0, (byte) 0);

        assertFalse(
                Feature.contains(
                        p,
                        Arrays.asList(p).iterator()
                )
        );

    }

    @Test
    public void testContains2() {
        final Point p = new Point(0, 0, (byte) 0);

        assertTrue(
                Feature.contains(
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
                Feature.contains(
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
