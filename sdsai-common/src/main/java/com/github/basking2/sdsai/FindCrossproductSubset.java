package com.github.basking2.sdsai;

import org.apache.commons.math3.analysis.function.Add;

import java.util.*;
import java.util.Set;

/**
 * From a List of tuples, compute the subset of tuples that represent the cross product of all their dimension values.
 *
 * The resulting list of tuples will be shorter than the input list, possibly zero, but the values of the
 * elements in the tuples will be defined for every other tuple element in the returned set.
 */
public class FindCrossproductSubset {

    private static Set<Integer> buildAlphabet(List<int[]> dims, int variableDimension) {
        Set<Integer> h = null;
        final List<Set<Integer>> alphabets = new ArrayList<>();
        String prevkey = null;

        for (int i = 0; i < dims.size(); ++i) {
            String key = "";
            for (int dim : Arrays.copyOfRange(dims.get(i), 0, variableDimension)) {
                key += dim;
            }

            if (!key.equals(prevkey)) {
                prevkey = key;
                h = new HashSet<Integer>();
                alphabets.add(h);
            }

            final int v = dims.get(i)[variableDimension];
            h.add(v);
        }

        // Compute intersection of alphabets.
        h = alphabets.get(0);
        for (Set<Integer> h2 : alphabets) {
            for (Integer k : h) {
                if (!h2.contains(k)) {
                    h.remove(k);
                }
            }
        }

        return h;
    }

    private static void filterDims(final List<int[]> dims, final int variableDimension, Set<Integer> alphabet) {
        int i = 0;
        while (i < dims.size()) {
            if (! alphabet.contains(dims.get(i)[variableDimension])) {
                dims.remove(i);
            } else {
                i += 1;
            }
        }
    }

    static void findDimsCrossproduct(final List<int[]> dims) {
        for (int i = 0; i < dims.get(0).length-1; i++) {

            final int q = i;

            dims.sort(new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    return Integer.compare(o1[q], o2[q]);
                }
            });

            final Set<Integer> alphabets = buildAlphabet(dims, i+1);

            filterDims(dims, i + 1, alphabets);
        }
    }







}
