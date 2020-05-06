package com.github.basking2.sdsai.marchinesquares;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    public void basicBuild2() throws IOException {
        final VectorTile[][] map = new VectorTile[][]{
                {buildTile(10, 10), buildTile(10, 10), buildTile(10, 10)},
                {buildTile(10, 10), buildTile(10, 10), buildTile(10, 10)},
                {buildTile(10, 10), buildTile(10, 10), buildTile(10, 10)},
        };

        final VectorTileGroup vectorTileGroup = new VectorTileGroup();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                vectorTileGroup.addEast(map[i][j]);
            }
            vectorTileGroup.addNewRow();
        }

        final int[] dims = new int[]{ 0, 0 };

        vectorTileGroup.getVectorTile().features.forEach(f -> {
            f.points.forEach(p -> {
                if (p.x > dims[0]) {
                    dims[0] = p.x;
                }
                 if (p.y > dims[1]) {
                     dims[1] = p.y;
                 }

            });
        });

        int width = dims[0];
        int height = dims[1];

        final StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\"type\": \"FeatureCollection\",\n");
        sb.append("\"features\": [\n");

        for (final Feature f: vectorTileGroup.getVectorTile().features) {
            sb.append("{\n");
            sb.append("\"type\": \"Feature\",\n");
            sb.append("\"properties\": {},\n");
            sb.append("\"geometry\": {\n");
            sb.append("\"type\": \"Polygon\",\n");

            sb.append("\"coordinates\": [ [ \n ");
            for (final Point p : f.points) {
                float x = p.x * 360 / width - 180;
                float y = p.y * 180 / height - 90;
                if (!Float.isNaN(x) && !Float.isNaN(y)) {
                    sb.append("[")
                            .append(x)
                            .append(",")
                            .append(y)
                            .append("],\n");
                }
            }
            sb.setCharAt(sb.length()-2, ' ');
            sb.append("] ]\n");

            sb.append("}\n");
            sb.append("},\n");
        }
        sb.setCharAt(sb.length()-2, ' ');

        sb.append("]\n");
        sb.append("}\n");

        try (final OutputStream os = new FileOutputStream(getClass().getSimpleName()  + ".geojson")) {
            os.write(sb.toString().getBytes("UTF-8"));
        }
    }
}
