package com.github.basking2.sdsai.marchinesquares;

/**
 * A side is one cell value and optionally one or two points.
 */
public class Side {
    public byte cell;
    public LinkedList.Node<Point> point1;
    public LinkedList.Node<Point> point2;

    /**
     * Constructor.
     *
     * @param cell A cell value.
     * @param point1 A point of a line.
     * @param point2 A point of a line.
     */
    public Side(final byte cell, final byte cell2, final LinkedList.Node<Point> point1, final LinkedList.Node<Point> point2) {
        this.cell = cell;
        this.point1 = point1;
        this.point2 = point2;
    }

    /**
     * Constructor.
     *
     * @param cell A cell value.
     * @param point1 A point of a line.
     */
    public Side(final byte cell, final LinkedList.Node<Point> point1) {
        this.cell = cell;
        this.point1 = point1;
        this.point2 = null;
    }

    /**
     * Constructor.
     *
     * @param cell A cell value.
     */
    public Side(final byte cell) {
        this.cell = cell;
        this.point1 = null;
        this.point2 = null;
    }
}
