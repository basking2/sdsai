package com.github.basking2.sdsai.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * An input stream that is a concatination of several other input streams.
 */
public class ConcatinatedInputStream extends InputStream {

    private final Iterator<? extends InputStream> inputStreams;

    private InputStream inputStream;

    public ConcatinatedInputStream(final Iterator<? extends InputStream> inputStreams) {
        this.inputStreams = inputStreams;
        if (this.inputStreams.hasNext()) {
            this.inputStream = inputStreams.next();
        }
    }

    @Override
    public int read() throws IOException {
        while (inputStream != null) {
            final int i = inputStream.read();

            if (i != -1) {
                return i;
            }

            nextStream();
        }

        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        while (inputStream != null) {
            final int i = inputStream.read(b, off, len);

            if (i != -1) {
                return i;
            }

            nextStream();
        }

        return -1;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        while (inputStream != null) {
            final int i = inputStream.read(b);

            if (i != -1) {
                return i;
            }

            nextStream();
        }

        return -1;
    }

    private void nextStream() throws IOException {
        inputStream.close();

        // Rotate the input stream and try again.
        if (inputStreams.hasNext()) {
            inputStream = inputStreams.next();
        } else {
            inputStream = null;
        }
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
    }

    @Override
    public int available() throws IOException {

        while (inputStream != null) {
            final int i = inputStream.available();

            // If a FileInputStream returns null, we can advance.
            if (i == 0 && inputStream instanceof FileInputStream) {
                nextStream();
            }
            else {
                return i;
            }
        }

        return 0;
    }
}
