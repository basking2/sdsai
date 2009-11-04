/* $Id: Stack.java 762 2008-08-20 20:23:17Z sam $ */

package org.sdsai;

import java.util.Iterator;

public interface Stack<E> 
{
  public void    push(E o);
  public E       pop();
  public E       peek();
  public boolean empty();
  public int     size();
  public E       popAndPush(E o);
  public Iterator<E> iterator();
}
