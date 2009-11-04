/* $Id: RStack.java 762 2008-08-20 20:23:17Z sam $ */

package org.sdsai;

import java.util.Iterator;

/**
 * Similar to RList
 */
public class RStack<E> implements Stack<E>, Iterable<E>
{
  /**
   * Top stack element.
   */
  protected SE<E> top;
  protected int  size;
  
  class SE<F> {
    protected F     data;
    protected SE<F> next;

    public SE(){}
    public SE(F o)         { data = o; }
    public SE(F o, SE<F> n){ data = o; next = n;}

  } /* Close Stacke Element (SE) class. */
 
  public RStack()
  {
    size = 0;
    top  = null;
  }

  public void push(E o)
  {
    top = new SE<E>(o, top);
    size++;
  }
  
  public E pop()
  {
    SE<E> e = null;

    if ( size == 1 ) {
      e    = top;
      top  = null;
      size = 0;
    } else if ( size > 1 ) {
      e    = top;
      top  = top.next;
      size--;
    }
    
    return e.data;
  }
  
  public E peek() { return top.data; }
  public boolean empty() { return top == null; }
  public int     size()   { return size; }

  public E popAndPush(E newE)
  {
    E oldE   = top.data;

    top.data = newE;
    
    return oldE;
  }


  @Override
  public Iterator<E> iterator()
  {
    return new Iterator<E>()
    {

      // Create node "before" the list.
      private SE<E> here = new SE<E>(null, top);

      @Override
      public boolean hasNext() { return top.next != null; }
      @Override
      public E next() { return (here = here.next).data; }
      @Override
      public void remove()
      {
        if ( here == null )
          throw new IllegalStateException();
        
        if ( here == top ) {
          top  = here.next;
          here = top;
        } 
        
        size--;
      }
    };
  }
}
