package com.github.basking2.sdsai.marchinesquares;

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

}
