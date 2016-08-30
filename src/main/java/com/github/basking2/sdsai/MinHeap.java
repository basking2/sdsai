/* $Id: MinHeap.java 332 2007-03-27 10:24:46Z sam $ */

package com.github.basking2.sdsai;

public class MinHeap<E> extends Heap<E>
{

  public MinHeap(int branches) { this.branches = branches; }

  public MinHeap() { super(); }

  protected boolean relationOk(Key<E> parent, Key<E> child)
  {
    return ( child.gt(parent) );
  }
}
