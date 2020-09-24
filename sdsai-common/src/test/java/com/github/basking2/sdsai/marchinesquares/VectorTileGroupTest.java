package com.github.basking2.sdsai.marchinesquares;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
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

        final VectorTileBuilder vtb = new VectorTileBuilder(t, FeatureFactory.uuidProperty());

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

        final VectorTileGroup vectorTileGroup = new VectorTileGroup(FeatureFactory.uuidProperty());

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

        final VectorTileGroup vectorTileGroup = new VectorTileGroup(FeatureFactory.uuidProperty());
        vectorTileGroup.setStitchTiles(true);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                checkVectorTile(map[i][j]);
                vectorTileGroup.addEast(map[i][j]);
            }
            vectorTileGroup.addNewRow();
        }

        final String geoJson = SimpleGeoJson.write(vectorTileGroup.getVectorTile());

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + ".geojson")) {
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

        final VectorTileGroup g = new VectorTileGroup(FeatureFactory.uuidProperty());
        g.addEast(new VectorTileBuilder(tiles[0], FeatureFactory.uuidProperty()).buildIsoband());
        g.addEast(new VectorTileBuilder(tiles[1], FeatureFactory.uuidProperty()).buildIsoband());

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 4, 5);

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + "2x1.geojson")) {
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

        final VectorTileGroup g = new VectorTileGroup(FeatureFactory.uuidProperty());
        g.addEast(new VectorTileBuilder(tiles[0], FeatureFactory.uuidProperty()).buildIsoband());
        g.addNewRow();
        g.addEast(new VectorTileBuilder(tiles[1], FeatureFactory.uuidProperty()).buildIsoband());

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 5, 4);

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + "1x2.geojson")) {
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

        final FeatureFactory featureFactory = FeatureFactory.uuidProperty();

        final VectorTileGroup g = new VectorTileGroup(featureFactory);
        g.addEast(new VectorTileBuilder(tiles[0], featureFactory).buildIsoband());
        g.addEast(new VectorTileBuilder(tiles[1], featureFactory).buildIsoband());
        g.addNewRow();
        g.addEast(new VectorTileBuilder(tiles[2], featureFactory).buildIsoband());
        g.addEast(new VectorTileBuilder(tiles[3], featureFactory).buildIsoband());

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 5, 5);

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + "2x2.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }

    @Test
    public void swizzle() throws IOException {

        final byte p = 1;
        final byte n = -1;

        final Tile[] tiles = {
                new Tile(new byte[]{
                        p, p, p,
                        p, p, 0,
                        p, p, p,
                        p, p, 0,
                        p, p, p}, 3),
                new Tile(new byte[]{
                        p, p, p,
                        p, p, p,
                        0, p, p,
                        p, p, p,
                        p, p, p}, 3)
        };

        final FeatureFactory featureFactory = FeatureFactory.uuidProperty();

        final VectorTileGroup g = new VectorTileGroup(featureFactory);
        g.addEast(new VectorTileBuilder(tiles[0], featureFactory).buildIsoband());
        g.addEast(new VectorTileBuilder(tiles[1], featureFactory).buildIsoband());

        for (final Point pt : g.getVectorTile().features.getHead().points) {
            System.out.println("P "+pt.x+ " "+pt.y);
        }

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 4, 5);

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + "swizzle.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }

    @Test
    public void testBorderingEmpty() throws IOException {
        final FeatureFactory featureFactory = FeatureFactory.uuidProperty();
        final VectorTileGroup g = new VectorTileGroup(featureFactory);
        final byte n = (byte)-1;
        final byte z = (byte) 0;
        final byte p = (byte) 1;

        final Tile[] tiles = {
                new Tile(new byte[]{z, z, z, z, z, z, z, z, z}, 3),
                new Tile(new byte[]{z, z, z, z, z, z, z, z, z}, 3),
                new Tile(new byte[]{z, z, z, z, z, z, z, z, z}, 3),

                new Tile(new byte[]{z, z, z, z, z, z, z, z, z}, 3),
                new Tile(new byte[]{p, p, p, p, p, p, p, p, p}, 3),
                new Tile(new byte[]{z, z, z, z, z, z, z, z, z}, 3),

                new Tile(new byte[]{z, z, z, z, z, z, z, z, z}, 3),
                new Tile(new byte[]{z, z, z, z, z, z, z, z, z}, 3),
                new Tile(new byte[]{z, z, z, z, z, z, z, z, z}, 3),
        };

        final VectorTile vectorTiles[] = new VectorTile[]{
                new VectorTileBuilder(tiles[0], featureFactory).buildIsoband(),
                new VectorTileBuilder(tiles[1], featureFactory).buildIsoband(),
                new VectorTileBuilder(tiles[2], featureFactory).buildIsoband(),
                new VectorTileBuilder(tiles[3], featureFactory).buildIsoband(),
                new VectorTileBuilder(tiles[4], featureFactory).buildIsoband(),
                new VectorTileBuilder(tiles[5], featureFactory).buildIsoband(),
                new VectorTileBuilder(tiles[6], featureFactory).buildIsoband(),
                new VectorTileBuilder(tiles[7], featureFactory).buildIsoband(),
                new VectorTileBuilder(tiles[8], featureFactory).buildIsoband()
        };

        g.addEast(vectorTiles[0]);
        g.addEast(vectorTiles[1]);
        g.addEast(vectorTiles[2]);

        g.addNewRow();

        g.addEast(vectorTiles[3]);
        g.addEast(vectorTiles[4]);
        g.addEast(vectorTiles[5]);

        g.addNewRow();

        g.addEast(vectorTiles[6]);
        g.addEast(vectorTiles[7]);
        g.addEast(vectorTiles[8]);

        for (int i = 0; i < vectorTiles.length; i++) {
            System.out.println(" ==== " + i + " ==== ");
            System.out.println("Top ");
            for (final Side s : vectorTiles[i].top) {
                System.out.print("\t");
                System.out.println(s.prettyPrint());
            }
            System.out.println("Bottom ");
            for (final Side s : vectorTiles[i].bottom) {
                System.out.print("\t");
                System.out.println(s.prettyPrint());
            }
            System.out.println("Left ");
            for (final Side s : vectorTiles[i].left) {
                System.out.print("\t");
                System.out.println(s.prettyPrint());
            }
            System.out.println("Right ");
            for (final Side s : vectorTiles[i].right) {
                System.out.print("\t");
                System.out.println(s.prettyPrint());
            }
        }

        Assert.assertTrue(g.getVectorTile().features.size() > 0);

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 9, 9);

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + "_border.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }

    @Test
    public void testBorderingEmpty2() throws IOException {
        final FeatureFactory featureFactory = FeatureFactory.uuidProperty();
        final VectorTileGroup g = new VectorTileGroup(featureFactory);
        final byte n = (byte)-1;
        final byte p = (byte) 1;

        g.addEast(VectorTileBuilder.buildConstantTile(n, 2,2));
        g.addEast(VectorTileBuilder.buildConstantTile(n, 2,3));
        g.addEast(VectorTileBuilder.buildConstantTile(n, 2,2));

        g.addNewRow();

        g.addEast(VectorTileBuilder.buildConstantTile(n, 3,2));
        g.addEast(VectorTileBuilder.buildConstantTile((byte)0, 3,3));
        g.addEast(VectorTileBuilder.buildConstantTile(n, 3,2));

        g.addNewRow();

        g.addEast(VectorTileBuilder.buildConstantTile(n, 2,2));
        g.addEast(VectorTileBuilder.buildConstantTile(n, 2,3));
        g.addEast(VectorTileBuilder.buildConstantTile(n, 2,2));

        Assert.assertTrue(g.getVectorTile().features.size() > 0);

        final String geoJson = SimpleGeoJson.write(g.getVectorTile(), 9, 9);

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + "_border2.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }
}
