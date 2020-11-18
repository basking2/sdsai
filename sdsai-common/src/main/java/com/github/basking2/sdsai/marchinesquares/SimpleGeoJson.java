package com.github.basking2.sdsai.marchinesquares;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

/**
 * A very naive method to build a simple GeoJSON object.
 */
public class SimpleGeoJson {

    private static Charset utf8 = Charset.forName("UTF-8");
    public static String write(final VectorTile tile, final GridToWorld gridToWorld) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(tile, gridToWorld, baos);
        baos.close();

        return new String(baos.toByteArray(), utf8);

    }

    public static void write(final VectorTile tile, final GridToWorld gridToWorld, final OutputStream out) throws IOException {

        out.write("{\n".getBytes(utf8));
        out.write("\"type\": \"FeatureCollection\",\n".getBytes(utf8));
        out.write("\"features\": [\n".getBytes(utf8));

        writeFeatures(out, tile, gridToWorld);

        out.write("]\n".getBytes(utf8));
        out.write("}\n".getBytes(utf8));

    }

    private static void writeFeatures(final OutputStream out, final VectorTile tile, final GridToWorld gridToWorld) throws IOException {
        final Iterator<Feature> itr = tile.features.iterator();

        if (itr.hasNext()) {
            Feature f = itr.next();

            while (itr.hasNext()) {
                writeFeature(out, f, gridToWorld);
                out.write(",\n".getBytes(utf8));
                f = itr.next();
            }

            writeFeature(out, f, gridToWorld);
            out.write("\n".getBytes(utf8));
        }
    }

    private static void writeFeature(final OutputStream out, final Feature f, final GridToWorld gridToWorld) throws IOException {
        out.write("{\n".getBytes(utf8));
        out.write("\"type\": \"Feature\",\n".getBytes(utf8));
        out.write("\"properties\": {\n".getBytes(utf8));
        appendProperties(out, f.properties);
        out.write("},\n".getBytes(utf8));
        out.write("\"geometry\": {\n".getBytes(utf8));
        out.write("\"type\": \"Polygon\",\n".getBytes(utf8));

        out.write("\"coordinates\": [ [ \n ".getBytes(utf8));
        writePoints(out, f, gridToWorld);
        out.write("] ]\n".getBytes(utf8));

        out.write("}\n".getBytes(utf8));
        out.write("}".getBytes(utf8));
    }

    private static void writePoint(final OutputStream out, final Point p, final GridToWorld gridToWorld) throws IOException {
        final double[] coords = gridToWorld.convert(p);
        if (!Double.isNaN(coords[0]) && !Double.isNaN(coords[1])) {
            out.write("[".getBytes(utf8));
            out.write((coords[0]+"").getBytes(utf8));
            out.write(",".getBytes(utf8));
            out.write((coords[1]+"").getBytes(utf8));
            out.write("]".getBytes(utf8));
        }
    }

    private static void writePoints(final OutputStream out, final Feature f, final GridToWorld gridToWorld) throws IOException {
        final Iterator<Point> itr = f.points.iterator();

        if (itr.hasNext()) {
            Point p = itr.next();

            while (itr.hasNext()) {
                writePoint(out, p, gridToWorld);
                out.write(",\n".getBytes(utf8));
                p = itr.next();
            }

            writePoint(out, p, gridToWorld);
            out.write("\n".getBytes(utf8));
        }
    }

    public static String write(final VectorTile tile) throws IOException {
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

        final GridToWorld gridToWorld = new LinearProportionalGridToWorld(dims[0]-1, dims[1]-1);

        return write(tile, gridToWorld);
    }

    private static void appendProperty(final OutputStream out, final Map.Entry<?, Object> entry) throws IOException {
        // Write entry and fetch the next.
        final String key = entry.getKey().toString();
        final Object value = entry.getValue();

        out.write(("\""+key+"\": ").getBytes(utf8));
        if (value instanceof Map) {
            out.write("{\n".getBytes(utf8));
            appendProperties(out, (Map<?, Object>)value);
            out.write("}".getBytes(utf8));
        }
        else {
            out.write("\"".getBytes(utf8));
            out.write(value.toString().getBytes(utf8));
            out.write("\"".getBytes(utf8));
        }
    }

    private static void appendProperties(final OutputStream out, final Map<? extends Object, Object> properties) throws IOException {
        final Iterator<? extends Map.Entry<?, Object>> itr = properties.entrySet().iterator();
        if (itr.hasNext()) {
            Map.Entry<?, Object> entry = itr.next();

            while (itr.hasNext()) {

                appendProperty(out, entry);
                out.write(",\n".getBytes(utf8));

                entry = itr.next();
            }

            appendProperty(out, entry);
            out.write("\n".getBytes(utf8));
        }
    }

    /**
     * How to convert the X and Y values of the points in the {@link VectorTile} to a latitude and longitude.
     */
    @FunctionalInterface
    public interface GridToWorld {

        /**
         * Returns (x, y) as a two element array.
         *
         * @param p The point to convert {@link Point#x} and {@link Point#y} from.
         *
         * @return A two element array of the longitude and latitude.
         */
        double[] convert(Point p);
    }

    /**
     * A grid to world method that treats X and Y as being proportional to latitude and longitude.
     *
     * That is, if the range of x values is (-50, 50) then an X value of -50 would be -180, X=0 is 0 degrees, and
     * X=50 is 180.
     */
    public static class LinearProportionalGridToWorld implements GridToWorld {
        final double maxWidth;
        final double maxHeight;

        final double xOffset;
        final double yOffset;

        /**
         * @param xOffset Added to X before conversion.
         * @param yOffset Added to Y before converion.
         * @param maxWidth The size in the X dimension.
         * @param maxHeight The size in the Y dimension.
         */
        public LinearProportionalGridToWorld(final double xOffset, final double yOffset, final double maxWidth, final double maxHeight) {
            this.maxHeight = maxHeight;
            this.maxWidth = maxWidth;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        /**
         * Assume that xOffset and yOffset are both 0.
         *
         * @param maxWidth The size in the X dimension.
         * @param maxHeight The size in the Y dimension.
         */
        public LinearProportionalGridToWorld(final double maxWidth, final double maxHeight) {
            this(0, 0, maxWidth, maxHeight);
        }

        @Override
        public double[] convert(Point p) {
            final double x = (p.x + xOffset) * 360d / maxWidth - 180d;
            final double y = (maxHeight - (p.y + yOffset)) * 180d / maxHeight - 90d;
            return new double[]{ x, y};
        }
    }
}
