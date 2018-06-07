package com.github.basking2.sdsai;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FindCrossproductSubsetTest {
    @Test
    public void test() {
        final List<int[]> dims = new ArrayList<>();
        for (int i = 0; i < 3; i ++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    dims.add(new int[]{i, j, k});
                }
            }
        }

        dims.remove(26);
        dims.remove(25);
        dims.remove(24);
        dims.remove(5);
        dims.remove(2);
        dims.remove(0);

        FindCrossproductSubset.findDimsCrossproduct(dims);

        assertEquals(6, dims.size());

        assertEquals(0, dims.get(0)[0]);
        assertEquals(0, dims.get(0)[1]);
        assertEquals(1, dims.get(0)[2]);

        assertEquals(1, dims.get(1)[0]);
        assertEquals(0, dims.get(1)[1]);
        assertEquals(1, dims.get(1)[2]);

        assertEquals(2, dims.get(2)[0]);
        assertEquals(0, dims.get(2)[1]);
        assertEquals(1, dims.get(2)[2]);

        assertEquals(0, dims.get(3)[0]);
        assertEquals(1, dims.get(3)[1]);
        assertEquals(1, dims.get(3)[2]);

        assertEquals(1, dims.get(4)[0]);
        assertEquals(1, dims.get(4)[1]);
        assertEquals(1, dims.get(4)[2]);

        assertEquals(2, dims.get(5)[0]);
        assertEquals(1, dims.get(5)[1]);
        assertEquals(1, dims.get(5)[2]);

        for (int[] dim : dims) {
            for (int i : dim) {
                System.out.print(i + " ");
            }
            System.out.println("");
        }
    }
}
