package com.github.basking2.sdsai.marchinesquares;

import com.github.basking2.sdsai.RTree;

import java.util.ArrayList;

public class GeoJsonVectorTile extends VectorTile {

    public GeoJsonVectorTile(final VectorTile vectorTile) {

        final RTree<Double, Feature> rtree = new RTree();

        final ArrayList<Feature> holes = new ArrayList<>();

        for (final Feature f : vectorTile.features) {
            if (f.isCounterClockwise()) {
                rtree.add(buildBoundingBox(f), f);
            } else {
                holes.add(f);
            }
        }

        for (final Feature hole : holes) {
            rtree.findEnclosed(buildBoundingBox(hole), feature -> {
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

        rtree.forEach( node -> {
            this.features.add(node.getT());
            return true;
        });
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
}
