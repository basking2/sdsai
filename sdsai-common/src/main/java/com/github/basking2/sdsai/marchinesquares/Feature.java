package com.github.basking2.sdsai.marchinesquares;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A feature is a map of strings to strings and an orderd list of points.
 *
 * The list of points is directed, with the first point being the start
 * and the last point being the end. If the feature is a polygon, then
 * the first and last point are the same values.
 */
public class Feature {

    /**
     * Properties.
     */
    public final Map<String, Object> properties;

    /**
     * A list of all points that make up this feature.
     * If the first and last point values are the same values,
     * then the feature is a polygon.
     */
    public final LinkedList.Node<Point> points;

    public final LinkedList<LinkedList.Node<Point>> holes;

    public Feature(final LinkedList.Node<Point> points) {
        this.points = points;
        this.properties = new HashMap<>();
        this.holes = new LinkedList();
    }

    /**
     * Add the given x and y offset to this point, translating it.
     *
     * @param xOffset The distance to move x.
     * @param yOffset The distance to move y.
     */
    public void translate(final double xOffset, final double yOffset) {
        for (final Point point: points) {
            point.x += xOffset;
            point.y += yOffset;
        }
    }

    /**
     * O(n) test if the polygon represented by this feature is wound counter-clockwise.
     *
     * Counter-clockwise polygons are external. Clockwise wound polygons must be
     * in a {@link Feature} as holes.
     *
     * @return
     */
    public boolean isCounterClockwise() {
        final Iterator<Point> itr = points.iterator();

        double sum = 0;

        if (itr.hasNext()) {
            Point p1 = itr.next();

            while (itr.hasNext()) {
                final Point p2 = itr.next();
                sum += (p2.x - p1.x) * (p2.y + p1.y);
                p1 = p2;
            }
        }

        // Negative is counter clockwise, positive is clockwise.
        // We give the tie (0) to counter clockwise.
        return sum <= 0;
    }
}
