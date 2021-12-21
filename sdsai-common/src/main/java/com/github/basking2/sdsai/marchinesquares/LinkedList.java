/**
 * Copyright (c) 2020-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.marchinesquares;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Java's linked list library is excellent, but we need direct access to manipulate the next reference.
 *
 * A user of this class may use either the nodes or the list depending on if they need
 * the flexibility of nodes or the global state of a list wrapper.
 *
 * @param <VALUE> The element value type.
 */
public class LinkedList<VALUE> implements Iterable<VALUE> {

    private int size = 0;
    private Node<VALUE> head = null;
    private Node<VALUE> tail = null;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(final VALUE value) {
        if (head == null) {
            tail = head = new Node(value, null);
            size = 1;
        }
        else {
            tail.next = new Node(value, null);
            tail = tail.next;
            size++;
        }
    }

    public void addHead(final VALUE value) {
        if (head == null) {
            tail = head = new Node(value, null);
            size = 1;
        }
        else {
            head = new Node(value, head);
            size++;
        }
    }

    public VALUE getHead() {
        return head.value;
    }

    public VALUE getTail() {
        return tail.value;
    }

    public VALUE removeHead() {
        final VALUE v = head.value;
        head = head.next;
        return v;
    }

    @Override
    public Iterator<VALUE> iterator() {
        if (head != null) {
            return head.iterator();
        } else {
            return new Iterator<VALUE>() {

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public VALUE next() {
                    throw new NoSuchElementException();
                }
            };
        }
    }

    public static class Node<VALUE> implements Iterable<VALUE> {
        public VALUE value;
        public Node<VALUE> next;

        /**
         * When traversing a recursive structure it is helpful to mark nodes in some way.
         *
         * We call this the color of a node. By default a node's color is 0x0.
         *
         * The color of the node has no impact how how functions of this class operate.
         */
        public byte color;

        public Node(final VALUE value, final Node<VALUE> next) {
            this.value = value;
            this.next = next;
            this.color = 0;
        }

        public Node(final VALUE value, final Node<VALUE> next, final byte color) {
            this.value = value;
            this.next = next;
            this.color = color;
        }

        @Override
        public Iterator<VALUE> iterator() {
            return new Iterator<VALUE>() {
                Node<VALUE> next = Node.this;

                @Override
                public boolean hasNext() {
                    return next != null;
                }

                @Override
                public VALUE next() {
                    if (next == null) {
                        throw new NoSuchElementException();
                    }

                    final VALUE v = next.value;
                    next = next.next;
                    return v;
                }
            };
        }

        @Override
        public String toString() {
            return "" + value + " color " + color + ((next == null) ? " no next ":" has next")
                    ;
        }

        public Node<VALUE> reverse() {
            Node<VALUE> prev = null;
            Node<VALUE> curr = this;
            while (curr != null) {
                final Node<VALUE> next = curr.next;
                curr.next = prev;
                prev = curr;
                curr = next;
            }

            return prev;
        }
    }

    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }
}
