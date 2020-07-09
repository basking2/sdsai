package com.github.basking2.sdsai.io;

import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
    public int mustRead(final InputStream in, final byte[] buffer, final int off, final int len) throws IOException {
        int r = 0;
        while (len - r > 0) {
            final int i = in.read(buffer, off + r, len - r);
            if (i < 0) {
                return r;
            } else {
                r += i;
            }
        }

        return r;
    }
}
