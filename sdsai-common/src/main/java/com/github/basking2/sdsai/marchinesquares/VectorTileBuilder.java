package com.github.basking2.sdsai.marchinesquares;

import static com.github.basking2.sdsai.marchinesquares.Colors.COLLECT_COLOR;

public class VectorTileBuilder {

    private final Tile tile;
    private final int HEIGHT;
    private final int WIDTH;
    final VectorTile vectorTile;

    /**
     * Feature field is indexed by [y][x][line] where the line is always a starting point connected to
     * an exiting edge.
     *
     * When building polygons, edges from neighboring cells are obtained.
     */
    final LinkedList.Node<Point>[][][] featureField;

    public VectorTileBuilder(final Tile tile) {
        this.vectorTile = new VectorTile();
        this.tile = tile;
        this.HEIGHT = tile.tile.length / tile.width - 1;
        this.WIDTH = tile.width - 1;
        this.featureField = new LinkedList.Node[HEIGHT][WIDTH][];
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
     *
     * @return A built vector tile.
     * @throws RuntimeException on errors that prevent object construction.
     */
    public VectorTile build() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final int i = y * WIDTH + x;
                featureField[y][x] = new LinkedList.Node[tile.contours[i].lineCount];

                // Populate tile vectorTile.top, vectorTile.right, vectorTile.bottom, and vectorTile.left.
                addSides(x, y);

                // Walk all the contours we have. Each contour is 0 - 4 lines.
                for (int j = 0; j < tile.contours[i].lineCount; j++) {
                    assert featureField[y][x][j] == null;

                    /**
                     * - lineBegin The side of the square the line begins on. The tail. Sides are 0 is the top,
                     *             1 is the right, 2 is the bottom, and 3 is the left side.
                     * - lineEnd The side of the square the line ends on. The head. Sides are 0 is the top,
                     *           1 is the right, 2 is the bottom, and 3 is the left side.
                     */
                    final byte lineBegin = tile.contours[i].lines[j * 2];
                    final byte lineEnd = tile.contours[i].lines[j * 2 + 1];

                    // FIXME - points are not offset from one another in any way, internal. They are all on the same xy.
                    final LinkedList.LabeledNode<Point> pBegin = buildPointLineNode(x, y, lineBegin);
                    final LinkedList.LabeledNode<Point> pEnd = buildPointLineNode(x, y, lineEnd);
                    pBegin.label = "Beginning";
                    pEnd.label = "Ending";
                    pBegin.next = pEnd;
                    featureField[y][x][j] = pBegin;

                    //-------------------------------------------------------------------------
                    // At here the [y][x] has a line in it. We should stitch it to
                    // cells above and below.
                    //-------------------------------------------------------------------------
                    switch (lineEnd) {
                        case 0:
                            // FIXME fetch a line out from above us.
                            // FIXME side check.
                            break;
                        case 1:
                            // FIXME side check.
                            break;
                        case 2:
                            // FIXME side check.
                            break;
                        case 3:
                            // FIXME fetch from behind us.
                            // FIXME side check.
                            break;
                        default:
                            throw new IllegalStateException("Unhandled edge value "+lineEnd);
                    }

                    switch (lineBegin) {
                        case 0:
                            // FIXME fetch a line out from above us.
                            // FIXME side check.
                            break;
                        case 1:
                            // FIXME side check.
                            break;
                        case 2:
                            // FIXME side check.
                            break;
                        case 3:
                            // FIXME fetch from behind us.
                            // FIXME side check.
                            break;
                        default:
                            throw new IllegalStateException("Unhandled edge value "+lineBegin);
                    }

                    // Handle where lines end in this square.
                    // We check the above and left squares for in-bound lines to us.
                    if (lineEnd == 3) {
                        if (x == 0) {
                            vectorTile.left.getTail().point1 = pEnd;
                        } else {
                            for (int k = 0; k < tile.contours[i - 1].lineCount; k++) {
                                if (tile.contours[i - 1].lines[k * 2] == 1) {
                                    // Line k starts on side 1, then we connect our line j that ends on side 3.
                                    assert featureField[i][j].next == null;
                                    featureField[i][j].next = featureField[i - 1][k];
                                }
                            }
                        }
                    } else if (x == WIDTH - 1 && lineEnd == 1) {
                        vectorTile.right.getTail().point1 = pEnd;
                    } else if (lineEnd == 0) {
                        if (y == 0) {
                            vectorTile.top.getTail().point1 = pEnd;
                        } else {
                            for (int k = 0; k < tile.contours[i - WIDTH].lineCount; k++) {
                                if (tile.contours[i - WIDTH].lines[k * 2] == 2) {
                                    // Line k starts on side 2, then we connect our line j that ends on side 0.
                                    assert featureField[i][j].next == null;
                                    featureField[i][j].next = featureField[i - WIDTH][k];
                                }
                            }
                        }
                    } else if (y == HEIGHT - 1 && lineEnd == 2) {
                        vectorTile.bottom.getTail().point1 = pEnd;
                    }

                    // Handle where lines begin in this square.
                    // We check the above and left squares for out-bound lines to us.
                    if (lineBegin == 3) {
                        if (x == 0) {
                            final Side s = vectorTile.left.getTail();
                            s.addPoint(pBegin);
                        } else {
                            for (int k = 0; k < tile.contours[i - 1].lineCount; k++) {
                                if (tile.contours[i - 1].lines[k * 2 + 1] == 1) {
                                    // Line k ends on side 1, then we connect our line j that starts on side 3.
                                    assert featureField[i - 1][k].next == null;
                                    featureField[i - 1][k].next = featureField[i][j];
                                }
                            }
                        }
                    } else if (x == WIDTH - 1 && lineBegin == 1) {
                        final Side s = vectorTile.right.getTail();
                        s.addPoint(pBegin);
                    } else if (lineBegin == 0) {
                        if (y == 0) {
                            final Side s = vectorTile.top.getTail();
                            s.addPoint(pBegin);
                        } else {
                            for (int k = 0; k < tile.contours[i - WIDTH].lineCount; k++) {
                                if (tile.contours[i - WIDTH].lines[k * 2 + 1] == 2) {
                                    // Line k ends on side 2, then we connect our line j that starts on side 0.
                                    assert featureField[i - WIDTH][k].next == null;
                                    featureField[i - WIDTH][k].next = featureField[i][j];
                                }
                            }
                        }
                    } else if (y == HEIGHT - 1 && lineBegin == 2) {
                        final Side s = vectorTile.bottom.getTail();
                        s.addPoint(pBegin);
                    }
                }
            }
        }

        // Put the last side values in place.
        // NOTE: These do not have next points because those reside in future tiles.
        vectorTile.top.add(new Side(tile.tile[tile.width-1]));
        vectorTile.bottom.add(new Side(tile.tile[tile.tile.length - 1]));
        vectorTile.left.add(new Side(tile.tile[tile.tile.length - tile.width]));
        vectorTile.right.add(new Side(tile.tile[tile.tile.length - 1]));

        collectPolygons();

        return vectorTile;
    }

    private void collectPolygons() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final int i = y * WIDTH + x;
                assert featureField[i] != null;
                // Walk all the contours we have. Each contour is 0 - 4 lines.
                for (int j = 0; j < tile.contours[i].lineCount; j++) {
                    assert featureField[i][j] != null;

                    final LinkedList.Node<Point> start = featureField[i][j];
                    LinkedList.Node<Point> stop = start.next;
                    start.color = COLLECT_COLOR;
                    while (start != stop && stop != null) {
                        if (stop.color == COLLECT_COLOR) {
                            stop = null;
                        }
                        else {

                            //
                            // Validation code.
                            //
                            // This code checks that all paths that terminate (not loop) are on border nodes.
                            //
                            // It is expensive to keep on, so it remains here commented out.
                            //

                            /*
                            if (stop.next == null && stop.value.x != 0 && stop.value.y != 0 && stop.value.y != HEIGHT-1 && stop.value.x != WIDTH-1 ) {
                                System.out.println("An internal node has a null pointer??? How?");
                                LinkedList.Node<Point> last = start;
                                while (last != null) {
                                    System.out.println("\tPoint "+last.value);
                                    last = last.next;
                                }

                                System.out.println("Values around the last good end point.");
                                for (int y2 = stop.value.y; y2 < stop.value.y+3; y2++) {
                                    for (int x2 = stop.value.x; x2 < stop.value.x+3; x2++) {
                                        System.out.print(tile.tile[y2*tile.width+x2]);
                                        System.out.print(" ");
                                    }
                                    System.out.println("");
                                }

                                assert false;
                            }
                             */

                            stop.color = COLLECT_COLOR;
                            stop = stop.next;
                        }
                    }

                    // If stop != null, then stop == start!
                    if (stop != null) {

                        // Make sure we are 3 segments long for a proper polygon.
                        if (start.next.next != start) {
                            final LinkedList.Node<Point> newStartNode = new LinkedList.Node<>(start.value, start.next, start.color);

                            // The start node is now the end node.
                            start.next = null;

                            final Feature feature = new Feature(newStartNode);

                            vectorTile.features.add(feature);
                        }
                    }
                }
            }
        }
    }

    private LinkedList.LabeledNode<Point> buildPointLineNode(double x, double y, byte side) {
        switch (side) {
            case 0:
                return new LinkedList.LabeledNode(new Point(x + 0.5, y, side), null);
            case 1:
                return new LinkedList.LabeledNode(new Point(x + 1.0, y+0.5, side), null);
            case 2:
                return new LinkedList.LabeledNode(new Point(x + 0.5, y+1.0, side), null);
            case 3:
                return new LinkedList.LabeledNode(new Point(x, y+0.5, side), null);
            default:
                throw new IllegalStateException("Side value must be 0, 1, 2, or 3 for nw, ne, se, or sw.");
        }
    }

    /**
     * Given an (x, y) index into the data array this will conditionally add {@link Side}
     * objects to the top, right, bottom, or left side lists.
     *
     * @param x Index into the x dimension.
     * @param y Index into the y dimension. Recall that y=0 is the top of the raster range.
     */
    private void addSides(final int x, final int y) {

        // A holder in case we have a side.
        if (x == 0) {
            // On the left side.
            vectorTile.left.add(new Side(tile.tile[x + y * tile.width]));
            if (y == 0) {
                // Also on the top!
                vectorTile.top.add(new Side(tile.tile[x + y * tile.width]));
            }
            else if (y == HEIGHT-1) {
                // Also on the bottom!
                vectorTile.bottom.add(new Side(tile.tile[x + y * tile.width]));
            }
        }
        else if (x == WIDTH-1) {
            // +1 because, recall, there are -1 contours than squares.
            vectorTile.right.add(new Side(tile.tile[x+y * tile.width + 1]));
            if (y == 0) {
                // Also on the top!
                vectorTile.top.add(new Side(tile.tile[x + y * tile.width]));
            }
            else if (y == HEIGHT-1) {
                // Also on the bottom!
                vectorTile.bottom.add(new Side(tile.tile[x + y * tile.width]));
            }
        }
        else if (y == 0) {
            // Only on the top.
            // +1 because, recall, there are -1 contours than squares.
            vectorTile.top.add(new Side(tile.tile[x + y * tile.width]));
        }
        else if (y == HEIGHT-1) {
            // Only on the bottom.
            // +1 because, recall, there are -1 contours than squares.
            vectorTile.bottom.add(new Side(tile.tile[x+y * tile.width + 1]));
        }
    }

    /**
     * Search all begin nodes and return the one with the matching side.
     * @param side The side to find a node for.
     * @param beginNodes The begin node.
     * @return The begin node found or null if none.
     */
    private LinkedList.Node<Point> findBegin(final byte side, final LinkedList.Node<Point>[] beginNodes) {
        for (int i = 0; i < beginNodes.length; i++) {
            if (beginNodes[i].value.side == side) {
                return beginNodes[i];
            }
        }

        return null;
    }

    /**
     * Search all begin nodes next point, which is the end node, and return the one with the matching side.
     * @param side The side to find a node for.
     * @param beginNodes The begin node.
     * @return The end node found or null if none.
     */
    private LinkedList.Node<Point> findEnd(final byte side, final LinkedList.Node<Point>[] beginNodes) {
        for (int i = 0; i < beginNodes.length; i++) {
            if (beginNodes[i].next != null) {
                if (beginNodes[i].next.value.side == side) {
                    return beginNodes[i].next;
                }
            }
        }

        return null;
    }
}
