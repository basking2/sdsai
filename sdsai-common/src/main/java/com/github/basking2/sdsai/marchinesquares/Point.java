package com.github.basking2.sdsai.marchinesquares;

public class Point {
    public final int x;
    public final int y;
    public final byte side;

    /**
     * Make a new point.
     * @param x
     * @param y
     * @param side
     */
    public Point(final int x, final int y, final byte side) {
        this.x = x;
        this.y = y;
        this.side = side;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)-%d", x, y, side);
    }
}
