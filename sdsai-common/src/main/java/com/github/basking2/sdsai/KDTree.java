/**
 * Copyright (c) 2021 Sam Baskinger
 */

package com.github.basking2.sdsai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A k-d tree that operates much like a traditional binary search tree.
 *
 * As such, it is possible for this K-D tree to build a linked list of nodes, in a particularly worst case of insertions.
 *
 * It is, however, simple, and does not require knowledge of the dimension range.
 *
 * @param <K> The key value of each element in the multidimensional key.
 * @param <V> The value.
 */
public class KDTree<K extends Comparable<K>, V> {

    private Node head;
    private int size;

    public KDTree() {
        this.head = null;
        this.size = 0;
    }

    public V find(final K[] key) {
        if (head != null) {
            final Node n = head.find(key, 0);

            if (n == null) {
                return null;
            }

            return n.value;
        }
        else {
            return null;
        }
    }

    public V findClosest(final K[] key) {
        if (head != null) {
            final Node n = head.findClosest(key, 0);

            return n.value;
        }
        else {
            return null;
        }
    }

    public void add(final K[] key, final V value) {
        if (head == null) {
            head = new Node(key, value);
            size = 1;
        }
        else {
            head.add(key, 0, value);
            size++;
        }
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public K[] minKey() {
        if (head == null) {
            return null;
        }

        return head.min().key;
    }

    public V min() {
        if (head == null) {
            return null;
        }

        return head.min().value;
    }

    public K[] maxKey() {
        if (head == null) {
            return null;
        }

        return head.max().key;
    }
    public V max() {
        if (head == null) {
            return null;
        }

        return head.max().value;
    }

    public V removeMin() {
        if (head == null) {
            return null;
        }

        size--;

        if (head.left == null) {
            // Head is the min. Remove it.
            if (head.right == null) {
                // Last node.
                size = 0;
                final Node n = head;
                head = null;
                return n.value;
            }
            else {
                final Node n = head;
                final Node min = n.right.removeMinChild();
                if (min == null) {
                    // Right node is the min.
                    head = n.right;
                }
                else {
                    head = min;
                    min.right = n.right;
                }

                return n.value;
            }
        }

        return head.removeMinChild().value;
    }

    public V removeMax() {
        if (head == null) {
            return null;
        }

        size--;

        if (head.right == null) {
            // Head is the max. Remove it.
            if (head.left == null) {
                // Last node.
                size = 0;
                final Node n = head;
                head = null;
                return n.value;
            }
            else {
                final Node n = head;
                final Node max = n.left.removeMaxChild();
                if (max == null) {
                    // Left node is the max.
                    head = n.left;
                }
                else {
                    head = max;
                    max.left = n.left;
                }

                return n.value;
            }
        }

        return head.removeMaxChild().value;
    }

    public V remove(final K[] key) {
        if (head == null) {
            return null;
        }

        size--;

        final Node removed = head.remove(null, key, 0);
        if (removed == head) {
            // Node was not removed! We must do it ourselves.

            if (head.left != null) {
                final Node newRoot = head.left.removeMaxChild();
                if (newRoot != null) {
                    newRoot.left = head.left;
                    newRoot.right = head.right;
                    head = newRoot;
                    return removed.value;
                }
            }

            if (head.right != null) {
                final Node newRoot = head.right.removeMinChild();
                if (newRoot != null) {
                    newRoot.left = head.left;
                    newRoot.right = head.right;
                    head = newRoot;
                    return removed.value;
                }
            }

            if (head.left != null) {
                // Left is our new root.
                head.left.right = head.right;
                head = head.left;
                return removed.value;
            }

            if (head.right != null) {
                // Right is our new root.
                head.right.left = head.left;
                head = head.right;
                return removed.value;
            }

            // OH! Head is the last element! Remove it.
            head = null;
            size = 0;
            return removed.value;
        }
        else if (removed == null) {
            return null;
        }
        else {
            return removed.value;
        }
    }

    public Iterator<K[]> breadthFirstKeys() {
        if (head == null) {
            @SuppressWarnings("unchecked")
            final Iterator<K[]> i = (Iterator<K[]>) Collections.EMPTY_LIST.iterator();
            return i;
        }

        final Iterator<Node> itr = head.iterator();
        return new Iterator<K[]>(){

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public K[] next() {
                return itr.next().key;
            }
        };
    }

    public Iterator<V> breadthFirstValues() {
        if (head == null) {
            @SuppressWarnings("unchecked")
            final Iterator<V> i = (Iterator<V>) Collections.EMPTY_LIST.iterator();
            return i;
        }

        final Iterator<Node> itr = head.iterator();
        return new Iterator<V>(){

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public V next() {
                return itr.next().value;
            }
        };
    }

    private class Node implements Iterable<Node> {
        private K[] key;
        private V value;
        private Node left;
        private Node right;

        public Node(final K[] key, final V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Insert the value at the key along the axis in this node.
         *
         * @param key
         * @param axis
         * @param value
         */
        public void add(final K[] key, final int axis, final V value) {
            final int cmp = key[axis].compareTo(this.key[axis]);

            if (cmp <= 0) {
                if (left == null) {
                    left = new Node(key, value);
                }
                else {
                    left.add(key, (axis + 1) % key.length, value);
                }
            }
            else if (right == null) {
                right = new Node(key, value);
            } else {
                right.add(key, (axis + 1) % key.length, value);
            }
        }

        public boolean keyEquals(final K[] key) {
            for (int i = 0; i < key.length; i++) {
                if (this.key[i].compareTo(key[i]) != 0) {
                    return false;
                }
            }

            return true;
        }

        public Node find(final K[] key, final int axis) {
            final int cmp = key[axis].compareTo(this.key[axis]);

            if (cmp == 0) {

                if (keyEquals(key)) {
                    // If this is totally equal.
                    return this;
                }
                else if (left == null) {
                    return null;
                }
                else {
                    return left.find(key, (axis + 1) % key.length);
                }
            }
            else if (cmp < 0) {
                if (left == null) {
                    return null;
                }
                else {
                    return left.find(key, (axis + 1) % key.length);
                }
            }
            else {
                if (right == null) {
                    return null;
                }
                else {
                    return right.find(key, (axis + 1) % key.length);
                }
            }
        }

        public Node findClosest(final K[] key, final int axis) {
            final int cmp = key[axis].compareTo(this.key[axis]);

            if (cmp == 0) {
                if (keyEquals(key)) {
                    // If this is totally equal.
                    return this;
                }
                else if (left == null) {
                    return this;
                }
                else {
                    return left.findClosest(key, (axis + 1) % key.length);
                }
            }
            else if (cmp < 0) {
                if (left == null) {
                    return this;
                }
                else {
                    return left.findClosest(key, (axis + 1) % key.length);
                }
            }
            else {
                if (right == null) {
                    return this;
                }
                else {
                    return right.findClosest(key, (axis + 1) % key.length);
                }
            }
        }

        public Node min() {
            Node n = this;

            while (n.left != null) {
                n = n.left;
            }

            return n;
        }

        public Node max() {
            Node n = this;

            while (n.right != null) {
                n = n.right;
            }

            return n;
        }

        /**
         * Remove the right-most node from this tree, ignoring the root.
         *
         * @return The removed node or null if there are no adequate child nodes.
         */
        public Node removeMaxChild() {
            if (this.right == null) {
                return null;
            }

            Node p = this;
            Node c = this.right;
            while (c.right != null) {
                p = c;
                c = c.right;
            }

            if (c.left != null) {
                final Node replacementNode = c.left.removeMaxChild();
                if (replacementNode != null) {
                    p.right = replacementNode;
                    replacementNode.left = c.left;
                }
                else {
                    p.right = c.left;
                }
            }
            else {
                p.right = null;
            }

            c.right = null;
            c.left = null;
            return c;
        }

        /**
         * Remove the right-most node from this tree, ignoring the root.
         *
         * @return The removed node or null if there are no adequate child nodes.
         */
        public Node removeMinChild() {
            if (this.left == null) {
                return null;
            }

            Node p = this;
            Node c = this.left;
            while (c.left != null) {
                p = c;
                c = c.left;
            }

            if (c.right != null) {
                final Node replacementNode = c.right.removeMinChild();
                if (replacementNode != null) {
                    p.left = replacementNode;
                    replacementNode.right = c.right;
                }
                else {
                    p.left = c.right;
                }
            }
            else {
                p.left = null;
            }

            c.right = null;
            c.left = null;
            return c;
        }

        /**
         * Remove the right node from this node and return it.
         *
         * @return The removed node or null.
         */
        public Node removeRightChild() {
            if (right == null) {
                return null;
            }

            final Node removed = right;
            final Node replacement = right.removeMinChild();
            if (replacement != null) {
                replacement.right = removed.right;
                right = replacement;
            }
            else {
                right = removed.right;
            }

            removed.left = null;
            removed.right = null;
            return removed;
        }

        /**
         * Remove the left node from this node and return it.
         *
         * @return The removed node or null.
         */
        public Node removeLeftChild() {
            if (left == null) {
                return null;
            }

            final Node removed = left;
            final Node replacement = left.removeMaxChild();
            if (replacement != null) {
                replacement.left = removed.left;
                left = replacement;
            }
            else {
                left = removed.left;
            }

            removed.left = null;
            removed.right = null;
            return removed;
        }

        /**
         * Find the given key in this subtree and remove the node associated with it.
         *
         * If parent is null and {@code this} is returned, no removal was done.
         * Callers of this may use this behavior to determine if they must remove
         * the root node of a tree.
         *
         * <pre>
         * {@code
         *   Node removed = root.remove(null, key, 0);
         *   if (removed == root) {
         *       // Find a new root for the tree and remove the given one.
         *       ...
         *   }
         * }
         * </pre>
         *
         * @param parent The parent node, or null if this node is the root of a tree.
         * @param key The key to identify the node by.
         * @param axis The axis we are considering. This starts at 0.
         * @return The node holding the key. If parent == null the node is not removed.
         */
        public Node remove(final Node parent, final K[] key, final int axis) {
            final int cmp = key[axis].compareTo(this.key[axis]);

            if (cmp == 0) {
                if (keyEquals(key)) {
                    // Remove this node from its parent.
                    if (parent == null) {
                        return this;
                    }
                    else if (parent.left == this) {
                        return parent.removeLeftChild();
                    }
                    else {
                        return parent.removeRightChild();
                    }
                }
                else if (left == null) {
                    return null;
                }
                else {
                    return left.remove(this, key, (axis + 1) % key.length);
                }
            }
            else if (cmp < 0) {
                if (left == null) {
                    return null;
                }
                else {
                    return left.remove(this, key, (axis + 1) % key.length);
                }
            }
            else {
                if (right == null) {
                    return null;
                }
                else {
                    return right.remove(this, key, (axis + 1) % key.length);
                }
            }
        }

        @Override
        public Iterator<Node> iterator() {

            final List<Node> nodes = new ArrayList<>();
            nodes.add(head);

            // A very simple breadth-first traversal.
            return new Iterator<Node>() {
                @Override
                public boolean hasNext() {
                    return ! nodes.isEmpty();
                }

                @Override
                public Node next() {
                    final Node n = nodes.get(0);

                    if (n.left != null) {
                        nodes.add(n.left);
                    }

                    if (n.right != null) {
                        nodes.add(n.right);
                    }

                    return n;
                }
            };
        }
    }

}
