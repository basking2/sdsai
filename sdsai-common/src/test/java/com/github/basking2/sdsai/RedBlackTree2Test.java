/**
 * Copyright (c) 2021 Sam Baskinger
 */

package com.github.basking2.sdsai;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RedBlackTree2Test {
    @Test
    public void randomAccess() {
        final RedBlackTree2<Integer, Integer> tree = new RedBlackTree2<>();

        final int N = 1000;
        int duplicates = 0;
        int keys = 0;

        for (int i = 0; i < N; i++) {
            final int x = (int) (Math.random() * 100000);
            final Integer old = tree.put(x, x);
            if (old != null) {
                duplicates++;
            }
        }

        assertEquals(duplicates + tree.size(), N);

        final Iterator<Integer> itr = tree.keys();
        final List<Integer> keyList = new ArrayList<>();
        while (itr.hasNext()) {
            keyList.add(itr.next());
        }

        while (!keyList.isEmpty()) {
            tree.remove(keyList.remove(0));
            keys++;
            assertEquals(keys + tree.size() + duplicates, N);
        }

        assertEquals(tree.size(), 0);
    }
}
