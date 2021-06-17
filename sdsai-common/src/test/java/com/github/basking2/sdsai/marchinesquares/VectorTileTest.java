package com.github.basking2.sdsai.marchinesquares;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

public class VectorTileTest {
    @Test
    public void testCollateHoles() throws IOException {
        final VectorTile vt = new VectorTile();

        LinkedList.Node<Point> poly = new LinkedList.Node<>(new Point(10, 10, (byte) 0), null);
        poly = new LinkedList.Node<>(new Point(10, -10, (byte) 0), poly);
        poly = new LinkedList.Node<>(new Point(-10, -10, (byte) 0), poly);
        poly = new LinkedList.Node<>(new Point(-10, 10, (byte) 0), poly);
        poly = new LinkedList.Node<>(new Point(10, 10, (byte) 0), poly);

        LinkedList.Node<Point> hole = new LinkedList.Node<>(new Point(5, 5, (byte) 0), null);
        hole = new LinkedList.Node<>(new Point(-5, 5, (byte) 0), hole);
        hole = new LinkedList.Node<>(new Point(-5, -5, (byte) 0), hole);
        hole = new LinkedList.Node<>(new Point(5, -5, (byte) 0), hole);
        hole = new LinkedList.Node<>(new Point(5, 5, (byte) 0), hole);

        vt.features.add(new Feature(poly));
        vt.features.add(new Feature(hole));

        vt.collateHoles();

        assertEquals(1, vt.features.size());
        assertEquals(1, vt.features.getHead().holes.size());

        final String geoJson = SimpleGeoJson.write(vt, new SimpleGeoJson.LinearProportionalGridToWorld(10, 10, 20, 20));

        try (final OutputStream os = new FileOutputStream("build/"+getClass().getSimpleName()  + ".geojson")) {
            os.write(geoJson.getBytes("UTF-8"));
        }
    }
}
