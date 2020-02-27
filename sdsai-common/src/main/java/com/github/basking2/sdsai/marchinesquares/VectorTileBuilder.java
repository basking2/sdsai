package com.github.basking2.sdsai.marchinesquares;

public class VectorTileBuilder {

    private final Tile tile;
    private final int HEIGHT;
    private final int WIDTH;
    final VectorTile vectorTile;

    final LinkedList.Node<Point>[][] featureField;

    public VectorTileBuilder(final Tile tile) {
        this.vectorTile = new VectorTile();
        this.tile = tile;
        this.HEIGHT = tile.tile.length / tile.width - 1;
        this.WIDTH = tile.width - 1;
        this.featureField = new LinkedList.Node[tile.contours.length][];
    }
    /**
     * Runs {@link Tile#isoband()} and calls {@link #build()}.
     *
     * @return A built vector tile.
     * @throws RuntimeException on errors that prevent object construction.
     */
    public VectorTile buildIsoband() {
        tile.isoband();
        return build();
    }

    /**
     * Build a new {@link VectorTile} or throw a {@link RuntimeException}.
     *
     * The tile should have had {@link Tile#isoband()} run on it already.
     *
     * @return A built vector tile.
     * @throws RuntimeException on errors that prevent object construction.
     */
    public VectorTile build() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final int i = y * WIDTH + x;

                if (featureField[i] == null) {
                    featureField[i] = new LinkedList.Node[tile.contours[i].lineCount];
                }

                // Walk all the contours we have. Each contour is 0 - 4 lines.
                for (int j = 0; j < tile.contours[i].lineCount; j++) {
                    if (featureField[i][j] == null) {
                        // Ah! This is new! Trace it.

                        /**
                         * - lineBegin The side of the square the line begins on. The tail. Sides are 0 is the top,
                         *             1 is the right, 2 is the bottom, and 3 is the left side.
                         * - lineEnd The side of the square the line ends on. The head. Sides are 0 is the top,
                         *           1 is the right, 2 is the bottom, and 3 is the left side.
                         */
                        final byte lineBegin = tile.contours[i].lines[j*2];
                        final byte lineEnd = tile.contours[i].lines[j*2+1];

                        final Point p = new Point(x, y, lineEnd);
                        featureField[i][j] = new LinkedList.Node(p, null);

                        // Handle where lines end in this square.
                        // We check the above and left squares for in-bound lines to us.
                        if (lineEnd == 3) {
                            if (x == 0) {
                                vectorTile.unfinishedLinesLeft.add(new PointAndCells(p, tile.tile[i], tile.tile[i + WIDTH]));
                            } else {
                                for (int k = 0; k < tile.contours[i-1].lineCount; k++) {
                                    if (tile.contours[i-1].lines[k*2] == 1) {
                                        // Line k starts on side 1, then we connect our line j that ends on side 3.
                                        assert featureField[i][j].next == null;
                                        featureField[i][j].next = featureField[i-1][k];
                                    }
                                }
                            }
                        }
                        else if (x==WIDTH-1 && lineEnd == 1) {
                            vectorTile.unfinishedLinesRight.add(new PointAndCells(p, tile.tile[i+1], tile.tile[i+1+WIDTH]));
                        }
                        else if (lineEnd == 0) {
                            if (y == 0) {
                                vectorTile.unfinishedLinesTop.add(new PointAndCells(p, tile.tile[i], tile.tile[i + 1]));
                            }
                            else {
                                for (int k = 0; k < tile.contours[i-WIDTH].lineCount; k++) {
                                    if (tile.contours[i-WIDTH].lines[k*2] == 2) {
                                        // Line k starts on side 2, then we connect our line j that ends on side 0.
                                        assert featureField[i][j].next == null;
                                        featureField[i][j].next = featureField[i-WIDTH][k];
                                    }
                                }
                            }
                        }
                        else if (y == HEIGHT-1 && lineEnd == 2) {
                            vectorTile.unfinishedLinesBottom.add(new PointAndCells(p, tile.tile[i+WIDTH], tile.tile[i+1+WIDTH]));
                        }

                        // Handle where lines begin in this square.
                        // We check the above and left squares for out-bound lines to us.
                        if (lineBegin == 3) {
                            if (x == 0) {
                                vectorTile.unfinishedLinesLeft.add(new PointAndCells(p, tile.tile[i], tile.tile[i + WIDTH]));
                            }
                            else {
                                for (int k = 0; k < tile.contours[i-1].lineCount; k++) {
                                    if (tile.contours[i-1].lines[k*2+1] == 1) {
                                        // Line k ends on side 1, then we connect our line j that starts on side 3.
                                        assert featureField[i-1][k].next == null;
                                        featureField[i-1][k].next = featureField[i][j];
                                    }
                                }
                            }
                        }
                        else if (x == WIDTH-1 && lineBegin == 1) {
                            vectorTile.unfinishedLinesRight.add(new PointAndCells(p, tile.tile[i+1], tile.tile[i+1+WIDTH]));
                        }
                        else if (lineBegin == 0) {
                            if (y == 0) {
                                vectorTile.unfinishedLinesTop.add(new PointAndCells(p, tile.tile[i], tile.tile[i + 1]));
                            }
                            else {
                                for (int k = 0; k < tile.contours[i-WIDTH].lineCount; k++) {
                                    if (tile.contours[i-WIDTH].lines[k*2+1] == 2) {
                                        // Line k ends on side 2, then we connect our line j that starts on side 0.
                                        assert featureField[i-WIDTH][k].next == null;
                                        featureField[i-WIDTH][k].next = featureField[i][j];
                                    }
                                }
                            }
                        }
                        else if (y == HEIGHT - 1 && lineBegin == 2) {
                            vectorTile.unfinishedLinesBottom.add(new PointAndCells(p, tile.tile[i+WIDTH], tile.tile[i+1+WIDTH]));
                        }
                    }
                }
            }
        }

        return vectorTile;
    }

    /**
     * Trace a line until it closes into a polygon or it leaves the edge of the tile.
     *
     * @param x The tile x value.
     * @param y The tile y value.
     * @param feature The collection of features that the created points should be added to.
     *                Only the head, the point generated by lineEnd, is added.
     */
    private void traceLine(final int x, final int y, final byte lineBegin, final byte lineEnd, final LinkedList<Point> feature) {

        if (y == 0) {
            // Consider top exits and entries.
        }

        if (x == WIDTH-1) {
            // Consider right exits and entries.
        }

        if (y == HEIGHT-1) {
            // Consider bottom exits and entries.
        }

        if (x == 0) {
            // Consider left exits and entries.
        }
    }
}
