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

    final private VectorTile tile;

    private Iterator<VectorTile> northTile;
    private LinkedList<VectorTile> currentRow;
    private VectorTile westTile;
    private int xOffset = 0;
    private int yOffset = 0;

    public VectorTileGroup(final VectorTile vectorTile) {
        this.northTile = null;
        this.westTile = null;
        this.tile = vectorTile;
        this.currentRow = new LinkedList<>();
        this.currentRow.add(vectorTile);
    }

    public void addEast(final VectorTile eastTile) {
        for (final Feature feature: eastTile.features) {
            feature.translate(xOffset, yOffset);
        }

        if (northTile != null && northTile.hasNext()) {
            final Iterator<Side> bottomSide = northTile.next().bottom.iterator();
            Side nw = bottomSide.next();

            final Iterator<Side> topSide = eastTile.top.iterator();
            Side sw = topSide.next();

            while (bottomSide.hasNext() && topSide.hasNext()) {
                final Side ne = bottomSide.next();
                final Side se = topSide.next();

                // Zip with the northern tile.
                final IsobandContours iso = new IsobandContours( new byte[]{nw.cell, ne.cell, se.cell, sw.cell});

                // FIXME - what do we do with the ISOBAND?!

                nw = ne;
                sw = se;
            }
        }

        if (westTile != null) {
            final Iterator<Side> rightSide = westTile.right.iterator();
            Side nw = rightSide.next();

            final Iterator<Side> leftSide = eastTile.left.iterator();
            Side ne = leftSide.next();

            while (leftSide.hasNext() && rightSide.hasNext()) {
                final Side sw = rightSide.next();
                final Side se = leftSide.next();

                // Zip with the northern tile.
                final IsobandContours iso = new IsobandContours( new byte[]{nw.cell, ne.cell, se.cell, sw.cell});

                // FIXME - what do we do with the ISOBAND?!

                nw = sw;
                ne = se;
            }
        }

        // The last thing we do is add the east tile to the current row list and make it the western tile.
        currentRow.add(eastTile);
        this.xOffset += eastTile.top.size();
        this.westTile = eastTile;
    }

    public void addNewRow(final VectorTile newTile) {
        // Drop to a new row.
        this.northTile = this.currentRow.iterator();
        this.westTile = null;
        this.xOffset = 0;
        this.yOffset += newTile.left.size();

        // Add an east tile.
        addEast(newTile);
    }

}
