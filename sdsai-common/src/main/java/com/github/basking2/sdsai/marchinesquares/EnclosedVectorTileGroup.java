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

    private final static int buffer = 2;

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


        if (topRow) {
            if (leftCol) {
                leftCol = false;
                northernWidths.clear();
                addCornerTile();
            }

            lastTileHeight = buffer;
            northernWidths.add(east.bottom.size());
            addHorizontalBorder(east.top.size());
            firstRow.add(east);
        }
        else if (leftCol) {
            leftCol = false;
            northernWidths.clear();
            lastTileHeight = east.right.size();
            addVerticalBorder(east.left.size());
            northernWidths.add(east.bottom.size());
            vectorTileGroup.addEast(east);
        }
        else {
            lastTileHeight = east.right.size();
            northernWidths.add(east.bottom.size());
            vectorTileGroup.addEast(east);
        }
    }

    private void addCornerTile() {
        vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, buffer, buffer));
    }

    private void addVerticalBorder(final int height) {
        vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, height, buffer));
    }

    private void addHorizontalBorder(final int width) {
        vectorTileGroup.addEast(VectorTileBuilder.buildConstantTile(cell, buffer, width));
    }

    public void addNewRow() {
        if (!leftCol) {
            // Only add a new row if the current row is not empty.

            if (topRow) {
                // When we go from the top row to the next row, fill in the borders.
                topRow = false;

                // Top-right corner.
                addCornerTile();

                // Next row.
                vectorTileGroup.addNewRow();

                // West-most tile.
                addVerticalBorder(firstRow.get(0).left.size());

                // Add the actual first row.
                for (final VectorTile vt : firstRow) {
                    vectorTileGroup.addEast(vt);
                }

                lastTileHeight = firstRow.get(firstRow.size()-1).right.size();

                // We are done with the buffered first row of tiles. Allow them to be garbage collected.
                firstRow.clear();
            }

            // Add the east-most border.
            addVerticalBorder(lastTileHeight);

            vectorTileGroup.addNewRow();

            leftCol = true;
        }
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

            addCornerTile();

            for (int i : northernWidths) {
                addHorizontalBorder(i);
            }

            addCornerTile();
        }
    }
}
