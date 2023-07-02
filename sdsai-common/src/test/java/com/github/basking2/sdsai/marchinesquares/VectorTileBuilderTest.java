/**
 * Copyright (c) 2020-2023 Sam Baskinger
 */

package com.github.basking2.sdsai.marchinesquares;

import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VectorTileBuilderTest {
    @Test
    public void basicBuild(){
        final int width = 256;
        final int height = 256;
        final byte[] array = new byte[height * width];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte)((int)(Math.random()*100 % 3) - 1);
        }

        final Tile t = new Tile(array, width);

        t.isoband();

        final VectorTileBuilder vtb = new VectorTileBuilder(t, FeatureFactory.uuidProperty());

        final VectorTile vectorTile = vtb.build();

        assertTrue(vectorTile.bottom.size() == width);
        assertTrue(vectorTile.top.size() == width);
        assertTrue(vectorTile.right.size() == height);
        assertTrue(vectorTile.left.size() == height);

    }

    @Test
    public void basicLoopBuild(){
        final byte[] array = new byte[]{
                1,  1,  1,
                1, -1,  1,
                1,  1,  1
        };

        final Tile t = new Tile(array, 3);

        t.isoband();

        assertEquals("(2->1)(1->2)", t.contours[0].toString());
        assertEquals("(2->3)(3->2)", t.contours[1].toString());
        assertEquals("(1->0)(0->1)", t.contours[2].toString());
        assertEquals("(0->3)(3->0)", t.contours[3].toString());

        final VectorTileBuilder vtb = new VectorTileBuilder(t, FeatureFactory.uuidProperty());
        final VectorTile vectorTile = vtb.build();
    }

    @Test
    public void basicLoopBuild2(){
        final byte[] array = new byte[]{
                1, 1, 1,
                1, 0, 1,
                1, 1, 1
        };

        final Tile t = new Tile(array, 3);

        t.isoband();

        assertEquals("(1->2)", t.contours[0].toString());
        assertEquals("(2->3)", t.contours[1].toString());
        assertEquals("(0->1)", t.contours[2].toString());
        assertEquals("(3->0)", t.contours[3].toString());

        final VectorTileBuilder vtb = new VectorTileBuilder(t, FeatureFactory.uuidProperty());
        final VectorTile vectorTile = vtb.build();
    }

    @Test
    public void basicLoopBuild3(){
        final byte[] array = new byte[]{
                0,  0,  0,
                0, -1,  0,
                0,  0,  0
        };

        final Tile t = new Tile(array, 3);

        t.isoband();

        assertEquals("(2->1)", t.contours[0].toString());
        assertEquals("(3->2)", t.contours[1].toString());
        assertEquals("(1->0)", t.contours[2].toString());
        assertEquals("(0->3)", t.contours[3].toString());

         final VectorTileBuilder vtb = new VectorTileBuilder(t, FeatureFactory.uuidProperty());
         final VectorTile vectorTile = vtb.build();

    }

    @Test
    public void testGeoJson() throws IOException {
        final int width = 256;
        final int height = 256;
        final byte[] array = new byte[height * width];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte)((int)(Math.random()*100 % 3) - 1);
        }

        final Tile t = new Tile(array, width);

        t.isoband();
        final VectorTile vectorTile = new VectorTileBuilder(t, FeatureFactory.uuidProperty()).build().collateHoles();

        final String geoJson = SimpleGeoJson.write(vectorTile);

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + ".geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }
    @Test
    public void craftedBuild() throws IOException {

        final byte p = 1;
        final byte n = -1;

        final Tile tile =
                new Tile(new byte[]{
                        0, 0, 0, 0, 0, 0,
                        0, n, n, n, n, 0,
                        0, n, p, p, n, 0,
                        0, n, p, p, n, 0,
                        0, n, n, n, n, 0,
                        0, 0, 0, 0, 0, 0,
                }, 6);

        final VectorTile vt = new VectorTileBuilder(tile, FeatureFactory.uuidProperty()).buildIsoband();
        vt.collateHoles();
        final String geoJson = SimpleGeoJson.write(vt, new SimpleGeoJson.LinearProportionalGridToWorld(5, 5));

        try (final OutputStream os = new FileOutputStream("build/" +getClass().getSimpleName()  + "2.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }

    @Test
    public void examineHoles() throws IOException {

        final byte p = 1;
        final byte n = -1;

        final Tile tile =
                new Tile(new byte[]{
                        n, n, n, n, n, n,
                        n, 0, 0, 0, 0, n,
                        n, 0, p, p, 0, n,
                        n, 0, p, p, 0, n,
                        n, 0, 0, 0, 0, n,
                        n, n, n, n, n, n,
                }, 6);

        final VectorTile vt = new VectorTileBuilder(tile, FeatureFactory.uuidProperty()).buildIsoband();
        vt.collateHoles();
        final String geoJson = SimpleGeoJson.write(vt, new SimpleGeoJson.LinearProportionalGridToWorld(5, 5));

        try (final OutputStream os = new FileOutputStream("build/" +getClass().getSimpleName()  + "3.geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }
}
