package com.github.basking2.sdsai.marchinesquares;

import org.junit.Assert;
import org.junit.Test;

public class TileTest {

    @Test
    public void testIsobandBuild() {
        final int width = 256;
        final int height = 256;
        final byte[] array = new byte[height * width];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte)((Math.random()*100 % 3) - 2);
        }

        final Tile t = new Tile(array, width);

        t.isoband();

        // Bottom line is null.
        for (int w = 0; w < width; w++) {
            Assert.assertNull(t.contours[t.contours.length - width]);
        }

        // Right line is null.
        for (int h = 0; h < height; h++) {
            Assert.assertNull(t.contours[width*h+width-1]);
        }

        // Everything else is not-null.
        for (int h = 0; h < height-1; h++) {
            for ( int w = 0; w < width-1; w++) {
                final int i = h*width+w;
                Assert.assertNotNull(t.contours[i]);
                Assert.assertEquals(t.contours[i].lineCount, t.contours[i].lines.length/2);
            }
        }
    }
}
