package com.github.basking2.sdsai;

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
            final int cmp = this.key[axis].compareTo(key[axis]);

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
            final int cmp = this.key[axis].compareTo(key[axis]);

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
            final int cmp = this.key[axis].compareTo(key[axis]);

            if (cmp == 0) {
                return this;
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
    }

}
