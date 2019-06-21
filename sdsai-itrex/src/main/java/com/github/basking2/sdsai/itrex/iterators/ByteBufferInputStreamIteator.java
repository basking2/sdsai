package com.github.basking2.sdsai.itrex.iterators;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * And Iterator that batches and streams reads from an InputStream.
 *
 * When the last read() is called on the input stream returns a length less than 0,
 * the input stream is closed.
 *
 * If you do not exhaust this iterator, you must close the input stream.
 *
 */
public class ByteBufferInputStreamIteator implements Iterator<ByteBuffer> {
    final private int bufferLength = 10240;

    private byte[] nextBuffer = new byte[bufferLength];

    private int nextBufferFill;

    private final InputStream in;

    public ByteBufferInputStreamIteator(final InputStream in) throws IOException {
        this.in = in;
        this.nextBufferFill = readOrClose(in, nextBuffer);
    }


    @Override
    public boolean hasNext() {
        return nextBufferFill >= 0;
    }

    @Override
    public ByteBuffer next() {
        final ByteBuffer buf = ByteBuffer.wrap(java.util.Arrays.copyOf(nextBuffer, nextBufferFill));

        try {
            nextBufferFill = readOrClose(in, nextBuffer);
        } catch (final IOException e) {
            throw new NoSuchElementException(e.getMessage());
        }

        return buf;
    }

    public static final int readOrClose(final InputStream in, byte[] nextBuffer) throws IOException {
        try {
            final int i = in.read(nextBuffer);

            if (i < 0) {
                in.close();
            }

            return i;
        } catch (final Throwable t) {
            in.close();
            throw t;
        }
    }
}
