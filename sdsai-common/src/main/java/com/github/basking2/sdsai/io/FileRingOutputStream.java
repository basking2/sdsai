/**
 * Copyright (c) 2019-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


import static com.github.basking2.sdsai.io.FileRing.getCurrentFileNumberAndSize;
import static com.github.basking2.sdsai.io.FileRing.getFile;
import static com.github.basking2.sdsai.io.FileRing.writeMeta;

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

    private final RotationPredicate doRotation;

    /**
     * Create a new file ring with a start file of 0.
     *
     * @param dir        Parent directory.
     * @param ringSize   How big is the ring.
     * @param doRotation How do we determine we should rotate the file size.
     * @throws FileNotFoundException On errors.
     */
    public FileRingOutputStream(
            final File dir,
            final int ringSize,
            final RotationPredicate doRotation
    ) throws IOException {
        this(dir, "", "", ringSize, doRotation);
    }

    public FileRingOutputStream(
            final File dir,
            final String prefix,
            final String suffix,
            final int ringSize,
            final RotationPredicate doRotation
    ) throws IOException {

        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.dir = dir;
        this.ringSize = ringSize;
        this.doRotation = doRotation;
        this.prefix = prefix;
        this.suffix = suffix;

        final int[] numAndSize = getCurrentFileNumberAndSize(dir, prefix, suffix);

        if (numAndSize[1] == 0) {
            this.num = 0;
            writeMeta(dir, prefix, suffix, num, ringSize);
        } else if (numAndSize[1] != ringSize) {
            this.num = numAndSize[0];
            writeMeta(dir, prefix, suffix, num, ringSize);
        } else {
            this.num = numAndSize[0];
        }

        final File f = getFile(this.dir, this.prefix, this.suffix, this.num);

        this.out = new FileOutputStream(f, true);
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

        writeMeta(dir, prefix, suffix, num, ringSize);
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

        final byte[] data = new byte[]{(byte) b};

        if (doRotation.test(this, data, 0, 1)) {
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

        if (doRotation.test(this, data, offset, length)) {
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

        writeMeta(dir, prefix, suffix, num, ringSize);
    }

    public void delete() {
        FileRing.delete(dir, prefix, suffix, num, ringSize);
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

    @FunctionalInterface
    public interface RotationPredicate {
        boolean test(final FileRingOutputStream fileRing, final byte[] data, int offset, int length);
    }

    public static class RotateAfterWrites implements RotationPredicate {

        private int count = 0;
        private final int rotate;

        public RotateAfterWrites(final int rotate) {
            this.rotate = rotate;
        }

        @Override
        public boolean test(final FileRingOutputStream fileRing, final byte[] data, final int offset, final int length) {
            count++;
            if (count >= rotate) {
                count = 0;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Return true when a file size is exceeded.
     */
    public static class RotateBySize implements RotationPredicate {
        private long size = 0;
        private final long rotate;

        public RotateBySize(final long rotate) {
            this.rotate = rotate;
        }

        /**
         * Construct using a file length as the starting point.
         *
         * If {@code currentFile} is a directory, it is assumed that the input file is a {@link FileRing}
         * and the {@link FileRing#getCurrentFile(File, String, String)} is used to pull the length.
         *
         * @param rotate At what size to rotate.
         * @param currentFile The current file to start the length at.
         * @throws IOException On error.
         */
        public RotateBySize(final long rotate, final File currentFile) throws IOException {
            this.rotate = rotate;

            if (currentFile.isDirectory()) {
                this.size = FileRing.getCurrentFile(currentFile, "", "").length();
            }

            if (currentFile.isFile()) {
                this.size = currentFile.length();
            }
        }

        /**
         * Construct assuming the current file ring.
         *
         * @param rotate At what size to rotate.
         * @param dir The directory that holds the file ring.
         * @param prefix The prefix.
         * @param suffix The suffix.
         * @throws IOException On errors.
         */
        public RotateBySize(final long rotate, final File dir, final String prefix, final String suffix) throws IOException {
            this.rotate = rotate;
            this.size = FileRing.getCurrentFile(dir, prefix, suffix).length();
        }

        @Override
        public boolean test(final FileRingOutputStream fileRing, final byte[] data, final int offset, final int length) {
            size += length;
            if (size >= rotate) {
                size = 0;
                return true;
            } else {
                return false;
            }
        }
    }
}
