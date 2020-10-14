package com.github.basking2.sdsai.marchinesquares;

import java.util.Iterator;

import static com.github.basking2.sdsai.marchinesquares.Colors.*;

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
     * Null or the last tile passed to {@link #addEast(VectorTile)} or {@link #addNewRow(VectorTile)}.
     */
    private VectorTile westTile;

    /**
     * The south-east point of the previous tile added.
     *
     * This is cleared by calls to {@link #addNewRow(VectorTile)}.
     */
    private Side northWestBottomPoint;

    /**
     * The south-east point of the previous tile added.
     *
     * This is cleared by calls to {@link #addNewRow(VectorTile)}.
     */
    private Side northWestRightPoint;

    /**
     * Accumulates tiles passed to {@link #addEast(VectorTile)} or {@link #addNewRow(VectorTile)}.
     *
     * When {@link #addNewRow(VectorTile)} is called this is set to {@link #northTiles} and this is replaced
     * with a new list.
     */
    private LinkedList<VectorTile> currentRow;
    private int xOffset = 0;
    private int yOffset = 0;

    private final FeatureFactory featureFactory;

    public VectorTileGroup(final FeatureFactory featureFactory) {
        this.northTiles = null;
        this.westTile = null;
        this.tile = new VectorTile();
        this.currentRow = new LinkedList<>();
        this.northWestBottomPoint = null;
        this.northWestRightPoint = null;
        this.featureFactory = featureFactory;
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
                northWestBottomPoint = northTile.bottom.getTail();
                northWestRightPoint = northTile.right.getTail();
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
    private void stitchWestEast(final VectorTile westTile, final VectorTile eastTile, final VectorTile northTile) {
        final Iterator<Side> leftItr = westTile.right.iterator();
        final Iterator<Side> rightItr = eastTile.left.iterator();

        // Find the previous two Side nodes.
        Side northSide;
        Side southSide;
        Side nw;
        Side ne;
        Side sw;
        Side se;


        if (northWestRightPoint != null && northTile != null) {
            // If there is a Four Corners square, set up to stitch it.
            nw = northWestRightPoint;
            ne = northTile.left.getTail();
            sw = leftItr.next();
            se = rightItr.next();

            northSide = northWestBottomPoint;

            // Sides that have never been processed need their points set.
            ne.setPoints(xOffset, yOffset, (byte)3, STITCH_COLOR, se.cell, ne.cell);
            se.setPoints(xOffset, yOffset, (byte)0, STITCH_COLOR, sw.cell, se.cell);

            yOffset++;

            southSide = westTile.top.getTail();
            southSide.setPoints(xOffset, yOffset, (byte)2, STITCH_COLOR, sw.cell, se.cell);
        } else {
            nw = leftItr.next();
            ne = rightItr.next();
            sw = leftItr.next();
            se = rightItr.next();

            yOffset += 1;

            // The north side has no points in it. We must build those with setPoints.
            northSide = westTile.top.getTail();
            northSide.setPoints(xOffset, yOffset, (byte)2, STITCH_COLOR, ne.cell, nw.cell);

            yOffset += 1;
            southSide = Side.buildArtificialSide(xOffset-1, yOffset-1, (byte)0, STITCH_COLOR, sw.cell, se.cell);
        }

        // FIXME
        // NorthSouth should be OK. WestEast, start looking here.
        // FIXME

        while (true) {
            // Zip with the northern tile.
            final IsobandContours iso = new IsobandContours(nw.cell, ne.cell, se.cell, sw.cell);

            // Four sides of the cell we are in.
            final Side[] sides = new Side[]{ northSide, ne, southSide, nw };

            linkSides(iso, sides);

            if (!leftItr.hasNext() || !rightItr.hasNext()) {
                break;
            }

            nw = sw;
            ne = se;
            sw = leftItr.next();
            se = rightItr.next();
            northSide = southSide;
            northSide.swapPoints();

            // NOTE: To gain the perspective of the neighboring cell, we use a reflected side.
            southSide = Side.buildArtificialSide(xOffset-1, yOffset, (byte)0, STITCH_COLOR, sw.cell, se.cell);

            yOffset++;
        }


        // Put the yOffset back where we found it.
        yOffset -= westTile.right.size();
    }

    private void linkSides(final IsobandContours iso, final Side[] sides) {
        for (int i = 0; i < iso.lines.length; i+=2) {
            final byte lineStart = iso.lines[i];
            final byte lineEnd = iso.lines[i+1];

            assert sides[lineStart].endPoint != null;
            assert sides[lineStart].endPoint.next == null;
            assert sides[lineEnd].beginPoint != null;

            // The end of the neighbor cell's line points to...
            //    ... the beginning of the other neighbor cell's line.
            // This relationship is backward when stitching between tiles.
            // The end is the end of a feature that went to the wall of a
            // tile and could not continue.
            sides[lineStart].endPoint.next = sides[lineEnd].beginPoint;

            checkFeature(sides[lineStart].endPoint);
        }
    }

    /**
     * Stitch the southern tiles to the northern tiles starting with the first cell, not the previous cell.
     *
     * The previous cell is stitched by the east-west function.
     *
     * @param northTile The tile to the North. We want the bottom row.
     * @param southTile The tile to the South. We want the top row.
     */
    private void stitchNorthSouth(final VectorTile northTile, final VectorTile southTile) {
        final Iterator<Side> topItr = northTile.bottom.iterator();
        final Iterator<Side> bottomItr = southTile.top.iterator();

        // Get the four points aligned in the direction we are iterating.
        // These are real sides that exist in the tile. They have points from when they
        // where isobanded and contoured. Their perspective is inside-out, as it were. That is,
        // their edges point into the tile they are a part of. When we stitch things, it will be
        // the reverse of how it normally is done.
        Side nw = topItr.next();
        Side sw = bottomItr.next();
        Side ne = topItr.next();
        Side se = bottomItr.next();

        // This is a side that has never had points assigned to it.
        // Notice we setPoints() as if this is on side 1, not 3. Recall our perspective is
        // reflected because we are between tiles.
        Side westSide = northTile.left.getTail();
        westSide.setPoints(xOffset, yOffset, (byte)1, STITCH_COLOR, nw.cell, sw.cell);

        xOffset += 2;

        while (true) {

            final Side eastSide;
            if (topItr.hasNext()) {
                // If this is not the last edge, build an artificial side from the east-to-west sides's cell values.
                eastSide = Side.buildArtificialSide(xOffset-1, yOffset-1, (byte)3, STITCH_COLOR, se.cell, ne.cell);
            } else {
                // If this is the last edge, we still must set points, but we use the actual Side object from the
                // north tile.
                eastSide = northTile.right.getTail();
                eastSide.setPoints(xOffset-1, yOffset-1, (byte)3, STITCH_COLOR, se.cell, ne.cell);
            }


            // Zip with the northern tile of the four points.
            final IsobandContours iso = new IsobandContours(nw.cell, ne.cell, se.cell, sw.cell);

            // Build a list of the sides that correspond with the points.
            final Side[] sides = new Side[]{ nw, eastSide, sw, westSide };

            // Link the sides together.
            linkSides(iso, sides);

            // Advance the sides and swap the points.
            // Yes, we do this before the possible break.
            westSide = eastSide;

            // Reflect the perspective.
            // NOTE: This leaves the last side pointing into the tiles we are stitching. We want this.
            //       This is correct for when we stitch the Four Corners squares.
            westSide.swapPoints();

            if (!bottomItr.hasNext() || !topItr.hasNext()) {
                break;
            }

            // If we have not exited, walk to the east and continue.
            nw = ne;
            sw = se;
            ne = topItr.next();
            se = bottomItr.next();

            xOffset++;
        }

        // Put the xOffset back where we found it.
        xOffset -= northTile.bottom.size();
    }

    public void addNewRow() {
        // Drop to a new row.
        if (westTile != null) {
            this.yOffset += westTile.right.size();
        }
        this.xOffset = 0;
        this.northTiles = this.currentRow.iterator();
        this.currentRow = new LinkedList<>();
        this.northWestBottomPoint = null;
        this.northWestRightPoint = null;
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
        translateUnfeaturedPoints(s.beginPoint);
        translateUnfeaturedPoints(s.endPoint);
    }

    private void translateUnfeaturedPoints(LinkedList.Node<Point> point) {
        while (point != null) {
            if (point.color == DEFAULT_COLOR) {
                point.color = STITCH_COLOR;
                point.value.x += xOffset;
                point.value.y += yOffset;
            }
            point = point.next;
        }
    }

    /**
     * Check if the node is a feature. If it is, add it.
     * @param start The node to check.
     */
    private void checkFeature(final LinkedList.Node<Point> start) {
        LinkedList.Node<Point> stop = start.next;

        // While there is no loop and there is a node.
        while (stop != null && stop != start) {
            stop.color = STITCH_COLOR;
            stop = stop.next;
        }

        if (stop != null) {
            // Make sure we are 3 segments long for a proper polygon.
            if (start.next.next != start) {
                final LinkedList.Node<Point> newStartNode = new LinkedList.Node<>(new Point(start.value), start.next, start.color);

                // The start node is now the end node.
                start.next = null;

                final Feature feature = featureFactory.buildFeature(newStartNode);

                tile.features.add(feature);
            }
        }
        else {
            // Uncolor the nodes.
            stop = start;

            // While there is no loop and there is a node.
            while (stop != null) {
                stop.color = DEFAULT_COLOR;
                stop = stop.next;
            }
        }
    }
}
