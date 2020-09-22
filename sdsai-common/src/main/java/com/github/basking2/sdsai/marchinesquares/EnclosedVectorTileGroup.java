package com.github.basking2.sdsai.marchinesquares;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This performs much the same function as the {@link VectorTileGroup} but surrounding the shape with a constant field.
 *
 * The effect is that all edges should be closed.
 *
 */
public class EnclosedVectorTileGroup implements Closeable {
    private static byte field;

    private static VectorTileGroup vectorTileGroup;

    boolean topRow;
    boolean leftCol;
    int lastTileHeight;
    ArrayList<Integer> northernWidths = new ArrayList<>();

    public EnclosedVectorTileGroup(final byte field, final FeatureFactory featureFactory){
        this.field = field;
        this.topRow = true;
        this.leftCol = true;
        this.vectorTileGroup = new VectorTileGroup(featureFactory);
        this.vectorTileGroup.setNorthWestPoint(new Side(field));
    }

    public void addEast(final VectorTile east) {

        if (leftCol) {
            // Heading east, we move off the left column.
            leftCol = false;

            // When we add the first tiler in a new row, clear the width list.
            northernWidths = new ArrayList<>(northernWidths.size());

            // Make the out-of-bounds tile that should exit to our east already.
            final VectorTile left = new VectorTile();
            for (int i = 0; i < east.left.size(); i++) {
                left.right.add(new Side(field));
                left.left.add(new Side(field));
            }
            left.top.add(new Side(field));
            left.top.add(new Side(field));
            left.bottom.add(new Side(field));
            left.bottom.add(new Side(field));
            vectorTileGroup.addEast(left);

        }

        if (topRow) {
            // If this is the top row, we must add an uppder level tile.
            final VectorTile top = new VectorTile();
            for (int i = 0; i < east.top.size(); i++) {
                top.bottom.add(new Side(field));
                top.top.add(new Side(field));
            }
            top.left.add(new Side(field));
            top.left.add(new Side(field));
            top.right.add(new Side(field));
            top.right.add(new Side(field));
            vectorTileGroup.setNorthTiles(Arrays.asList(top).iterator());
        }

        vectorTileGroup.addEast(east);

        // Every *user* tile, record the width for the close() operation.
        northernWidths.add(east.bottom.size());

        lastTileHeight = east.right.size();
    }

    public void addNewRow() {

        if (topRow) {
            topRow = false;

            // If we are on the top-row, add a tile to the upper-right must exist.
            final VectorTile top = new VectorTile();
            top.left.add(new Side(field));
            top.left.add(new Side(field));
            top.bottom.add(new Side(field));
            top.bottom.add(new Side(field));
            vectorTileGroup.setNorthTiles(Arrays.asList(top).iterator());
        }

        final VectorTile east = new VectorTile();
        for (int i = 0; i < lastTileHeight; i++) {
            east.left.add(new Side(field));
            east.right.add(new Side(field));
        }
        east.top.add(new Side(field));
        east.top.add(new Side(field));
        east.bottom.add(new Side(field));
        east.bottom.add(new Side(field));
        vectorTileGroup.addEast(east);

        leftCol = true;
        vectorTileGroup.addNewRow();
    }

    public void addNewRow(final VectorTile newTile) {
        addNewRow();
        addEast(newTile);
    }

    public VectorTile getVectorTile() {
        return vectorTileGroup.getVectorTile();
    }

    public void setStitchTiles(boolean b){
        vectorTileGroup.setStitchTiles(b);
    }

    /**
     * Close the bottom layer of this object.
     *
     * @throws Exception Not thrown.
     */
    @Override
    public void close() {
        addNewRow();

        for (int i: northernWidths) {
            final VectorTile bottom = new VectorTile();

            for (int w = 0; w < i; w++) {
                bottom.top.add(new Side(field));
                bottom.top.add(new Side(field));
                bottom.bottom.add(new Side(field));
                bottom.bottom.add(new Side(field));
            }

            bottom.left.add(new Side(field));
            bottom.left.add(new Side(field));
            bottom.right.add(new Side(field));
            bottom.right.add(new Side(field));

            addEast(bottom);
        }

        addNewRow();
    }
}
