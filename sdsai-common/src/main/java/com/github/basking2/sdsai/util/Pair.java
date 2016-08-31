package com.github.basking2.sdsai.util;

/**
 */
public final class Pair<LEFT, RIGHT> {
    public LEFT left;
    public RIGHT right;

    public Pair(final LEFT left, final RIGHT right) {
        this.left = left;
        this.right = right;
    }

    public static final <LEFT, RIGHT> Pair<LEFT, RIGHT> pair(final LEFT left, final RIGHT right)
    {
        return new Pair<LEFT, RIGHT>(left, right);
    }
}
