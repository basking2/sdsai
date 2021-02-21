package com.github.basking2.sdsai;

import java.util.Iterator;
import java.util.List;

/**
 * An implementation of a Red Black Tree.
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
 * <li>Every node is red or black.
 * <li>The root is black.
 * <li>Every leaf node is null (or NIL) and is a black node.
 * <li>If a node is red, then both its children are black.
 * <li>Every path from an internal node to a leaf node contains the same
 *     number of black nodes.
 * </ul>
 */
public class RedBlackTree2<K extends Comparable<K>, V>
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
        protected K key;
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

        protected RBNode(final K key, final V value, final RBNode parent) {
            this.key = key;
            this.value = value;
            left = RBNULL;
            right = RBNULL;
            this.parent = parent;
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
    }
    /**************** CLOSE INTERNAL CLASS ********************/

    public RBNode RBNULL;

    /**
     * Root of the tree. This is always black.
     */
    protected RBNode root;

    public RedBlackTree2()
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
     * Put the object v in the tree under key k. If a value is already there, it is returned.
     *
     * @param k The key to insert.
     * @param v The value to insert.
     * @return The previous value or null if none.
     */
    public V put(final K k, final V v)
    {
        if(size==0) {
            root = new RBNode(k, v, RBNULL);
            root.isBlack = true;
        } else {
            RBNode prev   = RBNULL;
            RBNode node   = root;       /* name of the node we want to insert */

            int cmp = 0;

            while (node != RBNULL) {
                cmp = k.compareTo(node.key);
                if (cmp < 0) {
                    // The new key is to the left.
                    prev = node;
                    node = node.left;
                }
                else if (cmp > 0) {
                    // The new key is to the right.
                    prev = node;
                    node = node.right;
                }
                else {
                    // Keys are equal. We found the node.
                    final V previousValue = node.value;
                    node.value = v;
                    return previousValue;
                }
            }

            /* Make the new node! */
            node = new RBNode(k, v, prev);

            /* Insert it at the right or left point of the tree */
            if (cmp < 0) {
                prev.left = node;
            } else {
                prev.right = node;
            }

            /** OK, we inserted... now fix the mess we have made! **/
            node.insertFixup();
        }

        size++;
        return null;
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
     * Calls del(o.getKey());
     */
    public V remove(K k)
    {
        /**** FIND THE NODE ****/
        if (size==0) {
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
                V v = n.value;
                _del_node(n);
                return v;
            }

        }

        return null;
    }

    /**
     * Given a node n in this RedBlackTree2, this method does the work of
     * removing the node and fixing the tree. This method is broken out of
     * del(Key<E>) so that it can be reused in the remove() call of this
     * structure's iterator.
     */
    private void _del_node(RBNode n)
    {
        RBNode y; /* value from text book used in delete */
        RBNode x; /* value from text book used in delete */

        if(n.left == RBNULL || n.right == RBNULL)
            y = n;
        else
            y = n.successor();

        x = (y.left != RBNULL)? y.left : y.right;

        x.parent = y.parent;

        if(y.parent==RBNULL)        root           = x;
        else if(y == y.parent.left) y.parent.left  = x;
        else                        y.parent.right = x;

        if(y!=n)      n.key = y.key;

        if(y.isBlack) {
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
}

