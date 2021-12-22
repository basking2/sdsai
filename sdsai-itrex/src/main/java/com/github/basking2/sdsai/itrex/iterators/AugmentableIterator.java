/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AugmentableIterator<T> implements Iterator<T> {

    /**
     * The first node in the list. The head.
     *
     * If this is null, we are out of elements. If this is non-null then
     * some data remains to be iterated over.
     */
    private LinkedList first = null;

    /**
     * The last node we know about.
     * This is used for appending to the list, only.
     */
    private LinkedList last = null;

    @Override
    public boolean hasNext() {
        return first != null;
    }

    @Override
    public T next() {
        if (first.ts != null && first.ts.hasNext()) {
            final T t = first.ts.next();
            if (!first.ts.hasNext()) {
                first = first.next;
            }
            return t;
        }
        else if (first.t != null) {
            final T t = first.t;
            first = first.next;
            return t;
        }
        else {
            throw new NoSuchElementException();
        }
    }

    public void append(final T t) {
        if (first == null) {
            first = new LinkedList(t);
            last = first;
        }
        else {
            last.next = new LinkedList(t);
            last = last.next;
        }
    }

    public void append(final Iterator<T> ts) {
        if (first == null) {
            first = new LinkedList(ts);
            last = first;
        }
        else {
            last.next = new LinkedList(ts);
            last = last.next;
        }
    }

    public void prepend(final T t) {
        if (first == null) {
            first = new LinkedList(t);
            last = first;
        }
        else {
            first = new LinkedList(t, null, first);
        }
    }

    public void prepend(final Iterator<T> ts) {
        if (first == null) {
            first = new LinkedList(ts);
            last = first;
        }
        else {
            first = new LinkedList(null, ts, first);
        }
    }

    private class LinkedList {
        final T t;
        final Iterator<T> ts;
        LinkedList next = null;

        public LinkedList(final T t) {
            this(t, null, null);
        }

        public LinkedList(final Iterator<T> ts) {
            this(null, ts, null);
        }

        public LinkedList(final T t, final Iterator<T> ts, final LinkedList next) {
            this.t = t;
            this.ts = ts;
            this.next = next;
        }
    }
}
