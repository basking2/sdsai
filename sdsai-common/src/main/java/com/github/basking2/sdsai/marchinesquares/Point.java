package com.github.basking2.sdsai.marchinesquares;

public class Point {
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

    public static LinkedList.LabeledNode<Point> buildPointLineNode(double x, double y, byte side) {
        switch (side) {
            case 0:
                return new LinkedList.LabeledNode(new Point(x + 0.5, y, side), null);
            case 1:
                return new LinkedList.LabeledNode(new Point(x + 1.0, y+0.5, side), null);
            case 2:
                return new LinkedList.LabeledNode(new Point(x + 0.5, y+1.0, side), null);
            case 3:
                return new LinkedList.LabeledNode(new Point(x, y+0.5, side), null);
            default:
                throw new IllegalStateException("Side value must be 0, 1, 2, or 3 for nw, ne, se, or sw.");
        }
    }

}
