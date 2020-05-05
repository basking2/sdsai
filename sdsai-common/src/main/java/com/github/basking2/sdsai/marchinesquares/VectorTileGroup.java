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
    private Side nortWestPoint;

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
        this.nortWestPoint = null;
    }

    public void addEast(final VectorTile eastTile) {
        for (final Feature feature: eastTile.features) {
            feature.translate(xOffset, yOffset);
        }

        if (northTiles != null && northTiles.hasNext()) {
            stitchNorthSouth(northTiles.next(), eastTile);
        }

        if (westTile != null) {
            stitchWestEast(westTile, eastTile);
        }

        // The last thing we do is add the east tile to the current row list and make it the western tile.
        this.currentRow.add(eastTile);
        this.xOffset += eastTile.top.size();
        this.westTile = eastTile;
        this.nortWestPoint = eastTile.bottom.getTail();
    }

    void stitchWestEast(final VectorTile westTile, final VectorTile eastTile) {
        final Iterator<Side> rightSide = westTile.right.iterator();
        Side nw = rightSide.next();

        final Iterator<Side> leftSide = eastTile.left.iterator();
        Side ne = leftSide.next();

        while (leftSide.hasNext() && rightSide.hasNext()) {
            final Side sw = rightSide.next();
            final Side se = leftSide.next();

            // Zip with the northern tile.
            final IsobandContours iso = new IsobandContours( new byte[]{nw.cell, ne.cell, se.cell, sw.cell});

            for (int i = 0; i < iso.lineCount; i++) {

            }

            // FIXME - what do we do with the ISOBAND?!

            nw = sw;
            ne = se;
        }
    }

    void stitchNorthSouth(final VectorTile northTile, final VectorTile southTile) {
        final Iterator<Side> bottomSide = northTile.bottom.iterator();
        Side nw = bottomSide.next();

        final Iterator<Side> topSide = southTile.top.iterator();
        Side sw = topSide.next();

        while (bottomSide.hasNext() && topSide.hasNext()) {
            final Side ne = bottomSide.next();
            final Side se = topSide.next();

            // Zip with the northern tile.
            final IsobandContours iso = new IsobandContours( new byte[]{nw.cell, ne.cell, se.cell, sw.cell});

            // FIXME - what do we do with the ISOBAND?!
            for (int linei = 0; linei < iso.lineCount; linei++) {

            }

            nw = ne;
            sw = se;
        }
    }

    public void addNewRow(final VectorTile newTile) {
        // Drop to a new row.
        this.northTiles = this.currentRow.iterator();
        this.nortWestPoint = null;
        this.westTile = null;
        this.xOffset = 0;
        this.yOffset += newTile.left.size();

        // Add an east tile.
        addEast(newTile);
    }

}
