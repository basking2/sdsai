/* $Id: List.java 702 2008-05-27 21:53:19Z sbaskin $ */

package com.github.basking2.sdsai.sandbox;

/**
 * 
 * @author sam
 *
 * @param <E>
 */
public interface List<E> extends Iterable<E>{
 
  public void add(E o);

  public E get(int i);

  /**
   * Set the object at index i to o.  
   * If i is not in the list, then this method has
   * no effect. The previous value is returned or null on failure.
   */
  public E set(int i, E o);

  public E del(int i);

  public E del(E o);
  
  public void clear();

  public boolean empty();

 /**
  * returns the index of the object. Should return -1 if the object is not
  * found.
  */
  public int find(E o);

  public int size();

  public Object[] toArray();
}
