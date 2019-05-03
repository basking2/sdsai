package com.github.basking2.sdsai.io;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class LazyFileOutputStreamTest {
    @Test
    public void testWrites() throws IOException {

        new File("target").mkdirs();

        final File f = new File("target", "lazyFileOutputStream.txt");

        try (final OutputStream o = new LazyFileOutputStream(f, 10)) {

            final byte[] buffer = new byte[100];

            o.write(buffer, 0, 30);
            o.write(buffer, 0, 9);
            o.write(buffer, 0, 9);
            o.write(buffer, 0, 9);
            o.write(buffer, 0, 9);
            o.flush();
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
            o.write(1);
        }
        finally {
            f.delete();
        }

    }
}
