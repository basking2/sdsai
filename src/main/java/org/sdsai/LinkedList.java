/* $Id: LinkedList.java 702 2008-05-27 21:53:19Z sbaskin $ */

package org.sdsai;

import java.util.Iterator;
/**
 * This is a wrapper for RList.
 * This can append items quickly to the list using add.
 * This also tracks the size of the list allowing constant time
 * add and size calls at the cost of extra calls to RList objects.
 */
public class LinkedList<E> implements List<E>
{

  /**
   * This is a List Element.
   */
  protected class LE<F>
  {
    protected F      data;
    protected LE<F>  next;

    public LE(F o, LE<F> n){ data = o; next=n; }

    /**
     * Get the ith list element after this one.
     */
    public LE<F> getle(int i)
    {
      LE<F> l=this;

      while(i-->0&&l!=null)
        l = l.next;

      return l;
    }

    /**
     * returns the ith data object after this list element.
     */
    public F get(int i)
    {
      LE<F> t = getle(i);
      return t==null? null : t.data;
    }
    
    /**
     * Set the object at index i to o.  
     * If i is not in the list, then this method has
     * no effect.
     */
    public F set(int i, F o)
    {
      LE<F> t = getle(i);
      if(t!=null){
        F tmp = t.data;
        t.data=o;
        return tmp;
      }
      return null;
    }
    
    
    public int find(F o)
    {
      LE<F>  t = this;
      int i = 0;
      while(t!=null && t.data != o){
        t = t.next;
        i++;
      }
      
      return t==null? -1 : i;
    }
  } /* Close internal LE class */

  protected class It implements java.util.Iterator<E> 
  {
    protected LinkedList<E> list;

    protected LE<E>         curr;

    protected LE<E>         prev;

    protected It(LinkedList<E> l) 
    { 
      list = l;
      curr = l.head;
      prev = null;
    }

    public void remove()
    {
      list.doDelete(prev, curr); 
    }

    public E next() 
    {
      prev = curr; 
      if ( prev != null )
        curr = prev.next;

      return curr.data;
    }

    public boolean hasNext()
    { 
      return ( curr != null && curr.next != null );
    }
  }
  
  protected LE<E>   head = null;
  protected LE<E>   tail = null;
  private int       size = 0;

  public boolean empty(){ return size==0; }

  public int      size(){ return size;    }
  
  /**
   * Change the object at index i to the object o.
   */
  public E set(int i, E o)
  { 
    return (head==null) ? null : head.set(i,o); 
  }

  /**
   * Append an object to the list.
   */
  public void add(E o)
  {
    if ( size==0 ) {

      head = new LE<E>(o,null);
      tail = head;

    } else if ( size == 1 ) {

      tail      = new LE<E>(o, null);
      head.next = tail;

    } else {

      tail.next = new LE<E>(o, null);
      tail      = tail.next;

    }

    size++;
  }

  
 /**
  * Return the index in the list in which o is situated.  -1 is
  * returned when an object is not found.
  */
  public int find(E o)
  {
    return (head==null)? -1 : head.find(o); 
  }

  
 /**
  * Get the ith item in the list. Null is returned if
  * the object does not exist.
  */
  public E get(int i)
  {
    return (head==null)? null : head.get(i); 
  }
  

  /**
   * Given the previous node and the node t this will delete the node t
   * from the list.  This function exists because most of the work
   * in deleting is done here leaving other functions to simply
   * find the node t and its preceeding node prev.  If prev is null then
   * t is assumed to be the head of the list.
   */
  protected void doDelete(LE<E> prev, LE<E> t)
  {
    /* Case where we are deleting from the head. */
    /* Note if the list size is >0 and prev = null, t != null. */
    if(prev == null){

      /* The general case. */
      if ( size > 2 ) {

        head = head.next;
        size--;

      } else if ( size == 1 ) {

        head = null;
        tail = null;
        size = 0;

      } else if ( size == 2 ) {

        head = tail;
        size--;

      }

    } else if ( t != null ) {
      
      /* Case where we are deleting the tail. */
      if( t.next == null ){

        prev.next = null;
        tail      = prev;
        size--;

      } else {

        /* Skip over t in the linked list. */
        prev.next = t.next;
        size--;

      }
    }
  }

 /**
  * Remove this object from the list. Null is returned if
  * the object is not in the list.
  */
  public E del(E o)
  {
    if(size==0) 
      return null;
    
    LE<E>     prev = null; 
    LE<E>     t    = head;

    while(t!=null && t.data != o){
      prev = t;
      t    = t.next;
    }

    doDelete(prev, t);

    return o;
  }

  /**
   * Delete the object at index i. 
   */
  public E del(int i)
  {
    if(head == null || i >= size)
      return null;

    LE<E> t    = head;
    LE<E> prev = null;

    for(; i>0; i--){
      prev = t;
      t    = t.next;
    }
      
    E tmp = t.data;

    doDelete(prev, t);
    
    return tmp;
  }

  /**
   * O(n) time to reverse the list.
   */
  public void rev(){
    LE<E> prev  = null;
    LE<E> list  = null;
    LE<E> next  = head;
    
    while(next != null){
      prev      = list;
      list      = next;
      next      = next.next;
      list.next = prev; /* reverse the pointer */
    }
    
    /* Swap the head and tail */
    LE<E> tmp = head;
    head      = tail;
    tail      = tmp;
  }

  /**
   * O(n) walk down the list copying data elements into a new array.
   */
  public Object[] toArray()
  {
    Object[] o = new Object[size];

    LE<E> l = head;

    for(int i = 0; i < size; i++){
      o[i] = l.data;
      l    = l.next;
    }

    return o;
  }

  public void foreach(ListVisitor<E> lv)
  {
    if(size > 0){
      LE<E> e = head;

      while ( e != null ){
        lv.visit(e.data);
        e = e.next;
      }

    }
  }

  public Iterator<E> iterator() 
  {

    return new java.util.Iterator<E>()
    {
      protected LE<E> prev = null;
      protected LE<E> curr = null;
      protected LE<E> next = head;

      public void remove()
      {
        if ( curr == null ) {
          throw new IllegalStateException(); 
        } else {
          doDelete(prev, curr); 
        }
      }

      public E next() 
      {
        E tmp = null;

        if ( next != null ) {
          prev = curr;
          curr = next;
          next = next.next;
          tmp  = curr.data;
        }

        return tmp;
      }

      public boolean hasNext()
      { 
        return next != null;
      }
    };

  }
  
  public void clear()
  {
    head = null;
    tail = null;
    size = 0;
  }

}


