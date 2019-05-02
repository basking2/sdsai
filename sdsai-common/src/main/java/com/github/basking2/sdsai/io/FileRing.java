package com.github.basking2.sdsai.io;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A collection of static methods to help {@link FileRingInputStream} and {@link FileRingOutputStream} operate.
 */
public class FileRing {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * Build an iterator that produces {@link FileInputStream}. The built iterator is suitable for
     * passing to {@link ConcatinatedInputStream}.
     *
     * @param dir The directory that holds the files.
     * @param prefix The prefix.
     * @param suffix The suffix.
     * @param number The file to start reading at.
     * @param ringSize The ring size.
     * @return An iterator that lazily creates {@link FileInputStream} objects.
     */
    public static Iterator<FileInputStream> buildInputStreamIterator(
            final File dir,
            final String prefix,
            final String suffix,
            final int number,
            final int ringSize
    )
    {
        final Iterator<FileInputStream> inputStreams = new Iterator<FileInputStream>() {

            private int i = 0;
            final private File[] files = list(dir, prefix, suffix, number, ringSize);

            @Override
            public boolean hasNext() {
                // We know the last file exists because it is the newest.
                // Intermediate files may not exist, but the last one should.
                return i < files.length;
            }

            @Override
            public FileInputStream next() {
                try {
                    while (i < files.length) {
                        final File f = files[i++];
                        if (f.exists()) {
                            return new FileInputStream(f);
                        }
                    }

                    // The last file _should_ always exist, but if not, throw an exception.
                    throw new NoSuchElementException("Last file was not found: "+files[i-1].getAbsolutePath());

                } catch (final FileNotFoundException e) {
                    throw new NoSuchElementException(e.getMessage());
                }
            }
        };

        return inputStreams;
    }

    /**
     * Read the meta file from a file ring and return an array with the current file number and the ring size as elements.
     *
     * @param dir The directory holding the files.
     * @param prefix The prefix.
     * @param suffix The suffix.
     * @return An array of {@code [current_file, ring_size]} or {@code [0, 0]} if no meta file exists.
     *
     * @throws IOException On errors reading a meta file that exists.
     */
    public static int[] getCurrentFileNumberAndSize(final File dir, final String prefix, final String suffix) throws IOException {
        final File metaFile = getMetaFile(dir, prefix, suffix);
        if (metaFile.exists() && metaFile.isFile()) {
            try (final FileReader s = new FileReader(metaFile)) {
                final BufferedReader reader = new BufferedReader(s);
                final int currentNum = Integer.parseInt(reader.readLine());
                final int size = Integer.parseInt(reader.readLine());
                return new int[]{currentNum, size};
            }
        }
        else {
            return new int[]{0, 0};
        }
    }

    /**
     * Write the meta data file, those values returned by {@link #getCurrentFileNumberAndSize(File, String, String)}.
     *
     * @param dir The directory that holds data.
     * @param prefix The file prefix.
     * @param suffix The file suffix.
     * @param num The current file.
     * @param ringSize The ring size.
     *
     * @throws IOException On a writing error.
     */
    public static void writeMeta(final File dir, final String prefix, final String suffix, final int num, final int ringSize) throws IOException {
        // Write meta.
        try (final FileOutputStream s = new FileOutputStream(getMetaFile(dir, prefix, suffix), false)) {
            s.write(String.format("%d\n%d\n", num, ringSize).getBytes(CHARSET));
        }
    }

    /**
     * List the current files, starting with listened one.
     *
     * @param dir Directory.
     * @param prefix Prefix.
     * @param suffix Suffix.
     * @param number Number.
     * @param ringSize Ring size.
     * @return The list of file objects. Not all of these may exist.
     */
    public static File[] list(final File dir, final String prefix, final String suffix, final int number, final int ringSize) {
        final File[] files = new File[ringSize];

        for (int i = 0; i < ringSize; i++) {
            files[i] = getFile(dir, prefix, suffix, (i + number) % ringSize);
        }

        return files;
    }

    /**
     * Build the file name.
     * @param dir The directory that holds the data.
     * @param prefix The prefix.
     * @param suffix The suffix.
     * @param num The file number.
     * @return The file object.
     */
    public static File getFile(final File dir, final String prefix, final String suffix, final int num) {
        return new File(dir, String.format("%s%08d%s", prefix, num, suffix));
    }

    /**
     * Build the meta file name.
     * @param dir The directory that holds the data.
     * @param prefix The prefix.
     * @param suffix The suffix.
     * @return The file object.
     */
    public static File getMetaFile(final File dir, final String prefix, final String suffix) {
        return new File(dir, String.format("%smeta%s", prefix, suffix));
    }

}
