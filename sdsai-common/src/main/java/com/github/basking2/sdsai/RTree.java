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
        List<Node> nodes = this.roots;

        whiletrue: while (true) {
            for (final Node n : nodes) {
                int rel = isInside(n.dimensions, dimensions);
                if (rel == OUTSIDE) {
                    // Dimensions are inside this. Descend the tree.
                    nodes = n.children;
                    break whiletrue;
                } else if (rel == INSIDE) {
                    // Dimensions are enclosed! Add this, the subtree, and keep searching in this list.
                    consumeAll(n, found);
                }

                // Else, keep walking for other INSIDE match types.
            }

            // If we ever get here, we are done.
            return;
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
        List<Node> nodes = this.roots;

        whiletrue: while (true) {
            for (final Node n : nodes) {
                int rel = isInside(n.dimensions, dimensions);
                if (rel == OUTSIDE) {
                    found.accept(n);
                    nodes = n.children;
                    break whiletrue;
                } else if (rel == INSIDE) {
                    // Anything enclosing us would also enclose this node. If we find a node that is within us,
                    // the search is over. There are no nodes left.
                    return;
                }

                // Else, keep walking for other INSIDE match types.
            }

            // If we ever get here, we are done.
            return;
        }
    }

    public Node find(final D[][] dimensions) {
        List<Node> nodes = this.roots;

        whiletrue: while (true) {
            for (final Node n : nodes) {
                final int rel = isInside(dimensions, n.dimensions);
                if (rel == INSIDE) {
                    if (equalDimensions(dimensions, n.dimensions)) {
                        return n;
                    }
                    else {
                        nodes = n.children;
                        break whiletrue;
                    }
                }
            }
        }
        return null;
    }

    public T delete(final D[][] dimensions) {
        List<Node> nodes = this.roots;

        Node parent = null;

        whiletrue: while (true) {
            for (final Node n : nodes) {
                final int rel = isInside(dimensions, n.dimensions);
                if (rel == INSIDE) {
                    if (equalDimensions(dimensions, n.dimensions)) {
                        if (n.children.size() > 0) {
                            if (parent == null) {
                                roots.addAll(n.children);
                            } else {
                                parent.children.addAll(n.children);
                            }
                            n.children.clear();
                        }
                        nodes.remove(n);
                        return n.t;
                    }
                    else {
                        parent = n;
                        nodes = n.children;
                        break whiletrue;
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

        whiletrue: while (true) {
            for (Node potentialParent : nodes) {
                int relation = isInside(dimensions, potentialParent.dimensions);
                if (relation == INSIDE) {
                    nodes = potentialParent.children;
                    break whiletrue;
                } else if (relation == OUTSIDE) {
                    final Node newNode = new Node(dimensions, t);
                    newNode.children.add(potentialParent);
                    nodes.remove(potentialParent);
                    nodes.add(newNode);
                    return;
                } else {
                    final Node newNode = new Node(dimensions, t);
                    nodes.add(newNode);
                    return;
                }
            }
        }
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
        final int result = isInside(d1[0], d2[0]);
        // If the first pair of dimensions is SAME, this will never change. Return.
        if (result == SAME) {
            return SAME;
        }

        // Now check all the other dimensions and make sure they all match the value in `result`.
        // Any difference means the second object is at the SAME level as the first object.
        for (int i = 1; i < dims; ++i) {
            final int r = isInside(d1[i], d2[i]);

            if (result != r) {
                return SAME;
            }
        }

        return result;
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

        if (lowerCmp >= 0) {
            if (upperCmp <= 0) {
                return INSIDE;
            } else {
                return SAME;
            }
        } else {
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
}
