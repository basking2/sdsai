package com.github.basking2.sdsai.marchinesquares;

import org.junit.Assert;
import org.junit.Test;

public class VectorTileGroupTest {

    private VectorTile buildTile(int height, int width) {
        final byte[] array = new byte[height * width];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte)((Math.random()*100 % 3) - 2);
        }

        final Tile t = new Tile(array, width);

        t.isoband();

        final VectorTileBuilder vtb = new VectorTileBuilder(t);

        final VectorTile vectorTile = vtb.build();
        return vectorTile;
    }

    @Test
    public void basicBuild(){
        final VectorTile[][] map = new VectorTile[][]{
                {buildTile(100, 100), buildTile(100, 100), buildTile(100, 100)},
                {buildTile(100, 100), buildTile(100, 100), buildTile(100, 100)},
                {buildTile(100, 100), buildTile(100, 100), buildTile(100, 100)},
        };

        final VectorTileGroup vectorTileGroup = new VectorTileGroup();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                vectorTileGroup.addEast(map[i][j]);
            }
            vectorTileGroup.addNewRow();
        }

    }

    @Test
    public void basicBuild2() {
        final VectorTile[][] map = new VectorTile[][]{
                {buildTile(100, 100), buildTile(100, 100), buildTile(100, 100)},
                {buildTile(100, 100), buildTile(100, 100), buildTile(100, 100)},
                {buildTile(100, 100), buildTile(100, 100), buildTile(100, 100)},
        };

        final VectorTileGroup vectorTileGroup = new VectorTileGroup();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                vectorTileGroup.addEast(map[i][j]);
            }
            vectorTileGroup.addNewRow();
        }

    }
}
