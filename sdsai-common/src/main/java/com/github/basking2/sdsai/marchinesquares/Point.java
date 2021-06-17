package com.github.basking2.sdsai.marchinesquares;

import java.util.Iterator;

public class Point {
    /**
     * A point exists on a side of a square between two corner values.
     *
     * If a Point's side field is set to TOP then the point is between the two top corners.
     */
    public static final byte TOP = 0;

    /**
     * A point exists on a side of a square between two corner values.
     *
     * If a Point's side field is set to RIGHT then the point is between the two right corners.
     */
    public static final byte RIGHT = 1;

    /**
     * A point exists on a side of a square between two corner values.
     *
     * If a Point's side field is set to BOTTOM then the point is between the two bottom corners.
     */
    public static final byte BOTTOM = 2;

    /**
     * A point exists on a side of a square between two corner values.
     *
     * If a Point's side field is set to LEFT then the point is between the two left corners.
     */
    public static final byte LEFT = 3;

    public double x;
    public double y;
    public byte side;

    /**
     * Make a new point along the edge of a cell.
     *
     * The x and y values should be considered to be in the center of the cell wall they address.
     * That is to say, a point located at (0, 0) along the top edge of a cell could be considered to
     * actually exist at (0.5, 0). To avoid floating point numbers we simply shift everything left and up.
     *
     * @param x The x location of the point.
     * @param y The y location of the point.
     * @param side The side (0-3) that the point is on.
     */
    public Point(final double x, final double y, final byte side) {
        this.x = x;
        this.y = y;
        this.side = side;
    }

    /**
     * Copy constructor.
     * @param p Point.
     */
    public Point(final Point p) {
        this.x = p.x;
        this.y = p.y;
        this.side = p.side;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)-%d", x, y, side);
    }

    /**
     * Build a point that is located on the side of a cell relative to the upper-left point.
     * The point created will be offset (+.5, +0), (+1, +.5), (+.5, +1), or (+0, +.5) depending on
     * if it is on the top, right, bottom, or left side of the cell.
     *
     * @param x The position of the left of the cell in the grid.
     * @param y The position of the top of the cell in the grid.
     * @param side The side of the cell that the point is located on.
     * @return An unlinked {@link LinkedList.Node} continaing a point.
     */
    public static LinkedList.Node<Point> buildPointLineNode(double x, double y, byte side) {
        switch (side) {
            case TOP:
                return new LinkedList.Node(new Point(x + 0.5, y, side), null);
            case RIGHT:
                return new LinkedList.Node(new Point(x + 1.0, y+0.5, side), null);
            case BOTTOM:
                return new LinkedList.Node(new Point(x + 0.5, y+1.0, side), null);
            case LEFT:
                return new LinkedList.Node(new Point(x, y+0.5, side), null);
            default:
                throw new IllegalStateException("Side value must be 0, 1, 2, or 3 for nw, ne, se, or sw.");
        }
    }

    /**
     * Determine if a polygon laid out in Raster order is wound counter clockwise or not.
     *
     * NOTE: The first point must be repeated as the last point to consider the last edge of the polygon.
     * This is normal for GeoJSON polygons.
     *
     * The Raster order is important because as the Y value increases the point is lower
     * on a rendered image. This flips the sign of the internal math. If you use this for
     * points on a normal cartesian plane, you will have to flip the sign of the y values
     * or invert the answer this method gives.
     *
     * @param points The list of points to consider.
     * @return True of the points represent a polygon on a Raster grid make a polygon that winds counter clockwise.
     */
    public static boolean isCounterClockwise(final Iterator<Point> points) {
        double sum = 0;

        if (points.hasNext()) {
            Point p1 = points.next();

            while (points.hasNext()) {
                final Point p2 = points.next();
                // NOTE: This is classically sum+=, but we switch to sum-=
                // because in this layout the y values are in the 4th quadrant.
                sum -= (p2.x - p1.x) * (p2.y + p1.y);
                p1 = p2;
            }
        }

        // Negative is counter clockwise, positive is clockwise.
        // We give the tie (0) to counter clockwise.
        return sum <= 0;
    }

    /**
     * Consider if the point is inside the polygon represented by the iteration of points.
     *
     * The first point in the iteration must be repeated as the last element in the interation so
     * all sides of the polygone are considered.
     *
     * This is computed by checking how often a ray from the point travelling along the X axis
     * crosses sides to its left.
     *
     * @param p The point to consider.
     * @param points The iteration of points.
     * @return true if the given point is located inside the polygon.
     */
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
                // If the line starts above us and terminates above or at us, do not consider it.
                !(startp.y > p.y && stopp.y >= p.y) &&

                // If the line starts below us and terminates below or at us, do not consider it.
                !(startp.y < p.y && stopp.y <= p.y)
            ) {
                final double sx = startp.x + (stopp.x - startp.x) * ((p.y - startp.y) / (stopp.y - startp.y));

                // If we intersect, flip the isInside value.
                if (p.x > sx) {
                    isInside = !isInside;
                }
            }
        }

        return isInside;
    }
}

