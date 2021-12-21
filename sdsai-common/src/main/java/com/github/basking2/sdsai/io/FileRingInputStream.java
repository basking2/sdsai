/**
 * Copyright (c) 2019-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.io;

import java.io.*;

import static com.github.basking2.sdsai.io.FileRing.buildInputStreamIterator;
import static com.github.basking2.sdsai.io.FileRing.getCurrentFileNumberAndSize;

public class FileRingInputStream extends InputStream {

    private final ConcatinatedInputStream inputStream;

    /**
     * Constructor.
     *
     * @param dir The directory that holds the files.
     * @param prefix File prefix.
     * @param suffix File suffix.
     * @param startingFile The first file in the ring. This must be less than ringSize.
     * @param ringSize The ring size.
     */
    public FileRingInputStream(
            final File dir,
            final String prefix,
            final String suffix,
            final int startingFile,
            final int ringSize
    ) {
        this.inputStream = new ConcatinatedInputStream(buildInputStreamIterator(dir, prefix, suffix, startingFile, ringSize));
    }

    /**
     * Constructor.
     *
     * @param dir The directory that holds the files.
     * @param prefix The prefix.
     * @param suffix The suffix.
     * @throws IOException On errors extracting the current file and ring size from the meta file, if one exists.
     */
    public FileRingInputStream(
            final File dir,
            final String prefix,
            final String suffix
    ) throws IOException {
        final int[] meta = getCurrentFileNumberAndSize(dir, prefix, suffix);

        this.inputStream = new ConcatinatedInputStream(buildInputStreamIterator(dir, prefix, suffix, meta[0]+1, meta[1]));
    }

    /**
     * Use the output stream's parameters to construct an input stream.
     *
     * Reading is done from one past the current file number. Because the topology of the data is a ring,
     * this functionally starts reading at the start of the oldest file.
     *
     * @param fileRingOutputStream The output stream we are writing to and want to read from.
     * @throws IOException On errors.
     */
    public FileRingInputStream(final FileRingOutputStream fileRingOutputStream) throws IOException {
        this(
                fileRingOutputStream.getDir(),
                fileRingOutputStream.getPrefix(),
                fileRingOutputStream.getSuffix()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte[] data, final int off, final int len) throws IOException {
        return inputStream.read(data, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte[] data) throws IOException {
        return inputStream.read(data);
    }

}
