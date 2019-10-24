package com.github.basking2.sdsai.itrex.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TwoTuple<L, R> implements Iterable<Object> {
    public L l;
    public R r;

    public TwoTuple(final L l, final R r) {
        this.l = l;
        this.r = r;
    }

    public L getL() {
        return l;
    }

    public void setL(L l) {
        this.l = l;
    }

    public R getR() {
        return r;
    }

    public void setR(R r) {
        this.r = r;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof TwoTuple) {
            @SuppressWarnings("unchecked")
            final TwoTuple<Object, Object> tt = (TwoTuple<Object, Object>)o;

            if (l == tt.l && r == tt.r) {
                // Same object (or null) case.
                return true;
            }
            else if (l == null || r == null) {
                // If l or r do not equal tt's l or r and they are not null, we can't compare. Return false.
                return false;
            }
            else if (!l.equals(tt.l) || !r.equals(tt.r)) {
                return false;
            }
            else {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int i = 0;

        if (r != null) {
            i = i ^ r.hashCode();
        }

        if (l != null) {
            i = i ^ l.hashCode();
        }

        return i;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            boolean left = true;
            boolean right = true;

            @Override
            public boolean hasNext() {
                return left || right;
            }

            @Override
            public Object next() {
                if (left) {
                    left = false;
                    return TwoTuple.this.l;
                }

                if (right) {
                    right = false;
                    return TwoTuple.this.r;
                }

                throw new NoSuchElementException("Both left and right values were returned.");
            }
        };
    }
}
