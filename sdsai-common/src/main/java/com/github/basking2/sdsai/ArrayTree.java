package com.github.basking2.sdsai;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A tree backed by an array.
 *
 * Because this tree is backed by an array, the tree is full.
 * That is to say, when you iterate over it, every node value will be
 * handed back whether it is defined or null.
 *
 * The user of this should take care to filter out values
 * they do not want or consider undefined.
 */
public class ArrayTree<T> implements Iterable<T> {

    private final ArrayList<T> tree;
    private final int branchiness;

    public ArrayTree(final int capacity, final int branchiness) {
        this.tree = new ArrayList<T>(capacity);
        this.branchiness = branchiness;
    }	

    public T get(final int index) {
        return tree.get(index);
    }

    public void set(final int index, final T t) {
        tree.set(index, t);
    }

    public int parent(final int index) {
        return parent(index, branchiness);
    }

    public int child(final int index, final int childNum) {
        return child(index, childNum, branchiness);
    }

    public int child0(final int index) {
        return child0(index, branchiness);
    }

    /**
     * Iterate over every node in breadth-first order.
     */
    @Override
    public Iterator<T> iterator() {
        return tree.iterator();
    }

    public static int parent(final int index, final int branchiness) {
        return (index-1)/branchiness;
    }

    public static int child0(final int index, final int branchiness) {
        return index * branchiness + 1;
    }

    public static int child(final int index, final int childNum, final int branchiness) {
        final int child0 = child0(index, branchiness);

        return child0 + childNum;
    }

    public int levelOffset(final int level) {
        return levelOffset(level, branchiness);
    }

    public int indexLevel(final int index) {
        return indexLevel(index, branchiness);
    }

    /**
     * Starting at level 0, this returns the number of nodes at in a level.
     */
    public static int nodesAtLevel(final int level, final int branchiness) {
        return (int)Math.pow(branchiness, level);
    }

    /**
     * What is the offset into an array that the first node of a tree level occures at.
     */
    public static int levelOffset(final int level, final int branchiness) {
        final double d = (Math.pow(branchiness, level)-1) / (branchiness-1);

        return (int) d;
    }

    public static int indexLevel(final int index, final int branchiness) {
        final double n = Math.log(index * (branchiness - 1) + 1);
        final double d = Math.log(branchiness);
        return (int)(n / d);
    }
}
