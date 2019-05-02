package com.github.basking2.sdsai.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * A class that reads and writes over a ring of files.
 *
 * When the ring is overwrite pre-existing data, the entire file is truncated and writing restarts.
 *
 * Records written do not span files.
 */
public class FileRingOutputStream extends OutputStream {

    private Logger LOG = LoggerFactory.getLogger(FileRingOutputStream.class);

    private final File dir;
    private final int ringSize;

    private int num;
    private OutputStream out;

    private final String prefix;
    private final String suffix;

    private final Predicate<FileRingOutputStream> doRotation;

    private final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * Create a new file ring with a start file of 0.
     *
     * @param dir Parent directory.
     * @param ringSize How big is the ring.
     * @param doRotation How do we determine we should rotate the file size.
     * @throws FileNotFoundException On errors.
     */
    public FileRingOutputStream(
            final File dir,
            final int ringSize,
            final Predicate<FileRingOutputStream> doRotation
    ) throws IOException {
        this(dir, "", "", ringSize, doRotation);
    }

    public FileRingOutputStream(
            final File dir,
            final String prefix,
            final String suffix,
            final int ringSize,
            final Predicate<FileRingOutputStream> doRotation
            ) throws IOException {

        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.dir = dir;
        this.ringSize = ringSize;
        this.doRotation = doRotation;
        this.prefix = prefix;
        this.suffix = suffix;

        this.num = getCurrentFileNumberAndSize(dir, prefix, suffix)[0];

        final File f = getFile(this.dir, this.prefix, this.suffix, this.num);

        this.out = new FileOutputStream(f, true);
    }

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
     * Set the file to start appending to in the ring.
     *
     * @param start The starting point.
     * @throws IOException On an error.
     */
    public void setRingStart(final int start) throws IOException {
        if (start >= ringSize) {
            throw new IOException("Start must be less than ringSize.");
        }

        this.num = start;
        final File f = getFile(dir, prefix, suffix, num);
        this.out = new FileOutputStream(f, true);

        try (final FileOutputStream s = new FileOutputStream(getMetaFile(dir, prefix, suffix), false)) {
            s.write(String.format("%d\n", num).getBytes(CHARSET));
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

    public int getCurrentFileNumber() {
        return num;
    }

    public int getRingSize() {
        return ringSize;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);

        if (doRotation.test(this)) {
            rotateFile();
        }
    }

    @Override
    public void write(final byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    @Override
    public void write(final byte[] data, final int offset, final int length) throws IOException {
        out.write(data, offset, length);

        if (doRotation.test(this)) {
            rotateFile();
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    private void rotateFile() throws IOException {
        out.close();
        num = (num + 1) % ringSize;
        final File f = getFile(dir, prefix, suffix, num);
        out = new FileOutputStream(f, false);

        // Write meta.
        try (final FileOutputStream s = new FileOutputStream(getMetaFile(dir, prefix, suffix), false)) {
            s.write(String.format("%d\n%d\n", num, ringSize).getBytes(CHARSET));
        }
    }

    public static File getFile(final File dir, final String prefix, final String suffix, final int num) {
        return new File(dir, String.format("%s%08d%s", prefix, num, suffix));
    }

    public static File getMetaFile(final File dir, final String prefix, final String suffix) {
        return new File(dir, String.format("%smeta%s", prefix, suffix));
    }

    public void deleteAll() {
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

    @Override
    public void close() throws IOException {
        out.close();
    }

    public File getDir() {
        return dir;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public static class RotateAfterWrites implements Predicate<FileRingOutputStream>  {

        private int count = 0;
        private final int rotate;

        public RotateAfterWrites(final int rotate) {
            this.rotate = rotate;
        }

        @Override
        public boolean test(FileRingOutputStream fileRing) {
            count++;
            if (count >= rotate) {
                count = 0;
                return true;
            }
            else {
                return false;
            }
        }
    }
}
