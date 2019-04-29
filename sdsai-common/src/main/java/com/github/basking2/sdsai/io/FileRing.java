package com.github.basking2.sdsai.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * A class that reads and writes over a ring of files.
 *
 * When the ring is overwrite pre-existing data, the entire file is truncated and writing restarts.
 *
 * Records written do not span files.
 */
public class FileRing {

    private Logger LOG = LoggerFactory.getLogger(FileRing.class);

    private final File dir;
    private final int ringSize;

    private int num;
    private OutputStream out;

    public FileRing(final File dir, final int ringSize) throws FileNotFoundException {

        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.dir = dir;
        this.ringSize = ringSize;
        this.num = 0;

        final File f = getFile(this.num);
        this.out = new FileOutputStream(f, true);
    }

    /**
     * Build a new file name.
     * @param num The number of the file.
     * @return The file.
     */
    private File getFile(final int num) {
        return new File(dir, String.format("%08d", num));
    }

    public void deleteAll() {
        for (int i = 0; i < num; ++i) {
            final File f = getFile(i);
            try {
                if (f.exists()) {
                    f.delete();
                }
            }
            catch (final Throwable t) {
                LOG.error(String.format("Failed to delete file %s.", f.getAbsoluteFile()), t);
            }
        }
    }
}
