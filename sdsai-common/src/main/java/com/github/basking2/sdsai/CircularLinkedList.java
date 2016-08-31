/* $Id: CircularLinkedList.java 338 2007-07-05 20:13:16Z sam $ */

package com.github.basking2.sdsai;

public class CircularLinkedList<E>
{
  protected int     size = 0;

  /**
   * The current location in the list.
   */
  protected Element<E> root = null;

  private class Element<F> {
    public F       data;
    public Element<F> next;
    public Element<F> prev;

    public Element(F d, Element<F> p, Element<F> n)
    {
      data = d;
      next = n;
      prev = p;
    }
  }

  /**
   * Add the object to the end of the list, before the root of the list.
   */
  public void add(E o)
  {
    if(size==0){
      size      = 1;
      root      = new Element<E>(o, null, null);
      root.prev = root;
      root.next = root;
    } else {
      size++;
      root.prev.next = new Element<E>(o, root.prev, root);
      root.prev      = root.prev.next;
    }      
  }

  /**
   * Rotate root of the list +i (root.next) or -i (root.prev) spaces and
   * return the Object stored in that Element. If the list is empty
   * null is returned. 
   */
  public E rotate(int i)
  {

    if(size > 0){

      /* Rotate + if i is positive. */
      for( ; i > 0; i--)
        root = root.next;

      /* Rotate - if i is negative. */
      for( ; i < 0; i++)
        root = root.prev;

      return root.data;

    }

    return null;
  }

  /* Rotate the root to root.next. Equivalent to rotate(1) */
  public E next()
  { 
    if(root!=null){
      root = root.next;
      return root.data;
    }

    return null;
  }

  /* Rotatet the root to root.prev. Equivalent to rotate(-1) */
  public E prev()
  { 
    if(root!=null){
      root = root.prev;
      return root.data;
    }

    return null;
  }

  /**
   * Rotate the root forward until we find the first match of Object o.
   * If we do not find o, we  return null, otherwise the object is returned.
   */
  public E rotateTo(E o)
  {
    int i = size;
    
    while( i > 0 && o != root.data){
      root = root.next;
      i--;
    }
    
    /* Return o ONLY if we rotated to o. Return null otherwise. */
    return o == root.data ? o : null;
  }

  public E del(E o)
  {
    /* Store the root so we can "rewind" to it quickly. */
    Element<E> e = root;

    /* This often will lose the current root. This is why we store it in e. */
    o = rotateTo(o);

    /* If we've found the object, delete the root. */
    if(o != null){

      if(size > 1){
        root.prev.next = root.next;
        root.next.prev = root.prev;

        /* If root equals e, then we advance the list to root.next.*/
        /* Otherwise, we simply "rewind" the list to the previous root. */
        if(root==e)
          root = root.next;

        else
          root = e;

        size--;

      } else {

        root = null;
        size = 0;

      }
    }

    return o;
  }

  public E getRoot()
  {
    return root == null ? null:root.data;
  }

  public int size()
  {
    return size;
  }
}
