package com.github.basking2.sdsai;

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

    public V min() {
        if (head == null) {
            return null;
        }

        return head.min().value;
    }

    public V max() {
        if (head == null) {
            return null;
        }

        return head.max().value;
    }

    private class Node {
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
         * Remove the right-most node from this tree.
         *
         * @return The removed node or null if there are no adequate child nodes.
         */
        public Node removeMax() {
            Node p = this;
            Node c = this.right;
            while (c.right != null) {
                p = c;
                c = c.right;
            }

            if (c.left != null) {
                final Node replacementNode = c.left.removeMax();
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
         * Remove the right-most node from this tree.
         *
         * @return The removed node or null if there are no adequate child nodes.
         */
        public Node removeMin() {
            Node p = this;
            Node c = this.left;
            while (c.left != null) {
                p = c;
                c = c.left;
            }

            if (c.right != null) {
                final Node replacementNode = c.right.removeMin();
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
    }
}
