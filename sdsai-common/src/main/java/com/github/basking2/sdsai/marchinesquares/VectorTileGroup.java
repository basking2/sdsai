package com.github.basking2.sdsai.marchinesquares;

import java.util.ArrayList;

/**
 * A vector tile group connects many {@link Tile} objects so they may be
 * joined to a single {@link VectorTile}.
 *
 * Tiles are retained for joining until they have no points left unjoined.
 */
public class VectorTileGroup {

    static class TileAndOffsets {
        public Tile tile;
        public int xOffset;
        public int yOffset;

        public TileAndOffsets(final Tile tile, final int xOffset, final int yOffset) {
            this.tile = tile;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    final VectorTile vectorTile;
    final ArrayList<TileAndOffsets> tiles;

    public VectorTileGroup(final VectorTile vectorTile, final Tile tile) {
        this.vectorTile = vectorTile;
        this.tiles = new ArrayList<>();
        this.tiles.add(new TileAndOffsets(tile, 0, 0));
    }

}
