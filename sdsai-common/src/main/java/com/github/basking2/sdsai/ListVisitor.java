/**
 * Copyright (c) 2008-2021 Sam Baskinger
 *
 * $Id: ListVisitor.java 762 2008-08-20 20:23:17Z sam $
 */

package com.github.basking2.sdsai;

/**
 * A ListVisitor is passed to the foreach(...) method of a List implementation.
 * The list implementation then calls visit(Object) on every object in 
 * the list in order, staring from the first object to the last.
 */
public interface ListVisitor<E>
{
  public void visit(E o);
}

