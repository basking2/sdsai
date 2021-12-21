/**
 * Copyright (c) 2008-2021 Sam Baskinger
 *
 * $Id: RedBlackTree.java 762 2008-08-20 20:23:17Z sam $
 */

package com.github.basking2.sdsai;

import java.util.Iterator;
import java.util.List;

/**
 * An implementation of an Red Black Tree. 
 * Note, duplicate keys are allowed.
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
public class RedBlackTree<E> implements Iterable<Key<E>>
{

 /**
  * One of three modes that the add method can function with.
  * This will add all objects to the tree.
  */
  public static final int ALL       = 1;

 /**
  * One of three modes that the add method can function with.
  * This will not add objects which are already in the tree.
  * However, object that happen to generate the same key will be added.
  */
  public static final int NODUPOBJ  = 2;

 /**
  * One of three modes that the add method can function with.
  * This will not add object which generate the same key.
  * This implies that no duplicate objects can be in the tree.
  */
  public static final int NODUPKEYS = 3;

  /*****************************************************************
   * How a tree is represented.
   * The advantages to this internal class are:
   *  - Truly empty data structure state.<br>
   *  - We can avoid recursion when "appropriate."<br>
   *  - We can compute and save global info like tree size.<br>
   */
  public class RBNode
  {
    protected boolean         isBlack; /* we insert red nodes */
    protected Key<E>          key;
    protected RBNode          parent;
    protected RBNode          left;
    protected RBNode          right;

    protected RBNode(boolean b)
    { isBlack = b; left=RBNULL; right=RBNULL; parent=RBNULL; }

    protected RBNode(Key<E> k)
    { key = k;    left=RBNULL; right=RBNULL; parent=RBNULL; }

    public void print(String s){
      if(empty()) return;

      System.out.println(s+key);
      
      left.print(s+"|  ");
      right.print(s+"|  ");
    }

    protected RBNode min()
    {
      RBNode n = this;
      while(n.left!=RBNULL)
        n = n.left;
      return n;
    }

    protected RBNode max()
    {
      RBNode n = this;
      while(n.right!=RBNULL)
        n = n.right;
      return n;
    }

    public Key<E> getKey() { return key; }

    /**
     * Returns the successor to a node or RBNULL if there is none.
     */
    protected RBNode successor()
    {
      if(right!=RBNULL)  /* if there is a right subtree, successor=min */
        return right.min();

      else if(parent==RBNULL) /* can't go UP, there's no successor */
        return RBNULL;

      else if(this==parent.left)/* not largest in tree, parent = successor */
        return parent;

      /* If we are the MAX of some subtree... */

      /* Make a variable so we can fake recursion */
      RBNode n = parent;

      /* Find a sub tree where we are NOT the max */
      while(n==n.parent.right)
        if(n==RBNULL)
          return RBNULL; /* no successor */
        else 
          n = n.parent;
      
      return n.parent; /* recurse to depth 1, worst case */
    }

    /**
     * Returns the predecessor to a node or RBNULL if there is none.
     * Reverse of successor function.
     */
    protected RBNode predecessor()
    {
      if(left!=RBNULL)  /* if there is a right subtree, predecessor=max() */
        return left.max();
      else if(parent==RBNULL) /* can't go UP, there's no successor */
        return RBNULL;
      else if(this==parent.right)/* not smallest in tree, parent=predecessor */
        return parent;

      /* If we are the MIN of some subtree... */

      /* Make a variable so we can fake recursion */
      RBNode n = parent;

      /* Find a sub tree where we are NOT the max */
      while(n==n.parent.left)
        if(n==RBNULL)
          return RBNULL; /* no successor */
        else 
          n = n.parent;
      
      return n.parent; /* recurse to depth 1, worst case */
    }

    /**
     * Returns the new "top" of the sub tree.
     */
    protected void rotate_right()
    {
      if(left!=RBNULL){
        /* reposition the new "top" node */
        if(parent!=RBNULL){
          if(this == parent.left)
            parent.left = left;
          else 
            parent.right = left;
        } else 
          root = left;
        left.parent = parent;
        
        parent       = left;       /* change my parent */
        left         = left.right; /* take over my new subtree */
        left.parent  = this;       /* let subtree know I'm the parent */
        parent.right = this;       /* let parent know I'm its child */
      }
    }
    
    /**
     * Returns the new "top" of the sub tree.
     */
    protected void rotate_left()
    {
      if(right!=RBNULL){
        /* reposition the new "top" node */
        if(parent!=RBNULL){
          if(this == parent.left)
            parent.left = right;
          else 
            parent.right = right;
        } else 
          root = right;
        right.parent = parent;
        
        parent       = right;      /* change my parent */
        right        = right.left; /* take over my new subtree */
        right.parent = this;       /* let subtree know I'm the parent */
        parent.left  = this;       /* let parent know I'm its child */
      }
    }

    protected void insert_fixup()
    {
      RBNode n = this;
      RBNode uncle;
      while(!n.parent.isBlack){/*While the parent is red we must fix things*/
        if(n.parent.parent.left == n.parent){
          uncle = n.parent.parent.right;
          if(uncle.isBlack){ /* is our uncle black? */
            if(n == n.parent.right){
              n = n.parent;
              n.rotate_left();
            }
            n.parent.isBlack = true;
            n.parent.parent.isBlack = false;
            n.parent.parent.rotate_right();
          } else { /* is our uncle red? */
            n.parent.isBlack = true;
            uncle.isBlack    = true;
            n.parent.parent.isBlack = false; /* n.p.p = uncle.p ??? */
            n = n.parent.parent;             /* n     = uncle.p ??? */
          }          
        } else {
          uncle = n.parent.parent.left;
          if(uncle.isBlack){ /* is our uncle black? */
            if(n == n.parent.left){
              n = n.parent;
              n.rotate_right();
            }
            n.parent.isBlack = true;
            n.parent.parent.isBlack = false;
            n.parent.parent.rotate_left();
          } else { /* is our uncle red? */
            n.parent.isBlack = true;
            uncle.isBlack    = true;
            n.parent.parent.isBlack = false; /* n.p.p = uncle.p ??? */
            n = n.parent.parent;             /* n     = uncle.p ??? */
          }          
        }
      }      

      root.isBlack = true; /* Ensure rule 2 of an RB tree */
    }

    public void delete_fixup()
    {
      RBNode n = this;
      RBNode w;
      while(n!=root && n.isBlack){
        if(n==n.parent.left){
          w = n.parent.right;
          if(!w.isBlack){
            w.isBlack = true;
            n.parent.isBlack = false;
            n.parent.rotate_left();
            w = n.parent.right;
          }
          if(w.left.isBlack && w.right.isBlack){
            w.isBlack = false;
            n = n.parent;
          } else {
            if(w.right.isBlack){
              w.left.isBlack = true;
              w.isBlack = false;
              w.rotate_right();
              w = n.parent.right;
            }
            w.isBlack = n.parent.isBlack;
            n.parent.isBlack = true;
            w.right.isBlack = true;
            n.parent.rotate_left();
            n = root;
          }
        } else {
          w = n.parent.left;
          if(!w.isBlack){
            w.isBlack = true;
            n.parent.isBlack = false;
            n.parent.rotate_right();
            w = n.parent.left;
          }
          if(w.left.isBlack && w.right.isBlack){
            w.isBlack = false;
            n = n.parent;
          } else {
            if(w.left.isBlack){
              w.right.isBlack = true;
              w.isBlack = false;
              w.rotate_left();
              w = n.parent.left;
            }
            w.isBlack = n.parent.isBlack;
            n.parent.isBlack = true;
            w.left.isBlack = true;
            n.parent.rotate_right();
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

  public RedBlackTree()
  {
    RBNULL = new RBNode(true)
      { public void print(String s){ System.out.println(s+"RBNULL"); } };
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
   * Calls add(o, ALL).
   */
  public void add(Key<E> o)
  { 
    try { add(o, ALL); } catch ( DuplicateDataException dde ) { /* Never thrown */ }
  }

  /**
   * This is just like add, but you can specify a mode.
   * Use the flags provided.
   *
   * <ul>
   * <li> ALL - all object are added.
   * <li> NODUPOBJ - Do not add duplicate objects, but different
   *      object with the same key value will be added.
   * <li> NODUPKEYS - Do not add object with the same keys.
   * </ul>
   */
  public boolean add(Key<E> k, int mode) throws DuplicateDataException
  {
    if(size==0){
      root = new RBNode(k);
      root.isBlack = true;
    } else {
      RBNode prev   = RBNULL;
      RBNode node   = root;       /* name of the node we want to insert */

      switch(mode){
      case ALL:
        /* Locate where to insert the new node */
        while(node != RBNULL){
          prev = node;
          node = ( k.gt(node.key) )? node.right: node.left;        
        }
        break;

      case NODUPOBJ:
        /* Locate where to insert the new node */
        while(node != RBNULL){
          prev = node;
          
          if( k.gt(node.key) )
            node = node.right;

          else if( k.eq(node.key) && k == node.key)
            throw new DuplicateDataException(node.key);
            //return false;

          else 
            node = node.left;
        }
        break;

      case NODUPKEYS:
        /* Locate where to insert the new node */
        while(node != RBNULL){
          prev = node;
          
          if( k.gt(node.key) )
            node = node.right;

          else if( k.eq(node.key) )
            throw new DuplicateDataException(node.key);
            //return false;

          else 
            node = node.left;
        }

        break;
      }

      /* Make the new node! */
      node = new RBNode(k);

      /* Insert it at the right or left point of the tree */
      if( k.gt(prev.key) )
        prev.right = node;
      else
        prev.left = node;
      
      node.parent = prev;

      /** OK, we inserted... now fix the mess we have made! **/
      node.insert_fixup();
    }
    
    size++;
    return true;
  }

 /**
  * Empty the data structure.
  */
  public void delAll()
  {
    root = RBNULL;
    size = 0;
  }

  /**
   * Calls del(o.getKey());
   */
  public Key<E> del(Key<? extends Object> k)
  {
    /**** FIND THE NODE ****/
    if(size==0) return null;
    
    RBNode n = root;
    
    while( k.ne(n.key) ) {
      n = ( k.gt(n.key) ) ? n.right: n.left;
      if(n==RBNULL) return null;
    }

    Key<E> d = n.key;
    /***********************/

    _del_node(n);

    return d;
  }

  public Key<E> delObj(Key<E> k)
  {
    if ( size == 0 ) return null;

    RBNode n    = first(k);

    if ( n == null ) return null;

    while ( n != RBNULL ) {

      /* Same object -> delete and return it. */
      if ( n.key == k )  { _del_node(n); return n.key; } 

      /* Different key, we've lost the object we want. */
      if ( k.ne(n.key) ) {               return null;  } 

      /* Default is to try the next object. */
      n = n.successor();

    }
    
    /* If we end up here then we ran off the tree w/o finding the object. */
    return null;
  }

 /**
  * Given a node n in this RedBlackTree, this method does the work of
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

    if(y.isBlack) x.delete_fixup();

    size--;
  }

  public boolean empty(){ return size==0; }

  public Key<E> find(Key<? extends Object> k)
  {
    if(size==0) return null;
    
    RBNode node = root;
    
    while ( node != RBNULL ) {
      if(k.eq(node.key) )
        return node.key;
      node = ( k.gt(node.key) ) ? node.right: node.left;
    }
    
    return null;
  }

 /**
  * Find all the keys of the value k.
  * This will always return a LinkedList, though possibly of size 0.
  */
  public void findAll(Key<? extends Object> k, List<Key<E>> lst)
  {
    if ( size == 0 ) return;
    
    RBNode node = first(k);

    if ( node == null || node == RBNULL ) return;

    while ( k.eq(node.key) ) {

      lst.add(node.key);
      node = node.successor();

    }
  }

  public boolean member(Key<? extends Object> k){ return find(k) != null; }

  public int size(){ return size; }

 /**
  * Returns the first key in the tree that equals k.
  */
  public RBNode first(Key<? extends Object> k)
  {
    RBNode node = root;
    RBNode prev = RBNULL; /* Previous Match. */
    
    while ( node != RBNULL ) {

      /* When we encounter an eq node, save it to prev. */
      if ( k.eq(node.key) ) {
        prev = node;
        node = node.left;
      } else if ( k.gt(node.key) ) {
        node = node.right;
      } else {
        node = node.left;
      }
    }

    return prev;
  }

  public RBNode last(Key<? extends Object> k)
  {
    RBNode node = root;
    RBNode prev = RBNULL; /* Previous Match. */
    
    while ( node != RBNULL ) {

      /* When we encounter an eq node, save it to prev. */
      if ( k.eq(node.key) ) {
        prev = node;
        node = node.right;
      } else if ( k.gt(node.key) ) {
        node = node.right;
      } else {
        node = node.left;
      }
    }

    return prev;
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

  public Key<E> get(RBNode l)
  {
    return l.key;
  }

  public Iterator<Key<E>> iterator()
  {
    return new Iterator<Key<E>>()
    {
      protected RBNode curr = RBNULL;
      protected RBNode next = root.min();

      public boolean hasNext()
      {
        return next != RBNULL;
      }

      public Key<E> next()
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

