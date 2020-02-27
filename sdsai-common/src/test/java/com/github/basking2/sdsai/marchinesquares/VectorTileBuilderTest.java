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

        Assert.assertTrue(vectorTile.unfinishedLinesBottom.size() < width);
        Assert.assertTrue(vectorTile.unfinishedLinesTop.size() < width);
        Assert.assertTrue(vectorTile.unfinishedLinesRight.size() < height);
        Assert.assertTrue(vectorTile.unfinishedLinesLeft.size() < height);

        int prev = 0;
        for (final PointAndCells p : vectorTile.unfinishedLinesLeft) {
            Assert.assertNotEquals(p.cell1, p.cell2);
            Assert.assertTrue(p.point.y >= prev);
            Assert.assertEquals(0, p.point.x);
            prev = p.point.y;

        }

        prev = 0;
        for (final PointAndCells p : vectorTile.unfinishedLinesRight) {
            Assert.assertNotEquals(p.cell1, p.cell2);
            Assert.assertTrue(p.point.y >= prev);
            Assert.assertEquals(width-2, p.point.x);
            prev = p.point.y;
        }

        prev = 0;
        for (final PointAndCells p : vectorTile.unfinishedLinesTop) {
            Assert.assertNotEquals(p.cell1, p.cell2);
            Assert.assertTrue(p.point.x >= prev);
            Assert.assertEquals(0, p.point.y);
            prev = p.point.x;
        }

        prev = 0;
        for (final PointAndCells p : vectorTile.unfinishedLinesBottom) {
            Assert.assertNotEquals(p.cell1, p.cell2);
            Assert.assertTrue(p.point.x >= prev);
            Assert.assertEquals(width-2, p.point.y);
            prev = p.point.x;
        }
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

        //final VectorTileBuilder vtb = new VectorTileBuilder(t);
        //final VectorTile vectorTile = vtb.build();
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

        //final VectorTileBuilder vtb = new VectorTileBuilder(t);
        //final VectorTile vectorTile = vtb.build();
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
