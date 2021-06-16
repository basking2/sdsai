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
        return Point.isCounterClockwise(points.iterator());
    }

    /**
     * Check if the point is contained in the polygon that makes out the outside of this Feature.
     *
     * If checkHoles is true, then the extra constraint that the point may not appear in any hole must
     * also be true.
     *
     * @param p The point to check.
     * @param checkHoles If true, then the point, p, must not be contained in any hole.
     * @return True if point p is contained in this feature.
     * If checkHoles is set, then the point must also not be contained in any of the holes of this polygon.
     */
    public boolean containsPoint(final Point p, final boolean checkHoles) {
        if (!contains(p, this.points.iterator())) {
            return false;
        }

        if (!checkHoles) {
            // True if the outer shell contains the point and we are not checking holes.
            return true;
        }

        // If we get here, we must check the holes.
        for (final LinkedList.Node<Point> points : this.holes) {
            if (contains(p, points.iterator())) {
                // False if a hole contains the point.
                return false;
            }
        }

        // True if the outer polygon contains the point but no hole did.
        return true;
    }

    public static boolean contains(final Point p, final Iterator<Point> points) {
        boolean isInside = false;

        if (!points.hasNext()) {
            return false;
        }
        Point stopp = points.next();

        while (points.hasNext()) {
            final Point startp = stopp;
            stopp = points.next();

            if (
                    // Line is note above us. We might intersect along the X axis.
                    !(startp.y > p.y && stopp.y > p.y) &&

                    // Line is not below us. We might intersect along the X axis.
                    !(startp.y < p.y && stopp.y < p.y)
            ) {
                final double sx = startp.x + (startp.x - stopp.x) * ((p.y - stopp.y) / (startp.y - stopp.y));

                // If we intersect, flip the isInside value.
                if (p.x > sx) {
                    isInside = !isInside;
                }
            }
        }

        return isInside;
    }


}
