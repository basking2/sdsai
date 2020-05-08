package com.github.basking2.sdsai.marchinesquares;

/**
 * A side is one cell value and optionally one or two points on the edge of a {@link Tile}.
 *
 * Points are assigned as either a begin or end point. That is, what role does that
 * point play in the contour of the cell of the tile the side is describing.
 */
public class Side {

    /**
     * The value in the data grid. This is -1, 0, or 1.
     * This is used to construct contours with neighboring tiles.
     */
    public byte cell;

    /**
     * A point that begins an contour. Its next field should be defined.
     *
     * The endPoint of one side should be linked to the beginPoint of another side.
     */
    public LinkedList.Node<Point> beginPoint;

    /**
     * A point that ends a contour. Its next field will be empty.
     *
     * The endPoint of one side should be linked to the beginPoint of another side.
     */
    public LinkedList.Node<Point> endPoint;

    /**
     * This is used to construct artificial sides as place holders.
     */
    public static byte BOGUS_VALUE = (byte)64;

    /**
     * Constructor.
     *
     * @param cell A cell value.
     * @param beginPoint A point of a line.
     * @param endPoint A point of a line.
     */
    public Side(final byte cell, final LinkedList.Node<Point> beginPoint, final LinkedList.Node<Point> endPoint) {
        this.cell = cell;
        this.beginPoint = beginPoint;
        this.endPoint = endPoint;
    }

    /**
     * Constructor.
     *
     * @param cell A cell value.
     */
    public Side(final byte cell) {
        this.cell = cell;
        this.beginPoint = null;
        this.endPoint = null;
    }

    @Override
    public String toString() {
        String s = "" + cell + " ";
        if (beginPoint != null) {
            s += "beginPoint "+beginPoint.value + " ";
        }
        if (endPoint != null) {
            s += "endPoint "+endPoint.value + " ";
        }

        return s;
    }

    /**
     * Build an artificial side with the correct number of points.
     * @param x The x value to create points at.
     * @param y The y value to create points at.
     * @param side The side of a cell that this lies on. The values are
     *             0 is the top, 1 is the right, 2 is the bottom, and 3 is the left.
     * @param left The value of the point that is on the left of the side, the side being viewed
     *             from the center of the cell. The counter-clockwise most point.
     * @param right The value of the point that is on the right of the side, the side being viewed
     *              from the center of the cell. The clockwise most point.
     * @return A built artificial side.
     */
    public static Side buildArtificialSide(final double x, final double y, final byte side, final byte left, final byte right) {
        final Side s = new Side(BOGUS_VALUE);

        switch (left) {
            case -1:
                switch (right) {
                    case -1:
                        // NOP: No points for contours on this side.
                        break;
                    case 0:
                        // A begin point that should attach to an end point in this cell.
                        s.beginPoint = new LinkedList.Node<>(new Point(x, y, side), null);
                        break;
                    case 1:
                        // Two points.
                        s.beginPoint = new LinkedList.Node<>(new Point(x, y, side), null);
                        s.endPoint = new LinkedList.Node<>(new Point(x, y, side), null);
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled byte "+right);
                }
                break;
            case 0:
                switch (right) {
                    case -1:
                        // A end point that should attach to a begin point outside this cell.
                        s.endPoint = new LinkedList.Node<>(new Point(x, y, side), null);
                        break;
                    case 0:
                        // NOP: No points for contours on this side.
                        break;
                    case 1:
                        // A end point that should attach to a begin point outside this cell.
                        s.endPoint = new LinkedList.Node<>(new Point(x, y, side), null);
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled byte "+right);
                }
                break;
            case 1:
                switch (right) {
                    case -1:
                        // Two points.
                        s.beginPoint = new LinkedList.Node<>(new Point(x, y, side), null);
                        s.endPoint = new LinkedList.Node<>(new Point(x, y, side), null);
                        break;
                    case 0:
                        // A begin point that should attach to an end point in this cell.
                        s.beginPoint = new LinkedList.Node<>(new Point(x, y, side), null);
                        break;
                    case 1:
                        // NOP: No points for contours on this side.
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled byte "+right);
                }
                break;
            default:
                throw new IllegalArgumentException("Unhandled byte "+left);

        }

        return s;
    }
}
