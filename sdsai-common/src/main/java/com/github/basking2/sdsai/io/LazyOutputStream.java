package com.github.basking2.sdsai.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * And output stream that buffers data and opens and writes that data only when the buffer is full.
 */
public class LazyOutputStream extends OutputStream {
    private byte[] buffer;
    private int count;
    private OutputStreamFactory factory;

    public LazyOutputStream(final int count, final OutputStreamFactory factory) {
        this.buffer = new byte[count];
        this.count = 0;
        this.factory = factory;
    }

    private void writeImpl(final byte[] data, final int off, final int len) throws IOException {
        try(final OutputStream outputStream = factory.get()) {

            if (count > 0) {
                outputStream.write(buffer, 0, count);
                count = 0;
            }

            if (len > 0) {
                outputStream.write(data, off, len);
            }
        }
    }

    @Override
    public void write(final int b) throws IOException {
        if (count + 1 < buffer.length) {
            buffer[count++] = (byte)b;
        }
        else {
            writeImpl(new byte[]{(byte)b}, 0, 1);
        }
    }

    @Override
    public void write(final byte[] data, final int off, final int len) throws IOException {
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
    public void write(final byte[] data) throws IOException {
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

    @FunctionalInterface
    public static interface OutputStreamFactory {
        OutputStream get() throws IOException;
    }
}
