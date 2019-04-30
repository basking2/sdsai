package com.github.basking2.sdsai.io;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileRingTest {
    @Test
    public void testDelete() throws FileNotFoundException {
        final FileRing fileRing = new FileRing(new File("target"), 10, new FileRing.RotateAfterWrites(1));
        fileRing.deleteAll();
    }

    @Test
    public void testWrites() throws IOException {
        final FileRing fileRing = new FileRing(new File("target"), 10, new FileRing.RotateAfterWrites(1));
        try {
            for (int i = 0; i < 11; i++) {
                final byte[] data = ("" + i + "\n").getBytes();
                fileRing.write(data, 0, data.length);
            }

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileRing.inputStream()));

            // 1 is overwritten and truncated. There are only 9 items in a 1-record 10-file ring.
            assertEquals(bufferedReader.readLine(), "2");
            assertEquals(bufferedReader.readLine(), "3");
            assertEquals(bufferedReader.readLine(), "4");
            assertEquals(bufferedReader.readLine(), "5");
            assertEquals(bufferedReader.readLine(), "6");
            assertEquals(bufferedReader.readLine(), "7");
            assertEquals(bufferedReader.readLine(), "8");
            assertEquals(bufferedReader.readLine(), "9");
            assertEquals(bufferedReader.readLine(), "10");
            assertNull(bufferedReader.readLine());
        }
        finally {
            fileRing.deleteAll();
        }
    }
}
