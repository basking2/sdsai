/* $Id: LStack.java 762 2008-08-20 20:23:17Z sam $ */

package com.github.basking2.sdsai;

import java.util.Iterator;

/**
 * An LStack is a stack based on a list.
 * This is an array implementation of a stack.
 * It uses the DynamicTable class for storage.
 */
public class LStack<E> implements Stack<E>, Iterable<E>
{
  private List<E> stack;

  public LStack(){ stack = new DynamicTable<E>(); }

  /**
   * This stack will operate on the given list.
   */
  public LStack(List<E> l){ stack = l; }
  public int size(){ return stack.size(); }
  public void push(E o){ stack.add(o); }
  public E pop(){ return stack.del(stack.size()-1); }
  public E peek(){ return stack.size()>0?stack.get(stack.size()-1):null;}
  public boolean empty(){ return stack.size()==0; }
  public E popAndPush(E o){ return stack.set(stack.size()-1,o); }
  public Iterator<E> iterator() { return stack.iterator(); }
}
