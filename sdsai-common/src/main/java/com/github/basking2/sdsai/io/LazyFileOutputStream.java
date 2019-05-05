package com.github.basking2.sdsai.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A lazy file output stream delays opening a file to write until its internal buffer is filled.
 *
 * This is a form of buffered output stream in which not only are writes delayed, but
 * also the file opening.
 */
public class LazyFileOutputStream extends LazyOutputStream {
    private File file;

    public LazyFileOutputStream(final File file, final int count) {
        super(count);
        this.file = file;
    }

    public LazyFileOutputStream(final File file) {
        this(file, 1000);
    }

    /**
     * Override this method if an extending class would like to open a type of stream other than a {@link FileOutputStream}.
     *
     * This allows the buffering logic and closing of this class to be used for other output streams that may be
     * cost-prohibitive to keep in an open state.
     *
     * @return An opened OutputStream.
     * @throws IOException On any exception.
     */
    @Override
    protected OutputStream openOutputStream() throws IOException {
        return new FileOutputStream(file, true);
    }
}
