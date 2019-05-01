package com.github.basking2.sdsai.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
public class FileRing {

    private Logger LOG = LoggerFactory.getLogger(FileRing.class);

    private final File dir;
    private final int ringSize;

    private int num;
    private OutputStream out;

    private final Predicate<FileRing> doRotation;

    /**
     * Create a new file ring with a start file of 0.
     *
     * @param dir Parent directory.
     * @param ringSize How big is the ring.
     * @param doRotation How do we determine we should rotate the file size.
     * @throws FileNotFoundException On errors.
     */
    public FileRing(
            final File dir,
            final int ringSize,
            final Predicate<FileRing> doRotation
    ) throws FileNotFoundException {
        this(dir, 0, ringSize, doRotation);
    }

    public FileRing(
            final File dir,
            final int start,
            final int ringSize,
            final Predicate<FileRing> doRotation
            ) throws FileNotFoundException {

        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (start >= ringSize) {
            throw new IllegalArgumentException("Start must be less than ringSize.");
        }

        this.dir = dir;
        this.ringSize = ringSize;
        this.num = start;
        this.doRotation = doRotation;

        final File f = getFile(this.num);
        this.out = new FileOutputStream(f, true);
    }

    /**
     * List the current files, starting with oldest one in the ring.
     * @return the current files, starting with oldest one in the ring.
     */
    public File[] list() {
        final File[] files = new File[ringSize];

        for (int i = 0; i < ringSize; i++) {
            files[i] = getFile((i + 1 + num) % ringSize);
        }

        return files;
    }

    public void write(final byte[] data, final int offset, final int length) throws IOException {
        out.write(data, offset, length);

        if (doRotation.test(this)) {
            rotateFile();
        }
    }

    private void rotateFile() throws IOException {
        out.close();
        num = (num + 1) % ringSize;
        final File f = getFile(num);
        out = new FileOutputStream(f, false);
    }

    /**
     * Build a new file name.
     * @param num The number of the file.
     * @return The file.
     */
    private File getFile(final int num) {
        return new File(dir, String.format("%08d", num));
    }

    public void deleteAll() {
        for (int i = 0; i < num; ++i) {
            final File f = getFile(i);
            try {
                if (f.exists()) {
                    f.delete();
                }
            }
            catch (final Throwable t) {
                LOG.error(String.format("Failed to delete file %s.", f.getAbsoluteFile()), t);
            }
        }
    }

    public InputStream inputStream() {
        final Iterator<FileInputStream> inputStreams = new Iterator<FileInputStream>() {

            private int i = 0;
            private File[] files = list();

            @Override
            public boolean hasNext() {
                return i < files.length && files[i+1].exists();
            }

            @Override
            public FileInputStream next() {
                try {
                    return new FileInputStream(files[i++]);
                } catch (final FileNotFoundException e) {
                    throw new NoSuchElementException(e.getMessage());
                }
            }
        };

        return new ConcatinatedInputStream(inputStreams);
    }

    public static class RotateAfterWrites implements Predicate<FileRing>  {

        private int count = 0;
        private final int rotate;

        public RotateAfterWrites(final int rotate) {
            this.rotate = rotate;
        }

        @Override
        public boolean test(FileRing fileRing) {
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
