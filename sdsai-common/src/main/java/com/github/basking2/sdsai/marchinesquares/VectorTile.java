package com.github.basking2.sdsai.marchinesquares;

/**
 */
public class VectorTile {

    /**
     * Fully finished features.
     *
     * These are often polygons, but can be curves.
     */
    public LinkedList<Feature> features;

    /**
     * These are lines that exited the polygon from the bottom.
     *
     * This list is ordered from left-to-right.
     */
    public LinkedList<PointAndCells> unfinishedLinesBottom;

    /**
     * These are lines that exited the polygon from the right;
     *
     * This list is ordered from top-to-bottom.
     */
    public LinkedList<PointAndCells> unfinishedLinesRight;
    /**
     * These are lines that exited the polygon from the left;
     *
     * This list is ordered from top-to-bottom.
     */
    public LinkedList<PointAndCells> unfinishedLinesLeft;

    /**
     * These are lines that exited the polygon from the top;
     *
     * This list is ordered from left-to-right.
     */
    public LinkedList<PointAndCells> unfinishedLinesTop;

    protected VectorTile() {
        this.features = new LinkedList<>();
        this.unfinishedLinesTop = new LinkedList<>();
        this.unfinishedLinesRight = new LinkedList<>();
        this.unfinishedLinesLeft = new LinkedList<>();
        this.unfinishedLinesBottom = new LinkedList<>();
    }


}
