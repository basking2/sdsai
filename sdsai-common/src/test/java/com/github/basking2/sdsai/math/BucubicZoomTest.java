/**
 * Copyright (c) 2016-2023 Sam Baskinger
 */

package com.github.basking2.sdsai.math;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BucubicZoomTest {

    @Test
    public void testStripesZoomIn()  throws Exception {

        final double[] imageIn = new double[256*256];
        final double[] imageOut = new double[256*256];

        // Color pixel.
        for ( int y = 0; y < 256; ++y) {
            for (int x = 0; x < 256; ++x) {
                final int idx = x + y * 256;

                if (x%2==0) {
                    imageIn[idx] = 0;
                } else {
                    imageIn[idx] = 255;
                }
            }
        }

        new BicubicZoom().zoom(
                imageIn,
                (256/4)*3, // right quarter
                (256/4)*3, // bottom quarter
                (256/4),   // 1/4 width
                (256/4),   // 1/4 height
                256,       // native image is still 256 pixels wide.

                imageOut, 0, 0, 256, 256, 256);


        writeImage("build/imageIn", imageIn);
        writeImage("build/imageOut", imageOut);

    }

    /**
     * Simple method to write an image sequence whose values are not outside the range of 1 byte (255).
     *
     * @param name
     * @throws IOException
     */
    private void writeImage(final String name, final double[] data) throws IOException {
        final BufferedImage bi = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < 256; ++y) {
            for (int x = 0; x < 256; ++x) {
                int v = (int)data[x+y*256];
                if (v > 0) {
                    //bi.setRGB(x, y, ((v<<16) | (v << 8) | v));
                    bi.setRGB(x, y, ((v<<16)));
                }
            }
        }


        ImageIO.write(bi,"png", new File(name+".png"));
    }
}
