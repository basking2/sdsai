package com.github.basking2.sdsai.marchinesquares;

import org.omg.CORBA.TRANSACTION_UNAVAILABLE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.basking2.sdsai.marchinesquares.Colors.COLLECT_COLOR;
import static com.github.basking2.sdsai.marchinesquares.Colors.STITCH_COLOR;

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

    public VectorTileGroup() {
        this.northTiles = null;
        this.westTile = null;
        this.tile = new VectorTile();
        this.currentRow = new LinkedList<>();
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

        // The xOffset has permanently changed.
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
            nw = rightSide.next();
            ne = leftSide.next();
            yOffset++;
        }

        Side northSide = Side.buildArtificialSide(xOffset, yOffset, (byte)0, nw.cell, ne.cell);

        while (leftSide.hasNext() && rightSide.hasNext()) {
            final Side sw = rightSide.next();
            final Side se = leftSide.next();
            final Side southSide = Side.buildArtificialSide(xOffset, yOffset, (byte)2, sw.cell, se.cell);

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
            northSide.cell = (byte)0;
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

        Side westSide = Side.buildArtificialSide(xOffset, yOffset, (byte)1, nw.cell, sw.cell);

        while (bottomSide.hasNext() && topSide.hasNext()) {
            final Side ne = bottomSide.next();
            final Side se = topSide.next();
            final Side eastSide = Side.buildArtificialSide(xOffset, yOffset, (byte)1, ne.cell, se.cell);

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
            westSide.cell = (byte)3;
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
        if (end.point2 == null) {
            // No second point. Easy.
            if (end.point1 == null) {
                throw new IllegalStateException("No linkable points.");
            }
            else {
                nextPoint = end.point1;
            }
        }
        else if (end.point1 == null) {
            // Sanity check.
            throw new IllegalStateException("No point to link against.");
        } else {
            // There are two points. Choose the best one.

            if (end.point1.next != null && end.point2.next == null) {
                nextPoint = end.point1;
            }
            else if (end.point1.next == null && end.point2.next != null) {
                nextPoint = end.point2;
            }
            else {
                throw new IllegalStateException("Both points are unlinked? This is unexpected.");
            }
        }

        final LinkedList.Node<Point> originPoint;
        if (start.point1 == null) {
            originPoint = new LinkedList.LabeledNode<>(new Point(xOffset, yOffset, pointSide), nextPoint);
            ((LinkedList.LabeledNode<Point>)originPoint).label = "Made here 3.";
            originPoint.color = COLLECT_COLOR;
            start.point1 = originPoint;
        }
        else if (start.point1.next == null) {
            originPoint = start.point1;
        }
        else if (start.point2 == null) {
            originPoint = new LinkedList.LabeledNode<>(new Point(xOffset, yOffset, pointSide), nextPoint);
            ((LinkedList.LabeledNode<Point>)originPoint).label = "Made here 4.";
            originPoint.color = COLLECT_COLOR;
            start.point2 = originPoint;
        }
        else if (start.point2.next == null) {
            originPoint = start.point2;
        }
        else {
            throw new IllegalStateException("Start Side does not have an unconnected point to link.");
        }

        if (originPoint.color == 0) {
            originPoint.color = COLLECT_COLOR;
            originPoint.value.x += xOffset;
            originPoint.value.y += yOffset;
        }

        nextPoint.color = COLLECT_COLOR;
        originPoint.next = nextPoint;
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

    public void addNewRow() {
        // Drop to a new row.
        if (westTile != null) {
            this.yOffset += westTile.right.size();
        }
        this.xOffset = 0;
        this.northTiles = this.currentRow.iterator();
        this.northWestPoint = null;
        this.westTile = null;
    }
}
