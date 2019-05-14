package com.github.basking2.sdsai.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A collection of static methods to help {@link FileRingInputStream} and {@link FileRingOutputStream} operate.
 *
 * This also acts as a parameter wrapper.
 *
 * A File Ring is a collection of N data files and one meta data file. The meta data file stores the value of N
 * and the last written to file number, from zero to N-1. When data is written to a File Ring, on each write
 * some predicate is evaluated to see if the next file should be written to. If true, then the current file value
 * is incremented and moduloed by N to produce the next file number. That file is truncated, the metadata file
 * is updated, and the new data file is written to.
 *
 * In this way a set of files in which N-1 are complete is maintained. This allows for fast append and scanning
 * read operations. The intended use is for an archive of limited size in which reads are rare and may take some time.
 */
public class FileRing {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static Logger LOG = LoggerFactory.getLogger(FileRing.class);

    private final File dir;
    private String prefix;
    private String suffix;

    public FileRing(final File dir, final String prefix, final String suffix) {
        this.dir = dir;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public FileRingOutputStream openForWriting(final int ringSize, final FileRingOutputStream.RotationPredicate doRotation) throws IOException {
        return new FileRingOutputStream(dir, prefix, suffix, ringSize, doRotation);
    }

    public FileRingOutputStream openForWriting(final FileRingOutputStream.RotationPredicate doRotation) throws IOException {
        int[] meta = getCurrentFileNumberAndSize(dir, prefix, suffix);

        if (meta[1] == 0) {
            throw new IOException("A ring size is required for creating a new file ring.");
        }

        return new FileRingOutputStream(dir, prefix, suffix, meta[1], doRotation);
    }

    public FileRingInputStream openForReading() throws IOException {
        return new FileRingInputStream(dir, prefix, suffix);
    }

    public FileRingInputStream openForReading(final int start, final int ringSize) throws IOException {
        return new FileRingInputStream(dir, prefix, suffix, start, ringSize);
    }

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
     * Return file in the ring that is currently being written to, according to the meta file.
     * @param dir The directory.
     * @param prefix The prefix.
     * @param suffix The suffix.
     * @return The file that is currently being written to.
     * @throws IOException On errors.
     */
    public static File getCurrentFile(final File dir, final String prefix, final String suffix) throws IOException {
        final int[] numAndSize = getCurrentFileNumberAndSize(dir, prefix, suffix);

        final int num;

        if (numAndSize[1] == 0) {
            num = 0;
        } else {
            num = numAndSize[0];
        }

        return getFile(dir, prefix, suffix, num);
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

    public static void delete(final File dir, final String prefix, final String suffix) throws IOException {
        final int[] nums = getCurrentFileNumberAndSize(dir, prefix, suffix);
        delete(dir, prefix, suffix, nums[0], nums[1]);
    }

    public static void delete(final File dir, final String prefix, final String suffix, final int num, final int ringSize) {
        for (final File f : list(dir, prefix, suffix, num, ringSize)) {
            try {
                if (f.exists()) {
                    f.delete();
                }
            } catch (final Throwable t) {
                LOG.error(String.format("Failed to delete file %s.", f.getAbsoluteFile()), t);
            }
        }

        final File meta = getMetaFile(dir, prefix, suffix);
        if (meta.exists()) {
            meta.delete();
        }
    }

}
