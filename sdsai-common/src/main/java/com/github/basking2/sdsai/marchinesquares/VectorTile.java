/**
 * Copyright (c) 2020-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.marchinesquares;

import com.github.basking2.sdsai.RTree;

import java.util.ArrayList;

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

    /**
     * Return A 2x2 array where [0][0] is x-min, [0][1] is x-max, [1][0] is y-min, and [1][1] is y-max.
     *
     * @param feature The features.
     * @return A 2x2 array where [0][0] is x-min, [0][1] is x-max, [1][0] is y-min, and [1][1] is y-max.
     */
    public static Double[][] buildBoundingBox(final Feature feature) {
        final Double[][] box = new Double[][]{
                { feature.points.value.x, feature.points.value.x },
                { feature.points.value.y, feature.points.value.y }
        };


        for (final Point p : feature.points) {
            box[0][0] = p.x < box[0][0]? p.x : box[0][0];
            box[0][1] = p.x > box[0][1]? p.x : box[0][1];
            box[1][0] = p.y < box[1][0]? p.y : box[1][0];
            box[1][1] = p.y > box[1][1]? p.y : box[1][1];
        }

        return box;
    }

    /**
     * Put negative features, polygons that are holes, inside features that are positive polygons.
     */
    public VectorTile collateHoles() {

        final RTree<Double, Feature> rtree = new RTree();

        final ArrayList<Feature> holes = new ArrayList<>();

        for (final Feature f : features) {
            if (f.isCounterClockwise()) {
                rtree.add(buildBoundingBox(f), f);
            } else {
                holes.add(f);
            }
        }

        for (final Feature hole : holes) {
            rtree.findEnclosing(buildBoundingBox(hole), feature -> {
                // We know that polygons in marching squares do not intersect, so checking a single point is
                // sufficient. We also know that holes are not contained in holes, so we do not need to check
                // the holes.
                if (feature.getT().containsPoint(hole.points.value, false)) {
                    feature.getT().holes.add(hole.points);

                    // Stop searching.
                    return false;
                }

                // Keep going.
                return true;
            });
        }

        this.features.clear();
        rtree.forEach( node -> {
            this.features.add(node.getT());
            return true;
        });

        return this;
    }
}
