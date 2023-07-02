/**
 * Copyright (c) 2019-2023 Sam Baskinger
 */

package com.github.basking2.sdsai.io;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FileRingInputStreamTest {
    @Test
    public void testReading() throws IOException {

        final File dir = Files.createTempDirectory("filteringtest").toFile();

        final FileRing fileRing = new FileRing(dir, "", "", 3);

        try(final OutputStream os = fileRing.openForWriting((fileRing1, data, offset, length) -> true)) {
            os.write("one\n".getBytes("UTF-8"));
            os.write("two\n".getBytes("UTF-8"));
            os.write("three\n".getBytes("UTF-8"));
            os.write("four\n".getBytes("UTF-8"));
        }

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
