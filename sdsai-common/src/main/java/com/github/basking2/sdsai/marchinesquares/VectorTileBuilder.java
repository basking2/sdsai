package com.github.basking2.sdsai.marchinesquares;

import static com.github.basking2.sdsai.marchinesquares.Colors.COLLECT_COLOR;
import static com.github.basking2.sdsai.marchinesquares.Colors.DEFAULT_COLOR;

public class VectorTileBuilder {

    private final Tile tile;
    private final int HEIGHT;
    private final int WIDTH;
    final VectorTile vectorTile;

    private final boolean DEBUG = false;

    private void p(Object ... s) {
        if (DEBUG) {
            if (s.length == 1) {
                System.out.println(s[0].toString());
            } else {
                for (final Object t : s) {
                    System.out.print(t.toString());
                }
                System.out.println("");
            }
        }
    }

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

                    p(String.format("Processing %d, %d line %d.", x, y, j));
                    p(String.format("  Cells %d %d %d %d",
                            tile.tile[x + y * tile.width],
                            tile.tile[x + y * tile.width + 1],
                            tile.tile[x + y * tile.width + 1 + tile.width],
                            tile.tile[x + y * tile.width + tile.width]
                    ));
                    assert featureField[y][x][j] == null;

                    //-------------------------------------------------------------------------
                    // - lineBegin The side of the square the line begins on. The tail. Sides are 0 is the top,
                    //             1 is the right, 2 is the bottom, and 3 is the left side.
                    // - lineEnd The side of the square the line ends on. The head. Sides are 0 is the top,
                    //           1 is the right, 2 is the bottom, and 3 is the left side.
                    //-------------------------------------------------------------------------
                    final byte lineBegin = tile.contours[i].lines[j * 2];
                    final byte lineEnd = tile.contours[i].lines[j * 2 + 1];

                    // Choose the points.
                    // Points on the top and left are fetched from previous nodes (unless we are on a side).
                    // Points on the bottom and right are created.
                    final LinkedList.Node<Point> pBegin;
                    final LinkedList.Node<Point> pEnd;

                    //-------------------------------------------------------------------------
                    // At here the [y][x] has a line in it. We should stitch it to
                    // cells above and below.
                    //-------------------------------------------------------------------------
                    switch (lineEnd) {
                        case 0:
                            if (y==0) {
                                // No cell above. Create.
                                p("  Build lineEnd ", lineEnd);
                                pEnd = buildPointLineNode(x, y, lineEnd);
                                vectorTile.top.getTail().endPoint = pEnd;
                            }
                            else {
                                // A cell above. Fetch.
                                p("  Find lineEnd ", lineEnd);
                                pEnd = findBegin(oppositeSide(lineEnd), featureField[y-1][x]);
                                pEnd.value.side = lineEnd;
                            }
                            break;
                        case 1:
                            // Not visited node. Create.
                            p("  Build lineEnd ", lineEnd);
                            pEnd = buildPointLineNode(x, y, lineEnd);
                            if (x == WIDTH - 1) {
                                vectorTile.right.getTail().endPoint = pEnd;
                            }
                            break;
                        case 2:
                            // Not visited node. Create.
                            p("  Build lineEnd ", lineEnd);
                            pEnd = buildPointLineNode(x, y, lineEnd);
                            if (y == HEIGHT - 1) {
                                vectorTile.bottom.getTail().endPoint = pEnd;
                            }
                            break;
                        case 3:
                            if (x == 0) {
                                p("  Build lineEnd ", lineEnd);
                                pEnd = buildPointLineNode(x, y, lineEnd);
                                vectorTile.left.getTail().endPoint = pEnd;
                            }
                            else {
                                p("  Find lineEnd ", lineEnd);
                                pEnd = findBegin(oppositeSide(lineEnd), featureField[y][x-1]);
                                pEnd.value.side = lineEnd;
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unhandled edge value "+lineEnd);
                    }

                    switch (lineBegin) {
                        case 0:
                            if (y==0) {
                                p("  Build lineBegin ", lineBegin);
                                pBegin = buildPointLineNode(x, y, lineBegin);
                                vectorTile.top.getTail().beginPoint = pBegin;
                            }
                            else {
                                p("  Find lineBegin ", lineBegin);
                                pBegin = findEnd(oppositeSide(lineBegin), featureField[y-1][x]);
                                pBegin.value.side = lineBegin;
                            }
                            break;
                        case 1:
                            p("  Build lineBegin ", lineBegin);
                            pBegin = buildPointLineNode(x, y, lineBegin);
                            if (x == WIDTH - 1) {
                                vectorTile.right.getTail().beginPoint = pBegin;
                            }
                            break;
                        case 2:
                            p("  Build lineBegin ", lineBegin);
                            pBegin = buildPointLineNode(x, y, lineBegin);
                            if (y == HEIGHT - 1) {
                                vectorTile.bottom.getTail().beginPoint = pBegin;
                            }
                            break;
                        case 3:
                            if (x == 0) {
                                p("  Build lineBegin ", lineBegin);
                                pBegin = buildPointLineNode(x, y, lineBegin);
                                vectorTile.left.getTail().beginPoint = pBegin;
                            }
                            else {
                                p("  Find lineBegin ", lineBegin);
                                pBegin = findEnd(oppositeSide(lineBegin), featureField[y][x-1]);
                                pBegin.value.side = lineBegin;
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unhandled edge value "+lineBegin);
                    }

                    // Link the pBegin to the pEnd.
                    assert pBegin.next == null;
                    pBegin.next = pEnd;

                    // Store the beginning, even if we copy it from another cell.
                    featureField[y][x][j] = pBegin;

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

                // Walk all the line beginnings in the cell.
                nextField: for (int j = 0; j < featureField[y][x].length; j++) {

                    assert featureField[y][x][j] != null;

                    final LinkedList.Node<Point> start = featureField[y][x][j];
                    LinkedList.Node<Point> stop = start.next;
                    start.color = COLLECT_COLOR;
                    // Exit this loop in 3 conditions.
                    // 1. We reach start again.
                    // 2. We reach COLLECT_COLOR nodes against, we are already linked.
                    // 3. We dead-end and are not a polygon (yet).
                    while (start != stop && stop != null) {
                        if (stop.color == COLLECT_COLOR) {
                            break nextField;
                        }
                        else {
                            stop.color = COLLECT_COLOR;
                            stop = stop.next;
                        }
                    }

                    // If stop != null, then stop == start!
                    if (stop != null) {

                        // Make sure we are 3 segments long for a proper polygon.
                        if (start.next.next != start) {
                            final LinkedList.Node<Point> newStartNode = new LinkedList.Node<>(new Point(start.value), start.next, start.color);

                            // The start node is now the end node.
                            start.next = null;

                            final Feature feature = new Feature(newStartNode);

                            vectorTile.features.add(feature);
                        }
                    }
                    else {
                        // We dead-ended. Uncolor the nodes we set.
                        stop = start;
                        while (stop != null) {
                            stop.color = DEFAULT_COLOR;
                            stop = stop.next;
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
                vectorTile.bottom.add(new Side(tile.tile[x + (y+1) * tile.width]));
            }
        }
        else if (x == WIDTH-1) {
            // +1 because, recall, there are -1 contours than squares.
            vectorTile.right.add(new Side(tile.tile[x + y * tile.width + 1]));
            if (y == 0) {
                // Also on the top!
                vectorTile.top.add(new Side(tile.tile[x + y * tile.width]));
            }
            else if (y == HEIGHT-1) {
                // Also on the bottom!
                vectorTile.bottom.add(new Side(tile.tile[x + (y+1) * tile.width]));
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
            vectorTile.bottom.add(new Side(tile.tile[x + (y+1) * tile.width]));
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

    private byte oppositeSide(final byte side) {
        switch (side) {
            case 0:
                return 2;
            case 1:
                return 3;
            case 2:
                return 0;
            case 3:
                return 1;
            default:
                throw new IllegalArgumentException("Unsupported byte values "+side);
        }
    }
}
