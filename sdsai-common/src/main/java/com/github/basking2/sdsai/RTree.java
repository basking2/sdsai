package com.github.basking2.sdsai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RTree<D extends Comparable<D>, T> {
    /**
     * Returned if a segment is inside another segment and so will be placed lower in the tree.
     *
     * This is used by {@link #isInside(Comparable[], Comparable[])}'s default implementation.
     */
    public static final int INSIDE = -1;

    public static final int EQUAL = -2;

    /**
     * Returned if a segment is on the same level as another segment and so will be placed at the same level in the tree.
     *
     * This is used by {@link #isInside(Comparable[], Comparable[])}'s default implementation.
     */
    public static final int SAME= 0;

    /**
     * Returned if a segment is outside another segment and so will be placed higher in the tree.
     *
     * This is used by {@link #isInside(Comparable[], Comparable[])}'s default implementation.
     */
    public static final int OUTSIDE = 1;

    public class Node {
        final D[][] dimensions;
        final T t;
        final List<Node> children;

        public Node(final D[][] dimensions, final T t){
            this.dimensions = dimensions;
            this. t = t;
            this.children = new ArrayList<>();
        }

        public T getT() {
            return this.t;
        }

        public D[][] getDimensions() {
            return this.dimensions;
        }

        public List<Node> getChildren() {
            return Collections.unmodifiableList(this.children);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();

            for (final D[] darr : dimensions) {
                sb.append("(").append(darr[0].toString()).append(", ").append(darr[1].toString()).append(") ");
            }

            return sb.toString();
        }

        /**
         * Return the size of this subtree.
         *
         * @return the size of this subtree.
         */
        public int computeSize() {
            int s = 1;
            for (final Node n : children) {
                s += n.computeSize();
            }
            return s;
        }
    }

    private List<Node> roots;
    private int size;

    public RTree() {
        this.size = 0;
        this.roots = new ArrayList<>();
    }

    /**
     * Collect the T values that are {@link #INSIDE} the given dimensions.
     *
     * @param dimensions The dimensions.
     * @param found A function to consume the found nodes.
     */
    public void findEnclosed(final D[][] dimensions, final Consumer<Node> found) {
        findEnclosed(dimensions, found, roots);
    }

    private void findEnclosed(final D[][] dimensions, final Consumer<Node> found, final List<Node> nodes) {
        if (nodes.isEmpty()) {
            return;
        }

        for (final Node n : nodes) {
            int rel = isInside(n.dimensions, dimensions);
            if (rel == OUTSIDE) {
                // Dimensions are inside this. Descend the tree.
                findEnclosed(dimensions, found, n.children);
            } else if (rel == INSIDE) {
                // Dimensions are enclosed! Add this, the subtree, and keep searching in this list.
                consumeAll(n, found);
            }
        }
    }

    /**
     * Collect the T values that are {@link #OUTSIDE} the given dimensions.
     *
     * Note that "outside" means that the dimensions are enclosed by the other object's dimensions.
     *
     * @param dimensions The dimensions.
     * @param found A function to consume the found nodes.
     */
    public void findEnclosing(final D[][] dimensions, final Consumer<Node> found) {
        findEnclosing(dimensions, found, roots);
    }

    private void findEnclosing(final D[][] dimensions, final Consumer<Node> found, final List<Node> nodes) {
        if (nodes.isEmpty()) {
            return;
        }

        for (final Node n : nodes) {
            int rel = isInside(n.dimensions, dimensions);
            if (rel == OUTSIDE) {
                found.accept(n);
                findEnclosing(dimensions, found, n.children);
            }
        }
    }

    public Node find(final D[][] dimensions) {
        return find(dimensions, roots);
    }

    private Node find(final D[][] dimensions, final List<Node> nodes) {
        if (nodes.isEmpty()) {
            return null;
        }

        for (final Node n : nodes) {
            final int rel = isInside(dimensions, n.dimensions);
            if (rel == INSIDE) {
                if (equalDimensions(dimensions, n.dimensions)) {
                    return n;
                } else {
                    final Node foundIt = find(dimensions, n.children);
                    if (foundIt != null) {
                        return foundIt;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Similar to {@link #delete(Comparable[][])}, but this will remove an entire subtree and return it to the caller.
     *
     * Where as {@link #delete(Comparable[][])} promotes all child nodes of a deleted node to the parent node,
     * this simply removes the entire tree and returns it.
     *
     * This is O(n) where n is the size of the subtree. This is required to accurately adjust the collection size.
     *
     * @param dimensions The dimensions of a node to remove along with all it's children.
     * @return The removed node.
     */
    public Node deleteSubtree(final D[][] dimensions) {
        return deleteSubtree(dimensions, this.roots);
    }

    private Node deleteSubtree(final D[][] dimensions, final List<Node> nodes) {
        if (nodes.isEmpty()) {
            return null;
        }

        for (final Node n : nodes) {
            final int rel = isInside(dimensions, n.dimensions);
            if (rel == INSIDE) {
                if (equalDimensions(dimensions, n.dimensions)) {
                    nodes.remove(n);
                    size -= n.computeSize();
                    return n;
                } else {
                    // If a node is "inside" another but is not equal, recurse.
                    final Node deletedNode = deleteSubtree(dimensions, n.children);

                    // If deleted from the subtree, return. We are done. Else, check the next node.
                    if (deletedNode != null) {
                        return deletedNode;
                    }
                }
            }
        }

        return null;
    }

    public T delete(final D[][] dimensions) {
        return delete(dimensions, roots);
    }

    private T delete(final D[][] dimensions, final List<Node> nodes) {
        if (nodes.isEmpty()) {
            return null;
        }

        for (final Node n : nodes) {
            final int rel = isInside(dimensions, n.dimensions);
            if (rel == INSIDE) {
                if (equalDimensions(dimensions, n.dimensions)) {
                    if (n.children.size() > 0) {
                        nodes.addAll(n.children);
                        n.children.clear();
                    }
                    nodes.remove(n);
                    size--;
                    return n.t;
                } else {
                    // If a node is "inside" another but is not equal, recurse.
                    final T t = delete(dimensions, n.children);

                    // If deleted from the subtree, return. We are done. Else, check the next node.
                    if (t != null) {
                        return t;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Add all T values in the tree (Node) to the list.
     * @param n The root to start att.
     * @param l The list to add to.
     */
    private void consumeAll(Node n, final Consumer<Node> l) {
        l.accept(n);

        for (final Node child: n.children) {
            consumeAll(child, l);
        }
    }

    public void add(final D[][] dimensions, final T t) {

        List<Node> nodes = roots;

        while (!nodes.isEmpty()) {
            boolean foundInside = false;
            for (final Node potentialParent : nodes) {
                final int relation = isInside(dimensions, potentialParent.dimensions);
                if (relation == INSIDE) {
                    // New node will be inside this node. Descend.
                    nodes = potentialParent.children;
                    foundInside = true;
                    break;
                } else if (relation == OUTSIDE) {
                    // New node contains this node. Make it the parent.
                    final Node newNode = new Node(dimensions, t);
                    newNode.children.add(potentialParent);
                    nodes.remove(potentialParent);
                    nodes.add(newNode);
                    size++;
                    return;
                }
            }

            if (!foundInside) {
                // We exit the above loop from finding an INSIDE relationship, take no action.
                // If we did _not_ find a subtree to insert into, append to nodes and exit.
                nodes.add(new Node(dimensions, t));
                size++;
                return;
            }

        }

        // Base case, empty node lists get any added node.
        nodes.add(new Node(dimensions, t));
        size++;

    }

    /**
     * Return if {@code d1} is {@link #INSIDE}, {@link #OUTSIDE}, or at the {@link #SAME} level in the tree relative to {@code d2}.
     *
     * Each pari of upper and lower values are compared using {@link #isInside(Comparable[], Comparable[])} to do the
     * comparison.
     *
     * @see #isInside(Comparable[], Comparable[])
     * @param d1 An array of 2-dimensions value pairs where {@code d1[i][0]} is the lower bound and {@code d1[i][1]} is the upper bound.
     * @param d2 An array of 2-dimensions value pairs where {@code d2[i][0]} is the lower bound and {@code d2[i][1]} is the upper bound.
     * @return if {@code d1} is {@link #INSIDE}, {@link #OUTSIDE}, or at the {@link #SAME} level in the tree relative to {@code d2}.
     */
    protected int isInside(final D[][] d1, final D[][] d2) {

        final int dims = (d1.length < d2.length)? d1.length : d2.length;

        if (dims == 0) {
            return SAME;
        }

        // Initialize results to the first relationship.
        int result = isInside(d1[0], d2[0]);
        // If the first pair of dimensions is SAME, this will never change. Return.
        if (result == SAME) {
            return SAME;
        }

        // Now check all the other dimensions and make sure they all match the value in `result`.
        // Any difference means the second object is at the SAME level as the first object.
        for (int i = 1; i < dims; ++i) {
            final int r = isInside(d1[i], d2[i]);

            if (result != r) {
                if (result == EQUAL) {
                    result = r;
                } else {
                    return SAME;
                }
            }
        }

        // If the result is EQUAL, then default to INSIDE.
        return result==EQUAL? INSIDE : result;
    }

    /**
     * Return if {@code d1} is {@link #INSIDE}, {@link #OUTSIDE}, or at the {@link #SAME} level in the tree relative to {@code d2}.
     *
     * This implementation considers that equal values are eligible to be inside. There is an intentional
     * bias to have {@code d1} be placed {@link #INSIDE} {@code d2} if the dimensions are all equal.
     *
     * This implementation may be overwritten if the user desires different comparison behavior.
     *
     * @param d1 A two-dimensional array where index 0 is the lower bound and index 1 is the upper bound.
     * @param d2 A two-dimensional array where index 0 is the lower bound and index 1 is the upper bound.
     * @return if {@code d1} is {@link #INSIDE}, {@link #OUTSIDE}, or at the {@link #SAME} level in the tree relative to {@code d2}.
     */
    protected int isInside(final D[] d1, final D[] d2) {
        final int lowerCmp = d1[0].compareTo(d2[0]);
        final int upperCmp = d1[1].compareTo(d2[1]);

        if (lowerCmp > 0) {
            if (upperCmp <= 0) {
                return INSIDE;
            }
            else {
                return SAME;
            }
        }
        else if (lowerCmp == 0) {
            if (upperCmp < 0) {
                return INSIDE;
            }
            else if (upperCmp == 0) {
                return EQUAL;
            }
            else {
                return OUTSIDE;
            }
        }
        else {
            if (upperCmp >= 0) {
                return OUTSIDE;
            } else {
                return SAME;
            }
        }
    }

    protected boolean equalDimensions(final D[][] d1, final D[][] d2) {
        final int len = d1.length < d2.length ? d1.length : d2.length;

        for (int i = 0; i < len; ++i) {
            if ( d1[i][0].compareTo(d2[i][0]) != 0 || d1[i][1].compareTo(d2[i][1]) != 0 ) {
                return false;
            }
        }

        return true;
    }

    public int getSize() {
        return this.size;
    }
}
