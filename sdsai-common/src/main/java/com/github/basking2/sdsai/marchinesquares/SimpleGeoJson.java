package com.github.basking2.sdsai.marchinesquares;

import java.util.Map;

/**
 * A very naive method to build a simple GeoJSON object.
 */
public class SimpleGeoJson {
    public static String write(final VectorTile tile, double maxHeight, double maxWidth) {

        final StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\"type\": \"FeatureCollection\",\n");
        sb.append("\"features\": [\n");

        for (final Feature f : tile.features) {
            sb.append("{\n");
            sb.append("\"type\": \"Feature\",\n");
            sb.append("\"properties\": {\n");
            appendProperties(sb, f.properties);
            sb.append("},\n");
            sb.append("\"geometry\": {\n");
            sb.append("\"type\": \"Polygon\",\n");

            sb.append("\"coordinates\": [ [ \n ");
            for (final Point p : f.points) {
                final double x = p.x * 360d / maxWidth - 180d;
                final double y = (maxHeight - p.y) * 180d / maxHeight - 90d;
                if (!Double.isNaN(x) && !Double.isNaN(y)) {
                    sb.append("[")
                            .append(x)
                            .append(",")
                            .append(y)
                            .append("],\n");
                }
            }
            sb.setCharAt(sb.length() - 2, ' ');
            sb.append("] ]\n");

            sb.append("}\n");
            sb.append("},\n");
        }
        sb.setCharAt(sb.length() - 2, ' ');

        sb.append("]\n");
        sb.append("}\n");

        return sb.toString();
    }

    public static String write(final VectorTile tile) {
        final double[] dims = new double[2];
        tile.features.forEach(f -> {
            f.points.forEach(p -> {
                if (p.x > dims[0]) {
                    dims[0] = p.x;
                }
                if (p.y > dims[1]) {
                    dims[1] = p.y;
                }

            });
        });

        double width = dims[0]-1;
        double height = dims[1]-1;

        return write(tile, height, width);
    }

    private static void appendProperties(final StringBuilder sb, final Map<? extends Object, Object> properties) {
        for (final Map.Entry<? extends Object, Object> entry : properties.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue();
            sb.append("\""+key+"\": ");
            if (value instanceof Map) {
                sb.append("{\n");
                appendProperties(sb, properties);
                sb.append("}\n");
            }
            else {
                sb.append('"').append(value).append("\",\n");
            }
            sb.setCharAt(sb.length() - 2, ' ');
        }

    }
}
