package com.github.basking2.sdsai.marchinesquares;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

public class FeatureTest {
    @Test
    public void testClockwise() {

        LinkedList.Node<Point> points = null;
        for (final Point p : Arrays.asList(
                new Point(0, 0, (byte)0),
                new Point(0, 1, (byte)0),
                new Point(1, 0, (byte)0),
                new Point(0, 0, (byte)0)
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
                new Point(0, 0, (byte)0),
                new Point(0, -1, (byte)0),
                new Point(1, 0, (byte)0),
                new Point(0, 0, (byte)0)
        )) {
            points = new LinkedList.Node<>(p, points);
        }

        points = points.reverse();

        final Feature f = new Feature(points);

        assertTrue(f.isCounterClockwise());
    }
}
