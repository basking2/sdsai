package com.github.basking2.sdsai.io;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FileRingInputStream extends InputStream {

    private final ConcatinatedInputStream inputStream;

    public FileRingInputStream(
            final File dir,
            final String prefix,
            final String suffix,
            final int startingFile,
            final int ringSize
    ) {
        this.inputStream = new ConcatinatedInputStream(buildInputStreamIterator(dir, prefix, suffix, startingFile, ringSize));
    }

    public FileRingInputStream(
            final File dir,
            final String prefix,
            final String suffix
    ) throws IOException {
        final int[] meta = FileRingOutputStream.getCurrentFileNumberAndSize(dir, prefix, suffix);

        this.inputStream = new ConcatinatedInputStream(buildInputStreamIterator(dir, prefix, suffix, meta[0]+1, meta[1]));
    }

    public FileRingInputStream(final FileRingOutputStream fileRingOutputStream) throws IOException {
        this(
                fileRingOutputStream.getDir(),
                fileRingOutputStream.getPrefix(),
                fileRingOutputStream.getSuffix()
        );
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int read(final byte[] data, final int off, final int len) throws IOException {
        return inputStream.read(data, off, len);
    }

    @Override
    public int read(final byte[] data) throws IOException {
        return inputStream.read(data);
    }

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
            private File[] files = FileRingOutputStream.list(dir, prefix, suffix, number, ringSize);

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
}
