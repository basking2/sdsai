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

    }
}
