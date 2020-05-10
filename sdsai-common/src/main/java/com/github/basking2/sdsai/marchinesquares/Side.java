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

    public static class ArtificialSide extends Side {
        public String msg;
        public ArtificialSide(final String msg) {
            super(BOGUS_VALUE);
            this.msg = msg;
        }

        @Override
        public void swapPoints() {
            super.swapPoints();
            msg = msg +" flipped";
        }

        @Override
        public String toString(){
            return super.toString() + " msg:"+msg;
        }
    }

    public void setPoints(final double x, final double y, final byte side, final byte color, final byte left, final byte right) {
        switch (left) {
            case -1:
                switch (right) {
                    case -1:
                        // NOP: No points for contours on this side.
                        break;
                    case 0:
                        // A begin point that should attach to an end point in this cell.
                        beginPoint = new LinkedList.Node<>(new Point(x, y, side), null, color);
                        break;
                    case 1:
                        // Two points.
                        beginPoint = new LinkedList.Node<>(new Point(x, y, side), null, color);
                        endPoint = new LinkedList.Node<>(new Point(x, y, side), null, color);
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled byte "+right);
                }
                break;
            case 0:
                switch (right) {
                    case -1:
                        // A end point that should attach to a begin point outside this cell.
                        endPoint = new LinkedList.Node<>(new Point(x, y, side), null, color);
                        break;
                    case 0:
                        // NOP: No points for contours on this side.
                        break;
                    case 1:
                        // A end point that should attach to a begin point outside this cell.
                        endPoint = new LinkedList.Node<>(new Point(x, y, side), null, color);
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled byte "+right);
                }
                break;
            case 1:
                switch (right) {
                    case -1:
                        // Two points.
                        beginPoint = new LinkedList.Node<>(new Point(x, y, side), null, color);
                        endPoint = new LinkedList.Node<>(new Point(x, y, side), null, color);
                        break;
                    case 0:
                        // A begin point that should attach to an end point in this cell.
                        beginPoint = new LinkedList.Node<>(new Point(x, y, side), null, color);
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
    }

    /**
     * Build an artificial side with the correct number of points.
     * @param x The x value to create points at.
     * @param y The y value to create points at.
     * @param side The side of a cell that this lies on. The values are
     *             0 is the top, 1 is the right, 2 is the bottom, and 3 is the left.
     * @param color The color to set the linked list nodes to.
     * @param left The value of the point that is on the left of the side, the side being viewed
     *             from the center of the cell. The counter-clockwise most point.
     * @param right The value of the point that is on the right of the side, the side being viewed
     *              from the center of the cell. The clockwise most point.
     * @return A built artificial side.
     */
    public static Side buildArtificialSide(final double x, final double y, final byte side, final byte color, final byte left, final byte right) {
        final Side s = new ArtificialSide("LEFT RIGHT SIDE "+left+":"+right+":"+side);

        s.setPoints(x, y, side, color, left, right);

        return s;
    }

    /**
     * Call {@link #buildArtificialSide(double, double, byte, byte, byte, byte)} with reflected inputs.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param side The side. This is reflected 0 to 2, 2 to 0, 1 to 3, and 3 to 1.
     * @param color The color to set the linked list nodes to.
     * @param left This is reflected by swapping with right.
     * @param right This is reflected by swapping with left.
     * @return The reflected side.
     */
    public static Side buildReflectedArtificialSide(final double x, final double y, final byte side, final byte color, final byte left, final byte right) {
        final byte reflectedSide;
        switch (side) {
            case 0:
                reflectedSide = 2;
                break;
            case 1:
                reflectedSide = 3;
                break;
            case 2:
                reflectedSide = 0;
                break;
            case 3:
                reflectedSide = 1;
                break;
            default:
                throw new IllegalStateException("Unhandled side "+side);
        }
        return buildArtificialSide(x, y, reflectedSide, color, right, left);
    }

    public void swapPoints() {
        final LinkedList.Node<Point> swap = beginPoint;
        beginPoint = endPoint;
        endPoint = swap;
    }
}
