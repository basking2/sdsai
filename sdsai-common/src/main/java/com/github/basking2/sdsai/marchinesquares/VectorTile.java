package com.github.basking2.sdsai.marchinesquares;

/**
 */
public class VectorTile {

    /**
     * Fully finished features.
     *
     * These are often polygons, but can be curves.
     */
    final public LinkedList<Feature> features;

    /**
     * These are lines that exited the polygon from the bottom.
     *
     * This list is ordered from left-to-right with out-bound edges preceding inbound edges.
     */
    final public LinkedList<Side> bottom;

    /**
     * These are lines that exited the polygon from the right;
     *
     * This list is ordered from top-to-bottom with out-bound edges preceding inbound edges.
     */
    final public LinkedList<Side> right;

    /**
     * These are lines that exited the polygon from the left;
     *
     * This list is ordered from top-to-bottom with out-bound edges preceding inbound edges.
     */
    final public LinkedList<Side> left;

    /**
     * These are lines that exited the polygon from the top;
     *
     * This list is ordered from left-to-right with out-bound edges preceding inbound edges.
     */
    final public LinkedList<Side> top;

    protected VectorTile() {
        this.features = new LinkedList<>();
        this.top = new LinkedList<>();
        this.right = new LinkedList<>();
        this.left = new LinkedList<>();
        this.bottom = new LinkedList<>();
    }
}
