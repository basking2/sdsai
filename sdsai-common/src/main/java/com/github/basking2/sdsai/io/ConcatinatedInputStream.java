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

    /**
     * Try to fill a buffer, even if that means blocking.
     */
    private final boolean tryFullRead = true;

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
    public int read(byte[] b, int off, int len) throws IOException {
        int totalRead = 0;

        while (inputStream != null) {
            final int i = inputStream.read(b, off + totalRead, len - totalRead);

            // The read is at the end of the stream. Cycle it.
            if (i == -1) {
                nextStream();
            }
            // If we read some bytes, record that we did and check if we should return.
            else {
                totalRead += i;

                // This could be totalRead==len to force a full read of the buffer.
                if (tryFullRead) {
                    if (totalRead == len) {
                        return totalRead;
                    }
                }
                else {
                    return totalRead;
                }
            }
        }

        // We've run out of input streams and not returned yet.
        if (totalRead == 0) {
            // If we've read nothing, return -1.
            return -1;
        } else {
            // If we've read anything, then return the total bytes read.
            return totalRead;
        }
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return read(b, 0, b.length);
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
