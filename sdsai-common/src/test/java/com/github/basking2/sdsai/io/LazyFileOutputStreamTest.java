/**
 * Copyright (c) 2019-2023 Sam Baskinger
 */

package com.github.basking2.sdsai.io;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    public void testReading() throws IOException {

        final File dir = Files.createTempDirectory("filteringtest").toFile();

        final FileRing fileRing = new FileRing(dir, "", "", 3);

        final OutputStream os = new LazyOutputStream(1, new LazyOutputStream.OutputStreamFactory() {
            @Override
            public OutputStream get() throws IOException {
                return fileRing.openForWriting((fileRing1, data, offset, length) -> true);
            }
        }) {
        };

        os.write("one\n".getBytes("UTF-8"));
        os.flush();
        os.write("two\n".getBytes("UTF-8"));
        os.flush();
        os.write("three\n".getBytes("UTF-8"));
        os.flush();
        os.write("four\n".getBytes("UTF-8"));
        os.flush();

        try (final InputStream is = fileRing.openForReading()) {
            try (final InputStreamReader isr = new InputStreamReader(is)) {
                try (final BufferedReader bis = new BufferedReader(isr)) {
                    assertEquals("three", bis.readLine());
                    assertEquals("four", bis.readLine());
                    assertNull(bis.readLine());
                }
            }

        }

        fileRing.delete();
    }
}
