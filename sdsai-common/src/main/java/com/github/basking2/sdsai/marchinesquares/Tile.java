package com.github.basking2.sdsai.marchinesquares;

public class Tile {
    /**
     * A tile of -1, 0, or 1 values. A value of -1 means the cell is below
     * the threshold. A value of 0 means it is at the threshold. A value of 1
     * means the cell is above the threshold.
     */
    public byte[] tile;

    public IsobandContours[] contours;

    /**
     * How wide is each row in the tile.
     */
    public int width;

    public Tile(final byte[] tile, final int width) {
        this.tile = tile;
        this.width = width;
        this.contours = new IsobandContours[tile.length - width - 1];
    }

    /**
     * Build and attach {@link IsobandContours} for all cells.
     *
     * Note this does not define contours for the right and bottom edge as that is
     * left for tile joining.
     */
    public void isoband() {
        final int W = width - 1;
        final int H = tile.length / width - 1;

        // Populate the contours array.
        for (int h = 0; h < H; h++) {
            for (int w = 0; w < W; w++) {
                this.contours[h * W + w] = new IsobandContours(new byte[]{
                        tile[h * width + w],
                        tile[h * width + w + 1],
                        tile[h * width + width + w + 1],
                        tile[h * width + width + w],
                });
            }
        }
    }
}
