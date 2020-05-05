package com.github.basking2.sdsai.marchinesquares;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A vector tile group connects many {@link Tile} objects so they may be
 * joined to a single {@link VectorTile}.
 *
 * Tiles are retained for joining until they have no points left unjoined.
 */
public class VectorTileGroup {

    /**
     * The first tile added. It is assumed all features will eventually be reachable through this.
     */
    final private VectorTile tile;

    /**
     * Null or an iteration of the previous row.
     */
    private Iterator<VectorTile> northTiles;

    /**
     * Nyll or the last tile passed to {@link #addEast(VectorTile)} or {@link #addNewRow(VectorTile)}.
     */
    private VectorTile westTile;

    /**
     * The south-east point of the previous tile added.
     *
     * This is cleared by calls to {@link #addNewRow(VectorTile)}.
     */
    private Side northWestPoint;

    /**
     * Accumulates tiles passed to {@link #addEast(VectorTile)} or {@link #addNewRow(VectorTile)}.
     *
     * When {@link #addNewRow(VectorTile)} is called this is set to {@link #northTiles} and this is replaced
     * with a new list.
     */
    private LinkedList<VectorTile> currentRow;
    private int xOffset = 0;
    private int yOffset = 0;

    public VectorTileGroup(final VectorTile vectorTile) {
        this.northTiles = null;
        this.westTile = null;
        this.tile = vectorTile;
        this.currentRow = new LinkedList<>();
        this.currentRow.add(vectorTile);
        this.northWestPoint = null;
    }

    public void addEast(final VectorTile eastTile) {
        for (final Feature feature: eastTile.features) {
            feature.translate(xOffset, yOffset);
        }

        final VectorTile northTile;

        if (northTiles != null && northTiles.hasNext()) {
            northTile = northTiles.next();
            stitchNorthSouth(northTile, eastTile);
        }
        else {
            northTile = null;
        }

        if (westTile != null) {
            stitchWestEast(westTile, eastTile, northTile);
        }

        if (northTile != null) {
            // Conditionally set the north-west point for a future tile.
            northWestPoint = northTile.bottom.getTail();
        }

        // Record this tile for a call to addNewRow().
        this.currentRow.add(eastTile);

        // The xOffset has permenantly changed.
        this.xOffset += eastTile.top.size();

        // The one stored west tile is updated.
        this.westTile = eastTile;
    }

    // Stitch west-east nodes.
    void stitchWestEast(final VectorTile westTile, final VectorTile eastTile, final VectorTile northTile) {
        final Iterator<Side> rightSide = westTile.right.iterator();
        final Iterator<Side> leftSide = eastTile.left.iterator();

        // Find the previous two Side nodes.
        Side nw;
        Side ne;
        if (northWestPoint != null && northTile != null) {
            nw = northWestPoint;
            ne = northTile.left.getTail();
        } else {
            xOffset++;
            nw = rightSide.next();
            ne = leftSide.next();
            yOffset++;
        }

        Side northSide = new Side((byte)64); // Side with a bogus value. This only holds points.
        Side southSide = new Side((byte)64); // Side with a bogus value. This only holds points.

        while (leftSide.hasNext() && rightSide.hasNext()) {
            final Side sw = rightSide.next();
            final Side se = leftSide.next();

            // Zip with the northern tile.
            final IsobandContours iso = new IsobandContours(new byte[]{nw.cell, ne.cell, se.cell, sw.cell});

            final Side[] sides = new Side[]{ northSide, ne, southSide, nw };

            for (int i = 0; i < iso.lines.length; i+=2) {
                final byte lineStart = iso.lines[i];
                final byte lineEnd = iso.lines[i+1];
                connectSides(sides[lineStart], sides[lineEnd], lineEnd);
            }

            nw = sw;
            ne = se;
            northSide = southSide;
            southSide = new Side((byte)64); // New east side with same bogus value.
            yOffset++;
        }


        // Put the yOffset back where we found it.
        yOffset -= westTile.right.size();
    }

    // Switch north-south nodes.
    void stitchNorthSouth(final VectorTile northTile, final VectorTile southTile) {
        final Iterator<Side> bottomSide = northTile.bottom.iterator();
        final Iterator<Side> topSide = southTile.top.iterator();

        // Find the previous two Side nodes.
        Side nw;
        Side sw;
        if (northWestPoint != null) {
            nw = northWestPoint;
            sw = westTile.top.getTail();
        } else {
            nw = bottomSide.next();
            sw = topSide.next();
            xOffset++;
        }

        Side eastSide = new Side((byte)64); // Side with a bogus value. This only holds points.
        Side westSide = new Side((byte)64); // Side with a bogus value. This only holds points.

        while (bottomSide.hasNext() && topSide.hasNext()) {
            final Side ne = bottomSide.next();
            final Side se = topSide.next();

            // Zip with the northern tile.
            final IsobandContours iso = new IsobandContours(new byte[]{nw.cell, ne.cell, se.cell, sw.cell});

            final Side[] sides = new Side[]{ nw, eastSide, sw, westSide };

            for (int i = 0; i < iso.lines.length; i+=2) {
                final byte lineStart = iso.lines[i];
                final byte lineEnd = iso.lines[i+1];
                connectSides(sides[lineStart], sides[lineEnd], lineEnd);
            }

            nw = ne;
            sw = se;
            westSide = eastSide;
            eastSide = new Side((byte)64); // New east side with same bogus value.
            xOffset++;
        }

        // Put the xOffset back where we found it.
        xOffset -= northTile.bottom.size();
    }

    /**
     * Connect a start side to an end side, taking the first null-next
     * from the start and connecting it with the first not-null-next
     * in the end side.
     *
     * The intuitive logic is that we should never connect to a point that
     * doesn't go somewhere.
     *
     * @param start The start side.
     * @param end The end side with on node with a defined next.
     */
    private void connectSides(final Side start, final Side end, byte pointSide) {

        final LinkedList.Node<Point> nextPoint;
        if (end.point1 == null) {
            // If there are no points, make one and use it.
            nextPoint = end.point1 = new LinkedList.Node<>(new Point(xOffset, yOffset, pointSide), null);
        }
        else if (end.point1.next != null) {
            nextPoint = end.point1;
        }
        else if (end.point2 == null) {
            // If there are no points, make one and use it.
            nextPoint = end.point2 = new LinkedList.Node<>(new Point(xOffset, yOffset, pointSide), null);
        }
        else if (end.point2.next != null) {
            nextPoint = end.point2;
        }
        else {
            throw new IllegalStateException("End Side does not have a connected out-point to link to.");
        }


        if (start.point1 != null && start.point1.next == null) {
            start.point1.next = nextPoint;
        }
        else if (start.point2 != null && start.point2.next == null) {
            start.point2.next = nextPoint;
        }
        else {
            throw new IllegalStateException("Start Side does not have an unconnected point to link.");
        }
    }

    /**
     * When this is called, it is anticipated that any Side passed to this shall have at most 1 point with a free next pointer.
     * @param side The side to inspect.
     * @return The point node that has a free next node.
     */
    private LinkedList.Node<Point> getOrigin(final Side side) {
        if (side.point1 == null) {
            return side.point1;
        }
        else if (side.point2 == null) {
            return side.point2;
        }
        else {
            throw new IllegalStateException("After linking contours but before stitching tiles, two edge points both have next points.");
        }
    }

    public void addNewRow(final VectorTile newTile) {
        // Drop to a new row.
        this.northTiles = this.currentRow.iterator();
        this.northWestPoint = null;
        this.westTile = null;
        this.xOffset = 0;

        // Add an east tile.
        addEast(newTile);
    }

}
