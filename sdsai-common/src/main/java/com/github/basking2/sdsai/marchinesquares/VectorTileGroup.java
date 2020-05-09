package com.github.basking2.sdsai.marchinesquares;

import java.util.Iterator;

import static com.github.basking2.sdsai.marchinesquares.Colors.COLLECT_COLOR;
import static com.github.basking2.sdsai.marchinesquares.Colors.STITCH_COLOR;

/**
 * A vector tile group connects many {@link Tile} objects so they may be
 * joined to a single {@link VectorTile}.
 *
 * Tiles are retained for joining until they have no points left unjoined.
 */
public class VectorTileGroup {

    private boolean stitchTiles = true;

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
            tile.features.add(feature);

        }
        for (final Side s : eastTile.top) {
            translateSide(s);
        }
        for (final Side s : eastTile.right) {
            translateSide(s);
        }
        for (final Side s : eastTile.bottom) {
            translateSide(s);
        }
        for (final Side s : eastTile.left) {
            translateSide(s);
        }

        if (stitchTiles) {
            final VectorTile northTile;
            if (northTiles != null && northTiles.hasNext()) {
                northTile = northTiles.next();
                stitchNorthSouth(northTile, eastTile);
            } else {
                northTile = null;
            }

            if (westTile != null) {
                stitchWestEast(westTile, eastTile, northTile);
            }

            if (northTile != null) {
                // Conditionally set the north-west point for a future tile.
                northWestPoint = northTile.bottom.getTail();
            }
        }

        // Record this tile for a call to addNewRow().
        this.currentRow.add(eastTile);

        // The xOffset has permanently changed.
        this.xOffset += eastTile.top.size();

        // The one stored west tile is updated.
        this.westTile = eastTile;
    }

    /**
     * Stitch the eastern tiles to the western tiles.
     *
     * @param westTile The tile to the West. We want the right row.
     * @param eastTile The tile to the South. We want the left row.
     * @param northTile This is used to find the pixel directly north of the first pixel in the east tile.
     *                  The complimentary function {@link #stitchNorthSouth(VectorTile, VectorTile)}
     *                  does not need a third argument because it can directly access the {@link #westTile}
     *                  field from any previous tile add.
     */
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

        // NOTE: To gain the perspective of the neighboring cell, we use a reflected side.
        Side northSide = Side.buildReflectedArtificialSide(xOffset, yOffset, (byte)0, nw.cell, ne.cell);

        while (leftSide.hasNext() && rightSide.hasNext()) {
            final Side sw = rightSide.next();
            final Side se = leftSide.next();

            // NOTE: To gain the perspective of the neighboring cell, we use a reflected side.
            final Side southSide = Side.buildReflectedArtificialSide(xOffset, yOffset, (byte)2, se.cell, sw.cell);

            // Zip with the northern tile.
            final IsobandContours iso = new IsobandContours(nw.cell, ne.cell, se.cell, sw.cell);

            // Four sides of the cell we are in.
            final Side[] sides = new Side[]{ northSide, ne, southSide, nw };

            linkSides(iso, sides);

            nw = sw;
            ne = se;
            northSide = southSide;
            northSide.swapPoints();
            yOffset++;
        }


        // Put the yOffset back where we found it.
        yOffset -= westTile.right.size();
    }

    private void linkSides(final IsobandContours iso, final Side[] sides) {
        for (int i = 0; i < iso.lines.length; i+=2) {
            final byte lineStart = iso.lines[i];
            final byte lineEnd = iso.lines[i+1];

            if (sides[lineStart].endPoint == null) {
                sides[lineStart].endPoint = new LinkedList.Node<Point>(new Point(xOffset, yOffset, lineStart), null, COLLECT_COLOR);
            }
            if (sides[lineEnd].beginPoint == null) {
                sides[lineEnd].beginPoint = new LinkedList.Node<Point>(new Point(xOffset, yOffset, lineEnd), null, COLLECT_COLOR);
            }

            assert sides[lineStart].endPoint != null;
            assert sides[lineStart].endPoint.next == null;
            assert sides[lineEnd].beginPoint != null;

            // The end of the neighbor cell's line points to...
            //    ... the beginning of the other neighbor cell's line.
            sides[lineStart].endPoint.next = sides[lineEnd].beginPoint;

            checkFeature(sides[lineStart].endPoint);
        }
    }

    /**
     * Stitch the southern tiles to the northern tiles.
     *
     * @param northTile The tile to the North. We want the bottom row.
     * @param southTile The tile to the South. We want the top row.
     */
    private void stitchNorthSouth(final VectorTile northTile, final VectorTile southTile) {
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

        // NOTE: To gain the perspective of the neighboring cell, we use a reflected side.
        Side westSide = Side.buildReflectedArtificialSide(xOffset, yOffset, (byte)3, sw.cell, nw.cell);

        while (bottomSide.hasNext() && topSide.hasNext()) {
            final Side ne = bottomSide.next();
            final Side se = topSide.next();

            // NOTE: To gain the perspective of the neighboring cell, we use a reflected side.
            final Side eastSide = Side.buildReflectedArtificialSide(xOffset, yOffset, (byte)1, ne.cell, se.cell);

            // Zip with the northern tile.
            final IsobandContours iso = new IsobandContours(nw.cell, ne.cell, se.cell, sw.cell);

            final Side[] sides = new Side[]{ nw, eastSide, sw, westSide };

            linkSides(iso, sides);

            nw = ne;
            sw = se;
            westSide = eastSide;
            westSide.swapPoints();
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

        final LinkedList.Node<Point> nextPoint = end.endPoint;

        final LinkedList.Node<Point> originPoint = start.beginPoint;

        assert originPoint.next == null;
        nextPoint.color = COLLECT_COLOR;
        originPoint.next = nextPoint;
    }

    public void addNewRow() {
        // Drop to a new row.
        if (westTile != null) {
            this.yOffset += westTile.right.size();
        }
        this.xOffset = 0;
        this.northTiles = this.currentRow.iterator();
        this.currentRow = new LinkedList<>();
        this.northWestPoint = null;
        this.westTile = null;
    }

    public void addNewRow(final VectorTile newTile) {
        addNewRow();
        addEast(newTile);
    }

    public VectorTile getVectorTile() {
        return tile;
    }

    public void setStitchTiles(final boolean stitchTiles) {
        this.stitchTiles = stitchTiles;
    }

    private void translateSide(final Side s) {
        if (s.beginPoint != null && s.beginPoint.next == null) {
            s.beginPoint.value.x += xOffset;
            s.beginPoint.value.y += yOffset;
        }

        if (s.endPoint != null && s.endPoint.next == null) {
            s.endPoint.value.x += xOffset;
            s.endPoint.value.y += yOffset;
        }
    }

    /**
     * Check if the node is a feature. If it is, add it.
     * @param start The node to check.
     */
    private void checkFeature(final LinkedList.Node<Point> start) {
        LinkedList.Node<Point> stop = start;

        // While there is no loop and there is a node.
        while (stop != null && stop.color != STITCH_COLOR) {
            stop.color = STITCH_COLOR;
            stop = stop.next;
        }

        if (stop != null) {
            // Make sure we are 3 segments long for a proper polygon.
            if (start.next.next != start) {
                final LinkedList.Node<Point> newStartNode = new LinkedList.Node<>(new Point(start.value), start.next, start.color);

                // The start node is now the end node.
                start.next = null;

                final Feature feature = new Feature(newStartNode);

                tile.features.add(feature);
            }
        }
        else {
            // Uncolor the nodes.
            stop = start;

            // While there is no loop and there is a node.
            while (stop != null) {
                stop.color = COLLECT_COLOR;
                stop = stop.next;
            }
        }
    }
}
