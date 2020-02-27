package com.github.basking2.sdsai.marchinesquares;

/**
 * This class holds a point (and x and y value) and the two cell values that describe
 * the side of the cell it terminates on.
 *
 * In Marching Squares, lines terminate squares of four cells on the top, right, bottom, or left
 * side. Every line that terminates on the top of a square is surrounded by the top-left and top-right
 * cell value.
 *
 * Cells are always ordered from the top-left and proceed in a clockwise direction.
 */
public class PointAndCells {
    /**
     * This is not final because we will replace it with a translated point for stitching.
     */
    public Point point;

    /**
     * The first cell in the square, proceeding in a clockwise manner from the top-left to the bottom-left.
     */
    final public byte cell1;

    /**
     * The second cell in the square, proceeding in a clockwise manner from the top-left to the bottom-left.
     */
    final public byte cell2;

    /**
     * Is this point at the end of a line, meaning it is exiting.
     *
     * A value of false means this is the beginning of a line, entering. a tile.
     */
    final public boolean isEnd;

    /**
     * Constructor.
     * @param cell1 The first cell in the square, proceeding in a clockwise manner from the top-left to the bottom-left.
     * @param cell2 The second cell in the square, proceeding in a clockwise manner from the top-left to the bottom-left.
     */
    public PointAndCells(final Point point, final byte cell1, final byte cell2, final boolean isEnd) {
        assert cell1 != cell2;

        this.point = point;
        this.cell1 = cell1;
        this.cell2 = cell2;
        this.isEnd = isEnd;
    }

}
