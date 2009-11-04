/* $Id: MaxHeap.java 332 2007-03-27 10:24:46Z sam $ */

package org.sdsai;

public class MaxHeap<E> extends Heap<E>
{

  public MaxHeap(int branches) { this.branches = branches; }

  public MaxHeap() { super(); }

  protected boolean relationOk(Key<E> parent, Key<E> child)
  {
    return ( child.lt(parent) );
  }

}
