package com.github.basking2.sdsai.marchinesquares;

import org.junit.Assert;
import org.junit.Test;

public class VectorTileBuilderTest {
    @Test
    public void basicBuild(){
        final int width = 256;
        final int height = 256;
        final byte[] array = new byte[height * width];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte)((Math.random()*100 % 3) - 2);
        }

        final Tile t = new Tile(array, width);

        t.isoband();

        final VectorTileBuilder vtb = new VectorTileBuilder(t);

        final VectorTile vectorTile = vtb.build();

        Assert.assertTrue(vectorTile.bottom.size() == width);
        Assert.assertTrue(vectorTile.top.size() == width);
        Assert.assertTrue(vectorTile.right.size() == height);
        Assert.assertTrue(vectorTile.left.size() == height);

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

        Assert.assertEquals("(2->1)(1->2)", t.contours[0].toString());
        Assert.assertEquals("(2->3)(3->2)", t.contours[1].toString());
        Assert.assertEquals("(1->0)(0->1)", t.contours[2].toString());
        Assert.assertEquals("(0->3)(3->0)", t.contours[3].toString());

        final VectorTileBuilder vtb = new VectorTileBuilder(t);
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

        Assert.assertEquals("(1->2)", t.contours[0].toString());
        Assert.assertEquals("(2->3)", t.contours[1].toString());
        Assert.assertEquals("(0->1)", t.contours[2].toString());
        Assert.assertEquals("(3->0)", t.contours[3].toString());

        final VectorTileBuilder vtb = new VectorTileBuilder(t);
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

        Assert.assertEquals("(2->1)", t.contours[0].toString());
        Assert.assertEquals("(3->2)", t.contours[1].toString());
        Assert.assertEquals("(1->0)", t.contours[2].toString());
        Assert.assertEquals("(0->3)", t.contours[3].toString());

         final VectorTileBuilder vtb = new VectorTileBuilder(t);
         final VectorTile vectorTile = vtb.build();

    }
}
