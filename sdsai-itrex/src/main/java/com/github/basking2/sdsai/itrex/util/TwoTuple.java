package com.github.basking2.sdsai.itrex.util;

public class TwoTuple<L, R> {
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
}
