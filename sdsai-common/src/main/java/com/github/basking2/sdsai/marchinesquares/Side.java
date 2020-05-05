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
    public Side(final byte cell, final LinkedList.Node<Point> point1, final LinkedList.Node<Point> point2) {
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

    public void addPoint(final LinkedList.Node<Point> p) {
        if (point1 == null) {
            point1 = p;
        }
        else if (point2 == null) {
            point2 = p;
        }
        else {
            throw new IllegalArgumentException("Both point slots are filled in this side.");
        }
    }

    @Override
    public String toString() {
        String s = "" + cell + " ";
        if (point1 != null) {
            s += "point1 "+point1.value;
        }
        if (point2 != null) {
            s += "point2 "+point1.value;
        }

        return s;
    }
}
