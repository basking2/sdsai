/* $Id: Set.java 313 2006-07-10 02:26:03Z sam $ */

package com.github.basking2.sdsai;

/**
 * Sets are different than Collections.  A group may have
 * two instance of a data type.  A set has either one instance or 
 * no instances.
 */
public class Set<E> extends RedBlackTree<E> {
  
  /**
   * This calles add(o,NODUPKEYS).  This add is provided for compatibility.
   * WARNING: This does NOT notify the user if the key is not added
   * because the object already exists in the tree.
   */
  public void add(Key<E> k) { try { add(k, NODUPKEYS); } catch ( DuplicateDataException e ) { } }

  /** 
   * Make a new set consisting of two other sets.
   */
  public Set<E> union(Set<E> s)
  {
    Set<E> u = new Set<E>(); /* empty set to fill */
    
    RedBlackTree<E>.RBNode n = min();

    while ( n != null ) {
      try { 
        u.add( get(n), NODUPKEYS);
      } catch ( DuplicateDataException e ) { }

      n = next(n);
    }
    
    n = s.min();

    while ( n != null ) {
      try { 
        u.add( s.get(n), NODUPKEYS);
      } catch ( DuplicateDataException e ) { }

      n = s.next(n);
    }

    return u;
  }

  /**
   * Make a new set consisting of the intersection of two other sets.
   */
  public Set<E> intersect(Set<E> s)
  {
    Set<E>  u = new Set<E>();

    RedBlackTree<E>.RBNode n = min();

    while ( n != null ) {

      Key<E> k = get(n);

      if(s.member(k))
        try {
          u.add(k, NODUPKEYS);
        } catch ( DuplicateDataException e ) { }

      n = next(n);
    }

    return u;
  }

  /**
   * This is not a standard set operation, per se.
   * This the set of all elements that are in one set or the other 
   * but not both.
   */
  public Set<E> disjunction(Set<E> s)
  {
    Set<E> u = new Set<E>();

    Key<E> o;
    
    RedBlackTree<E>.RBNode n = min();

    while ( n != null ) {
      o = get(n);

      if(!s.member(o))
        try {
          u.add(o, NODUPKEYS);
        } catch ( DuplicateDataException e ) { }

      n = next(n);
    }

    n = s.min();

    while ( n != null ) {

      o = s.get(n);

      if(!member(o))
        try {
          u.add(o, NODUPKEYS);
        } catch ( DuplicateDataException e ) { }

      n = s.next(n);
    }

    return u;
  }
    
}
