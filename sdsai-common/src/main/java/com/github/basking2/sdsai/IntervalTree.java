package com.github.basking2.sdsai;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An implementation of a Augmented Interval Tree backed by a Red Black Tree.
 *
 * We choose an augmented interval tree for our implementation because we may choose
 * any interval for which the values are ordered. A centered interval tree
 * implementation requires that we be able to compute a center point
 * as well. Computing a center point is not always a defined operation for keys
 * that may be ordered.
 *
 * This version 2 class is a rewrite of the first class to conform to
 * Java collection conventions and update the coding style.
 *
 * The book <u>Introduction to Algorithms</u> by
 * Cormen, Leiserson, Rivest and Stein was used for reference
 * (and as such the algorithms may look JUST like theirs do). See section 13
 * in the 2nd edition.<p>
 *
 * Here are the rules that dictate a Red-Black Tree's shape/look:
 * <ul>
 * <li>Every node is red or black.</li>
 * <li>The root is black.</li>
 * <li>Every leaf node is null (or NIL) and is a black node.</li>
 * <li>If a node is red, then both its children are black.</li>
 * <li>Every path from an internal node to a leaf node contains the same
 *     number of black nodes.</li>
 * </ul>
 */
public class IntervalTree<K extends Comparable<K>, V>
{
    /*****************************************************************
     * How a tree is represented.
     * The advantages to this internal class are:
     *  - Truly empty data structure state.<br>
     *  - We can avoid recursion when "appropriate."<br>
     *  - We can compute and save global info like tree size.<br>
     */
    public class RBNode {
        protected boolean isBlack; /* we insert red nodes */

        /**
         * The {@link Interval#getMin()} value used to order and index nodes in the Red Black tree.
         */
        protected K key;

        /**
         * This is the largest value in the sub-tree.
         */
        private K max;

        /**
         * This is effectively the key, though order and indexing is done through {@link Interval#getMin()}.
         */
        protected Interval<K> interval;

        /**
         * A value associated with the interval.
         */
        protected V value;

        protected RBNode parent;
        protected RBNode left;
        protected RBNode right;

        protected RBNode(final boolean b) {
            isBlack = b;
            left = RBNULL;
            right = RBNULL;
            parent = RBNULL;
        }

        protected RBNode(final Interval<K> interval, final V value, final RBNode parent) {
            this.key = interval.getMin();
            this.max = interval.getMax();
            this.value = value;
            this.interval = interval;
            left = RBNULL;
            right = RBNULL;
            this.parent = parent;
        }

        /**
         * Copy data members (not tree structure members) from another RBNode.
         *
         * NOTE: This may make the {@link #max} invalid. The function {@link #updateMax()} should be called.
         *
         * @param that The node to copy data from.
         * @see #updateMax()
         */
        public void copyData(final RBNode that) {
            this.max = that.max;
            this.value = that.value;
            this.key = that.key;
            this.interval = that.interval;
        }

        public void print(String s) {
            if (empty()) return;

            System.out.println(s + key);

            left.print(s + "|  ");
            right.print(s + "|  ");
        }

        protected RBNode min() {
            RBNode n = this;
            while (n.left != RBNULL)
                n = n.left;
            return n;
        }

        protected RBNode max() {
            RBNode n = this;
            while (n.right != RBNULL)
                n = n.right;
            return n;
        }

        protected void setMax(final K max) {
            this.max = max;
        }

        /**
         * Returns the successor to a node or RBNULL if there is none.
         */
        protected RBNode successor() {
            if (right != RBNULL)  /* if there is a right subtree, successor=min */
                return right.min();

            else if (parent == RBNULL) /* can't go UP, there's no successor */
                return RBNULL;

            else if (this == parent.left)/* not largest in tree, parent = successor */
                return parent;

            /* If we are the MAX of some subtree... */

            /* Make a variable so we can fake recursion */
            RBNode n = parent;

            /* Find a sub tree where we are NOT the max */
            while (n == n.parent.right)
                if (n == RBNULL)
                    return RBNULL; /* no successor */
                else
                    n = n.parent;

            return n.parent; /* recurse to depth 1, worst case */
        }

        /**
         * Returns the predecessor to a node or RBNULL if there is none.
         * Reverse of successor function.
         */
        protected RBNode predecessor() {
            if (left != RBNULL)  /* if there is a right subtree, predecessor=max() */
                return left.max();
            else if (parent == RBNULL) /* can't go UP, there's no successor */
                return RBNULL;
            else if (this == parent.right)/* not smallest in tree, parent=predecessor */
                return parent;

            /* If we are the MIN of some subtree... */

            /* Make a variable so we can fake recursion */
            RBNode n = parent;

            /* Find a sub tree where we are NOT the max */
            while (n == n.parent.left)
                if (n == RBNULL)
                    return RBNULL; /* no successor */
                else
                    n = n.parent;

            return n.parent; /* recurse to depth 1, worst case */
        }

        /**
         * Returns the new "top" of the sub tree.
         */
        protected void rotateRight() {
            if (left != RBNULL) {
                /* reposition the new "top" node */
                if (parent != RBNULL) {
                    if (this == parent.left)
                        parent.left = left;
                    else
                        parent.right = left;
                } else
                    root = left;
                left.parent = parent;

                parent = left; /* change my parent */
                left = left.right; /* take over my new subtree */
                left.parent = this; /* let subtree know I'm the parent */
                parent.right = this; /* let parent know I'm its child */

                // Fix max value after the rotation.
                updateMax();
                parent.updateMax();
            }
        }

        /**
         * Returns the new "top" of the sub tree.
         */
        protected void rotateLeft() {
            if (right != RBNULL) {
                /* reposition the new "top" node */
                if (parent != RBNULL) {
                    if (this == parent.left)
                        parent.left = right;
                    else
                        parent.right = right;
                } else
                    root = right;
                right.parent = parent;

                parent = right; /* change my parent */
                right = right.left; /* take over my new subtree */
                right.parent = this; /* let subtree know I'm the parent */
                parent.left = this; /* let parent know I'm its child */

                // Fix max value after the rotation.
                updateMax();
                parent.updateMax();
            }
        }

        protected void insertFixup() {
            RBNode n = this;
            RBNode uncle;
            while (!n.parent.isBlack) {/*While the parent is red we must fix things*/
                if (n.parent.parent.left == n.parent) {
                    uncle = n.parent.parent.right;
                    if (uncle.isBlack) { /* is our uncle black? */
                        if (n == n.parent.right) {
                            n = n.parent;
                            n.rotateLeft();
                        }
                        n.parent.isBlack = true;
                        n.parent.parent.isBlack = false;
                        n.parent.parent.rotateRight();
                    } else { /* is our uncle red? */
                        n.parent.isBlack = true;
                        uncle.isBlack = true;
                        n.parent.parent.isBlack = false; /* n.p.p = uncle.p ??? */
                        n = n.parent.parent; /* n     = uncle.p ??? */
                    }
                } else {
                    uncle = n.parent.parent.left;
                    if (uncle.isBlack) { /* is our uncle black? */
                        if (n == n.parent.left) {
                            n = n.parent;
                            n.rotateRight();
                        }
                        n.parent.isBlack = true;
                        n.parent.parent.isBlack = false;
                        n.parent.parent.rotateLeft();
                    } else { /* is our uncle red? */
                        n.parent.isBlack = true;
                        uncle.isBlack = true;
                        n.parent.parent.isBlack = false; /* n.p.p = uncle.p ??? */
                        n = n.parent.parent; /* n     = uncle.p ??? */
                    }
                }
            }

            root.isBlack = true; /* Ensure rule 2 of an RB tree */
        }

        public void deleteFixup()
        {
            RBNode n = this;
            RBNode w;
            while(n!=root && n.isBlack){
                if(n==n.parent.left){
                    w = n.parent.right;
                    if(!w.isBlack){
                        w.isBlack = true;
                        n.parent.isBlack = false;
                        n.parent.rotateLeft();
                        w = n.parent.right;
                    }
                    if(w.left.isBlack && w.right.isBlack){
                        w.isBlack = false;
                        n = n.parent;
                    } else {
                        if(w.right.isBlack){
                            w.left.isBlack = true;
                            w.isBlack = false;
                            w.rotateRight();
                            w = n.parent.right;
                        }
                        w.isBlack = n.parent.isBlack;
                        n.parent.isBlack = true;
                        w.right.isBlack = true;
                        n.parent.rotateLeft();
                        n = root;
                    }
                } else {
                    w = n.parent.left;
                    if(!w.isBlack){
                        w.isBlack = true;
                        n.parent.isBlack = false;
                        n.parent.rotateRight();
                        w = n.parent.left;
                    }
                    if(w.left.isBlack && w.right.isBlack){
                        w.isBlack = false;
                        n = n.parent;
                    } else {
                        if(w.left.isBlack){
                            w.right.isBlack = true;
                            w.isBlack = false;
                            w.rotateLeft();
                            w = n.parent.left;
                        }
                        w.isBlack = n.parent.isBlack;
                        n.parent.isBlack = true;
                        w.left.isBlack = true;
                        n.parent.rotateRight();
                        n = root;
                    }
                }
            }

            n.isBlack = true;
        }

        /**
         * Update the {@link #max} value for this node, considering children that are not RBNULL and the interval.
         *
         * If this node is also RBNULL, this does nothing.
         */
        public void updateMax() {
            if (this != RBNULL) {
                K currentMax = interval.getMax();

                // Is the left subtree larger than the currentMax?
                if (left != RBNULL && currentMax.compareTo(left.max) < 0) {
                    currentMax = left.max;
                }

                // Is the right subtree larger than the currentMax?
                if (right != RBNULL && currentMax.compareTo(right.max) < 0) {
                    currentMax = right.max;
                }

                // Set the max in this subtree.
                this.max = currentMax;
            }
        }

        /**
         * Walk up the tree until we are at an RBNULL node and update the {@link #max} value.
         *
         * @see #updateMax()
         */
        public void updateMaxAncestry() {
            RBNode n = this;

            while (n != RBNULL) {

                n.updateMax();;

                // Walk up the tree.
                n = n.parent;
            }
        }
    }

    /**************** CLOSE INTERNAL CLASS ********************/

    public RBNode RBNULL;

    /**
     * Root of the tree. This is always black.
     */
    protected RBNode root;

    public IntervalTree()
    {
        RBNULL = new RBNode(true) {
            public void print(String s) {
                System.out.println(s + "RBNULL");
            }
        };

        RBNULL.left   = RBNULL;
        RBNULL.right  = RBNULL;
        RBNULL.parent = RBNULL;

        root = RBNULL;
    }

    public void print(){ root.print("> ");}


    /**
     * Size of the tree.
     * Handy to have and "cheap" to maintain.
     */
    protected int size;

    /**
     * Add the object v in the tree under interval i using the minimum value as the tree key and tracking the maximum value for the subtree containing the interval.
     *
     * Duplicate intervals take O(n), where n is the number of intervals with the same minimum value, to
     * find. Because of this we do not define put() but add() and allow for duplicate intervals.
     *
     * @param i The interval to insert bounded by keys.
     * @param v The value to insert.
     */
    public void add(final Interval<K> i, final V v) {
        if (size == 0) {
            root = new RBNode(i, v, RBNULL);
            root.isBlack = true;
            size = 1;
            return;
        }


        RBNode prev = RBNULL;
        RBNode node = root;       /* name of the node we want to insert */

        int cmp = 0;
        final K mink = i.getMin();
        final K maxk = i.getMax();

        while (node != RBNULL) {
            // First, update the max key in the node we are considering.
            if (node.max.compareTo(maxk) < 0) {
                node.setMax(maxk);
            }

            // Second, check the minimum value and find the next child key.
            cmp = mink.compareTo(node.key);
            if (cmp <= 0) {
                // The new key is to the left or equal (in which case we insert to the left).
                prev = node;
                node = node.left;
            } else if (cmp > 0) {
                // The new key is to the right.
                prev = node;
                node = node.right;
            }
        }

        /* Make the new node! */
        node = new RBNode(i, v, prev);

        /* Insert it at the right or left point of the tree */
        if (cmp < 0) {
            prev.left = node;
        } else {
            prev.right = node;
        }

        // OK, we inserted... now fix the mess we have made!
        // Note, we've updated the max values during the walk down the tree. Those are OK!
        node.insertFixup();

        size++;
    }

    /**
     * Empty the data structure.
     */
    public void clear()
    {
        root = RBNULL;
        size = 0;
    }

    /**
     *
     * Remove the interval that matches. This is O(log(n) + m) where n is the size of the tree and m is the number of other intervals with the same minimum.
     *
     * If there are duplicate intervals, only one of the duplicates is removed.
     *
     * @param i The interval to remove.
     * @return The value associated with the removed interval or null if the interval is not found.
     */
    public V remove(final Interval<K> i)
    {
        /**** FIND THE NODE ****/
        if (size==0) {
            return null;
        }

        return remove(i, root);
    }

    private V remove(final Interval<K> i, RBNode n)
    {

        final K mink = i.getMin();
        final K maxk = i.getMax();

        while ( n != RBNULL ) {
            final int cmp = mink.compareTo(n.key);
            if (cmp < 0) {
                n = n.left;
            } else if (cmp > 0) {
                n = n.right;
            }
            else {
                final int maxcmp = maxk.compareTo(n.max);

                if (maxcmp <= 0) {

                    // This subtree can contain the interval.
                    if (maxk.compareTo(n.interval.getMax()) == 0) {
                        // The current node matches.
                        final V v = n.value;
                        _del_node(n);
                        return v;
                    } else {
                        // The current min value matches, but the max does not. Search child trees.

                        if (maxk.compareTo(n.left.max) <= 0) {
                            // Recursively try left tree.
                            final V v = remove(i, n.left);
                            if (v != null) {
                                return v;
                            }
                        }

                        if (maxk.compareTo(n.right.max) <= 0) {
                            // Recursively try right tree.
                            final V v = remove(i, n.right);
                            if (v != null) {
                                return v;
                            }
                        }

                        return null;
                    }
                }
                else {
                    // This subtree does not have any node with at least the given max. Not found.
                    return null;
                }
            }

        }

        return null;
    }

    /**
     * Given a node n in this Interval Tree, this method does the work of
     * removing the node and fixing the tree. This method is broken out of
     * del(Key<E>) so that it can be reused in the remove() call of this
     * structure's iterator.
     */
    private void _del_node(RBNode n) {
        // FIXME - repair max  values.
        RBNode y; /* value from text book used in delete */
        RBNode x; /* value from text book used in delete */

        // Assign the node to remove to y.
        // If n has one or zero children, we'll remove n.
        // If n has both children, remove it's successor which will have one child or be a leaf node.
        y  = (n.left == RBNULL || n.right == RBNULL) ? n : n.successor();

        // Y is a leaf node. Set the one child (or RBNULL if no children) as x.
        x = (y.left != RBNULL) ? y.left : y.right;

        // X points at y's parent. We're moving y.
        x.parent = y.parent;

        // Point the parent at x, skipping over y.
        // Since y has at most 1 child, this is a simple operation.
        if (y.parent == RBNULL) {
            // If y was root, x is now root and has no max changes.
            // Y's parent, RBNULL, also has no max change.
            root = x;
        }
        else if (y == y.parent.left) {
            // Y is the left child, so point that to x. The parent needs a max update.
            y.parent.left = x;
        }
        else {
            // Y is the right child, so point that to x. The parent needs a max update.
            y.parent.right = x;
        }

        if (y != n) {
            // We removed n's successor. Before we discard y, copy its data into n.
            // N's max key is updated if y's interval is bigger.
            n.copyData(y);
        }

        // This should be sufficient because...
        // If n had two children, it's successor, y, must be a descendant.
        // Otherwise, n is aliased to y and is removed. X is promoted to n's place in the tree. X and y both point to the same parent.
        y.parent.updateMaxAncestry();

        if (y.isBlack) {
            x.deleteFixup();
        }

        size--;
    }


    public boolean empty(){ return size==0; }

    public V find(final K k)
    {
        if(size==0) {
            return null;
        }

        RBNode n = root;

        while ( n != RBNULL ) {
            final int cmp = k.compareTo(n.key);
            if (cmp < 0) {
                n = n.left;
            } else if (cmp > 0) {
                n = n.right;
            }
            else {
                // Found it!
                return n.value;
            }

        }

        return null;
    }

    public boolean member(final K k) {
        return find(k) != null;
    }

    public int size() {
        return size;
    }

    public RBNode min()
    {
        RBNode n = root.min();

        if(n==RBNULL)
            n = null;

        return n;
    }

    public RBNode max(){
        RBNode n = root.max();

        if ( n == RBNULL )
            n = null;

        return n;
    }

    /**
     * Returns the next largest value in the tree given a location
     * produced by this tree.  If there is no next location,
     * null is returned.
     */
    public RBNode next(RBNode l)
    {
        RBNode node = l.successor();
        if(node == RBNULL )
            node = null;

        return node;
    }

    /**
     * Similar to next.
     */
    public RBNode prev(RBNode l)
    {
        RBNode node = l.predecessor();

        if(node == RBNULL)
            node = null;

        return node;
    }

    public Iterator<K> keys()
    {
        return new Iterator<K>()
        {
            protected RBNode curr = RBNULL;
            protected RBNode next = root.min();

            public boolean hasNext()
            {
                return next != RBNULL;
            }

            public K next()
            {
                curr = next;
                next = next.successor();
                return curr.key;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Iterator<Interval<K>> intervals()
    {
        return new Iterator<Interval<K>>()
        {
            protected RBNode curr = RBNULL;
            protected RBNode next = root.min();

            public boolean hasNext()
            {
                return next != RBNULL;
            }

            public Interval<K> next()
            {
                curr = next;
                next = next.successor();
                return curr.interval;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Iterator<V> values()
    {
        return new Iterator<>()
        {
            protected RBNode curr = RBNULL;
            protected RBNode next = root.min();

            public boolean hasNext()
            {
                return next != RBNULL;
            }

            public V next()
            {
                curr = next;
                next = next.successor();
                return curr.value;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Find all entries for which the entry interval intersects with the given interval i.
     *
     * @param i The interval to intersect with.
     * @param f The consumer.
     */
    public void findIntersecting(final Interval<K> i, final BiConsumer<Interval<K>, V> f) {
        // Find the lowest bound.
        RBNode node = root;

        final K mink = i.getMin();
        final K maxk = i.getMax();

        while (node != RBNULL) {
            int cmp = maxk.compareTo(node.key);
            if (cmp <= 0) {
                // Find the node that we most closely cross into.
                node = node.left;
            }
        }
    }
}

