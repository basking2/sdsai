package com.github.basking2.sdsai.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * And output stream that buffers data and opens and writes that data only when the buffer is full.
 */
public abstract class LazyOutputStream extends OutputStream {
    private byte[] buffer;
    private int count;

    public LazyOutputStream(final int count) {
        this.buffer = new byte[count];
        this.count = 0;
    }

    protected void writeImpl(final byte[] data, int off, int len) throws IOException {
        try(final OutputStream outputStream = openOutputStream()) {

            if (count > 0) {
                outputStream.write(buffer, 0, count);
                count = 0;
            }

            if (len > 0) {
                outputStream.write(data, off, len);
            }
        }
    }

    /**
     * Override this method if an extending class would like to open a type of stream.
     *
     * This allows the buffering logic and closing of this class to be used for other output streams that may be
     * cost-prohibitive to keep in an open state.
     *
     * @return An opened OutputStream.
     * @throws IOException On any exception.
     */
    protected abstract OutputStream openOutputStream() throws IOException;

    @Override
    public void write(int b) throws IOException {
        if (count + 1 < buffer.length) {
            buffer[count++] = (byte)b;
        }
        else {
            writeImpl(new byte[]{(byte)b}, 0, 1);
        }
    }

    @Override
    public void write(byte[] data, int off, int len) throws IOException {
        if (count + len < buffer.length) {
            for (int i = 0; i < len; i++) {
                buffer[count++] = data[off + i];
            }
        }
        else {
            writeImpl(data, off, len);
        }
    }

    @Override
    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    @Override
    public void close() throws IOException {
        if (count > 0) {
            writeImpl(null, 0, 0);
        }
    }

    @Override
    public void flush() throws IOException {
        if (count > 0) {
            writeImpl(null, 0, 0);
        }
    }

    public void resizeBuffer(final int newSize) {
        this.buffer = Arrays.copyOf(buffer, newSize);
    }
}
