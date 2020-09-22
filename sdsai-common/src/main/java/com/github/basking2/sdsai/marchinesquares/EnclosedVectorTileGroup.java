package com.github.basking2.sdsai.marchinesquares;

import java.io.Closeable;
import java.util.ArrayList;

/**
 * This performs much the same function as the {@link VectorTileGroup} but surrounding the shape with a constant field.
 *
 * The effect is that all edges should be closed.
 *
 */
public class EnclosedVectorTileGroup implements Closeable {
    private final byte cell;

    private final VectorTileGroup vectorTileGroup;

    private final static int buffer = 3;

    boolean topRow;
    boolean leftCol;
    boolean closed;
    int lastTileHeight;

    final ArrayList<Integer> northernWidths;
    final ArrayList<VectorTile> firstRow;

    public EnclosedVectorTileGroup(final byte field, final FeatureFactory featureFactory){
        this.closed = false;
        this.cell = field;
        this.topRow = true;
        this.leftCol = true;
        this.vectorTileGroup = new VectorTileGroup(featureFactory);
        this.northernWidths = new ArrayList<>();
        this.firstRow = new ArrayList<>();

    }

    public void addEast(final VectorTile east) {

        northernWidths.add(east.bottom.size());

        if (topRow) {
            lastTileHeight = buffer;
            if (leftCol) {
                leftCol = false;
                vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, buffer, buffer));
            }

            vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, buffer, east.top.size()));
            firstRow.add(east);
        }
        else if (leftCol) {
            leftCol = false;
            lastTileHeight = east.right.size();
            vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, lastTileHeight, buffer));
            vectorTileGroup.addEast(east);
        }
        else {
            lastTileHeight = east.right.size();
            vectorTileGroup.addEast(east);
        }
    }

    public void addNewRow() {
        if (topRow) {
            topRow = false;
            // Top-right corner.
            vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, buffer, buffer));
            vectorTileGroup.addNewRow();

            // West-most tile.
            vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, lastTileHeight, buffer));
            for (final VectorTile vt: firstRow) {
                vectorTileGroup.addEast(vt);
            }
            vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, lastTileHeight, buffer));
            firstRow.clear();
        }

        vectorTileGroup.addNewRow();
        leftCol = true;
    }

    public void addNewRow(final VectorTile newTile) {
        addNewRow();
        addEast(newTile);
    }

    /**
     * Close and get the {@link VectorTile}.
     *
     * @return The {@link VectorTile}.
     */
    public VectorTile getVectorTile() {
        close();
        return vectorTileGroup.getVectorTile();
    }

    public void setStitchTiles(boolean b){
        vectorTileGroup.setStitchTiles(b);
    }

    /**
     * Close the bottom layer of this object.
     */
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            addNewRow();

            vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, buffer, buffer));

            for (int i : northernWidths) {
                vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, buffer, i));
            }

            vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, buffer, buffer));
        }
    }
}
