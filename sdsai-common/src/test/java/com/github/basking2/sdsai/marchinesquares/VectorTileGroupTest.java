package com.github.basking2.sdsai.marchinesquares;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class VectorTileGroupTest {

    private VectorTile buildTile(int height, int width) {
        final byte[] array = new byte[height * width];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte)((int)(Math.random()*100 % 3) - 1);
        }

        final Tile t = new Tile(array, width);

        t.isoband();

        final VectorTileBuilder vtb = new VectorTileBuilder(t);

        final VectorTile vectorTile = vtb.build();
        return vectorTile;
    }

    private void checkVectorTile(final VectorTile vectorTile) {
        for (final Side s : vectorTile.top) {
            if (s.endPoint != null) {
                Assert.assertNull(s.endPoint.next);
            }
            if (s.beginPoint != null) {
                Assert.assertNotNull(s.beginPoint.next);
            }
        }
        for (final Side s : vectorTile.bottom) {
            if (s.endPoint != null) {
                Assert.assertNull(s.endPoint.next);
            }
            if (s.beginPoint != null) {
                Assert.assertNotNull(s.beginPoint.next);
            }
        }
        for (final Side s : vectorTile.left) {
            if (s.endPoint != null) {
                Assert.assertNull(s.endPoint.next);
            }
            if (s.beginPoint != null) {
                Assert.assertNotNull(s.beginPoint.next);
            }
        }
        for (final Side s : vectorTile.right) {
            if (s.endPoint != null) {
                Assert.assertNull(s.endPoint.next);
            }
            if (s.beginPoint != null) {
                Assert.assertNotNull(s.beginPoint.next);
            }
        }
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
                checkVectorTile(map[i][j]);
                vectorTileGroup.addEast(map[i][j]);
            }
            vectorTileGroup.addNewRow();
        }

    }

    @Test
    public void basicBuild2() throws IOException {
        final int sz = 30;
        final VectorTile[][] map = new VectorTile[][]{
                {buildTile(sz, sz), buildTile(sz, sz), buildTile(sz, sz)},
                {buildTile(sz, sz), buildTile(sz, sz), buildTile(sz, sz)},
                {buildTile(sz, sz), buildTile(sz, sz), buildTile(sz, sz)},
        };

        final VectorTileGroup vectorTileGroup = new VectorTileGroup();
        vectorTileGroup.setStitchTiles(true);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                checkVectorTile(map[i][j]);
                vectorTileGroup.addEast(map[i][j]);
            }
            vectorTileGroup.addNewRow();
        }

        final String geoJson = SimpleGeoJson.write(vectorTileGroup.getVectorTile());

        try (final OutputStream os = new FileOutputStream(getClass().getSimpleName()  + ".geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }

    @Test
    public void craftedBuild2x1() throws IOException {

        final byte p = 1;
        final byte n = -1;

        final Tile[] tiles = {
                new Tile(new byte[]{
                        0, 0, 0,
                        0, n, n,
                        0, n, p,
                        0, n, n,
                        0, 0, 0}, 3),
                new Tile(new byte[]{
                        0, 0, 0,
                        n, n, 0,
                        p, n, 0,
                        n, n, 0,
                        0, 0, 0}, 3)
        };

        final VectorTileGroup g = new VectorTileGroup();
        g.addEast(new VectorTileBuilder(tiles[0]).buildIsoband());
        g.addEast(new VectorTileBuilder(tiles[1]).buildIsoband());

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 5, 6);

        try (final OutputStream os = new FileOutputStream(getClass().getSimpleName()  + "2x1.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }

    @Test
    public void craftedBuild1x2() throws IOException {

        final byte p = 1;
        final byte n = -1;

        final Tile[] tiles = {
                new Tile(new byte[]{
                        0, 0, 0, 0, 0,
                        0, p, p, p, 0,
                        0, p, n, p, 0}, 5),
                new Tile(new byte[]{
                        0, p, n, p, 0,
                        0, p, p, p, 0,
                        0, 0, 0, 0, 0}, 5)
        };

        final VectorTileGroup g = new VectorTileGroup();
        g.addEast(new VectorTileBuilder(tiles[0]).buildIsoband());
        g.addNewRow();
        g.addEast(new VectorTileBuilder(tiles[1]).buildIsoband());

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 6, 5);

        try (final OutputStream os = new FileOutputStream(getClass().getSimpleName()  + "1x2.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }

    @Test
    public void craftedBuild2x2() throws IOException {

        final byte p = 1;
        final byte n = -1;

        final Tile[] tiles = {
                new Tile(new byte[]{
                        0, 0, 0,
                        0, 0, 0,
                        0, 0, p}, 3),
                new Tile(new byte[]{
                        0, 0, 0,
                        0, 0, 0,
                        p, 0, 0}, 3),
                new Tile(new byte[]{
                        0, 0, p,
                        0, 0, 0,
                        0, 0, 0}, 3),
                new Tile(new byte[]{
                        p, 0, 0,
                        0, 0, 0,
                        0, 0, 0}, 3)
        };

        final VectorTileGroup g = new VectorTileGroup();
        g.addEast(new VectorTileBuilder(tiles[0]).buildIsoband());
        g.addEast(new VectorTileBuilder(tiles[1]).buildIsoband());
        g.addNewRow();
        g.addEast(new VectorTileBuilder(tiles[2]).buildIsoband());
        g.addEast(new VectorTileBuilder(tiles[3]).buildIsoband());

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 6, 6);

        try (final OutputStream os = new FileOutputStream(getClass().getSimpleName()  + "2x2.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }
}
