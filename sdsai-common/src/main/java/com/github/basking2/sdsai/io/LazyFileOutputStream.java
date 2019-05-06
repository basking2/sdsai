package com.github.basking2.sdsai.io;


import java.io.File;
import java.io.FileOutputStream;

/**
 * A lazy file output stream delays opening a file to write until its internal buffer is filled.
 *
 * This is a form of buffered output stream in which not only are writes delayed, but
 * also the file opening.
 */
public class LazyFileOutputStream extends LazyOutputStream {
    private File file;

    public LazyFileOutputStream(final File file, final int count) {
        super(count, () -> new FileOutputStream(file, true));
        this.file = file;
    }

    public LazyFileOutputStream(final File file) {
        this(file, 1000);
    }
}
