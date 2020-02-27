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
    public Point point;
    public byte cell1;
    public byte cell2;

    /**
     * Constructor.
     * @param cell1 The values of the first cell.
     * @param cell2 The value of the second cell.
     */
    public PointAndCells(final Point point, final byte cell1, final byte cell2) {
        this.point = point;
        this.cell1 = cell1;
        this.cell2 = cell2;
    }
}
