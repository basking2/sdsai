/**
 * Copyright (c) 2018-2021 Sam Baskinger
 */
package com.github.basking2.sdsai;

import java.util.ArrayList;
import java.util.List;

public class Trie<V> {
    private class Node {
        CharSequence key;
        V value;
        List<Node> children;

        public Node(final CharSequence key, final V value, final List<Node> children) {
            this.key = key;
            this.value = value;
            this.children = children;
        }

        public Node(final CharSequence key, final V value) {
            this(key, value, new ArrayList<>());
        }

        public Node(final CharSequence key) {
            this(key, null, new ArrayList<>());
        }
    }

    private final Node root;

    public Trie() {
        this.root = new Node("", null);
    }

    private int prefixlength(final CharSequence k1, final CharSequence k2) {
        int l = java.lang.Math.min(k1.length(), k2.length());
        for (int i = 0; i < l; i++) {
            if (k1.charAt(i) != k2.charAt(i)) {
                return i;
            }
        }

        return l;
    }

    public V find(final CharSequence key) {
        return find_helper(key, root);
    }

    private V find_helper(final CharSequence key, final Node node) {
        if (key.length() == 0) {
            return node.value;
        }

        int prefixlen = 0;
        Node child = null;
        for (int i = 0; i < node.children.size(); i++) {
            final Node c = node.children.get(i);
            prefixlen = prefixlength(key, c.key);
            if (prefixlen > 0) {
                child = c;
                break;
            }
        }

        if (child != null) {
            if (child.key.length() != prefixlen) {
                // Our only match is for a node that should exist between this and the child.
                // We cannot find the key.

                return null;
            }
            else {
                return find_helper(key.subSequence(prefixlen, key.length()), child);
            }
        }
        else {
            return null;
        }
    }

    public void add(final CharSequence key, final V value) {
        add_helper(key, value, root);
    }

    private void add_helper(final CharSequence key, final V value, final Node node) {
        if (key.length() == 0) {
            // Base case for setting the root node.
            node.value = value;
            return;
        }

        Node child = null;
        int child_i = -1;
        int prefixlen = 0;

        for (int i = 0; i < node.children.size(); i++) {
            final Node c = node.children.get(i);
            prefixlen = prefixlength(key, c.key);
            if (prefixlen > 0) {
                child = c;
                child_i = i;
                break;
            }
        }

        if (child != null) {

            //----------------------------------------------------------------------------------------------------
            // There are 4 cases captured here.
            //   1. Our new node is the parent of the chosen node.
            //   2. Our new node is the child of the chosen node.
            //   3. Our new node is the sibling of the chosen node.
            //   4. Our new node is the replacement of the chosen node.
            //----------------------------------------------------------------------------------------------------

            // If there is a child, this means prefixlen is > 0.
            if (prefixlen < key.length()) {
                // If the prefixlen less than our key, we are a sibling or child.

                if (prefixlen < child.key.length()) {
                    // If prefix is less than the child key, we are siblings.

                    child.key = child.key.subSequence(prefixlen, child.key.length());
                    final Node sibling = new Node(key.subSequence(prefixlen, key.length()), value);

                    final Node newChild = new Node(key.subSequence(0, prefixlen), null);
                    newChild.children.add(child);
                    newChild.children.add(sibling);
                    node.children.set(child_i, newChild);
                } else {
                    // Otherwise, we must be the child's child.
                    add_helper(key.subSequence(prefixlen, key.length()), value, child);
                }
            } else if (prefixlen == child.key.length()) {
                child.value = value;
            } else {
                // Make a new child with a partial key in it and no value.
                child.key = child.key.subSequence(prefixlen, child.key.length());
                final Node newChild = new Node(key.subSequence(0, prefixlen), value);
                newChild.children.add(child);
                node.children.set(child_i, newChild);
            }
        } else {
            // No child found with this prefix. We are the child.
            node.children.add(new Node(key, value));
        }

    }

    public void prettyPrint() {
        prettyPrint(root, 0);
    }

    public void prettyPrint(final Node node, final int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        System.out.print(node.key);
        System.out.print("(");
        System.out.print(node.children.size());
        System.out.print("): ");
        System.out.println(node.value);

        for (final Node n: node.children) {
            prettyPrint(n, level+1);
        }
    }

    public V remove(final CharSequence key) {
        if (key.length() == 0) {
            final V v = root.value;
            root.value = null;
            return v;
        }
        else {
            return remove_helper(key, null, root);
        }
    }

    private V remove_helper(final CharSequence key, final Node parent, final Node node) {
        if (key.length() == 0) {

            // NOTE - we are guaranteed this is not the root node.
            // This means the parent node is not None and that we may remove the node.

            // Remove the deleted node from the parent.
            parent.children.remove(node);

            // Promote node's children to the parent's children.
            for (final Node c : node.children) {
                c.key = node.key.toString() + c.key.toString();
                parent.children.add(c);
            }

            return node.value;
        }

        int prefixlen = 0;
        Node child = null;
        for (int i = 0; i < node.children.size(); i++) {
            final Node c = node.children.get(i);
            prefixlen = prefixlength(key, c.key);
            if (prefixlen > 0) {
                child = c;
                break;
            }
        }

        if (child != null) {
            return remove_helper(key.subSequence(prefixlen, key.length()), node, child);
        }
        else {
            return null;
        }
    }
}
