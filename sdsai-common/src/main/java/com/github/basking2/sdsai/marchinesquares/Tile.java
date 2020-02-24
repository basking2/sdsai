package com.github.basking2.sdsai.marchinesquares;

public class Tile {
    /**
     * A tile of -1, 0, or 1 values. A value of -1 means the cell is below
     * the threshold. A value of 0 means it is at the threshold. A value of 1
     * means the cell is above the threshold.
     */
    public byte[] tile;

    public Contours[] contours;

    /**
     * How wide is each row in the tile.
     */
    public int width;

    public Tile(final byte[] tile, final int width) {
        this.tile = tile;
        this.width = width;
        this.contours = new Contours[tile.length];
    }

    /**
     * Build and attach {@link Contours} not on borders.
     */
    public void apply() {
        for (int i = 0; i < tile.length; i++) {

        }
    }
}
