package com.github.basking2.sdsai.marchinesquares;

public class SimpleGeoJson {
    public static String write(final VectorTile tile, double height, double width) {

        final StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\"type\": \"FeatureCollection\",\n");
        sb.append("\"features\": [\n");

        for (final Feature f : tile.features) {
            sb.append("{\n");
            sb.append("\"type\": \"Feature\",\n");
            sb.append("\"properties\": {},\n");
            sb.append("\"geometry\": {\n");
            sb.append("\"type\": \"Polygon\",\n");

            sb.append("\"coordinates\": [ [ \n ");
            for (final Point p : f.points) {
                double x = p.x * 360f / width - 180f;
                double y = -(p.y * 180f / height - 90f);
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

        double width = dims[0];
        double height = dims[1];

        return write(tile, height, width);
    }
}
