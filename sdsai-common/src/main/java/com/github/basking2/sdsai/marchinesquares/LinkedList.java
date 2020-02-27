package com.github.basking2.sdsai.marchinesquares;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Java's linked list library is excellent, but we need a specific {@link Node#join(Node, Object, Node)} operation.
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

        public Node(final VALUE value, final Node<VALUE> next) {
            this.value = value;
            this.next = next;
        }

        /**
         * @param head    The head of the list. The next value must be null to avoid list leaking.
         * @param body    The element to put in a new list node. This new list node is set to the {@link #next} value of head.
         * @param tail    The {@link #next} value of the node holding body is set to tail.
         * @param <VALUE> The value stored.
         */
        public static <VALUE> void join(final Node<VALUE> head, final VALUE body, final Node<VALUE> tail) {
            if (head.next != null) {
                throw new IllegalStateException("Head node may not have next set to non-null.");
            }

            head.next = new Node<>(body, tail);
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
    }
}
