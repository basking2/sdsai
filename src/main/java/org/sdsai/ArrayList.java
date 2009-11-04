/* $Id: DynamicTable.java 329 2007-02-14 21:18:34Z sam $ */

package org.sdsai;

import java.util.Iterator;

/**
 * This is a simple ArrayList. It does not grow, it is the size
 * chosen. If an element cannot be added, it is simply dropped.
 * NULL values are returned if an object does not exist.
 */
public class ArrayList<E> implements List<E>
{

  protected Object[] list;

  protected int size;
 
 /**
  * Create a new List object with keepOrdered set to false.  That is, the list
  * will delete objects in constant time, but not keep the original ordering of object.
  */
  public ArrayList(){ list = new Object[0]; size=0; }

  public ArrayList(int i){ list = new Object[i]; size=0; }

  /**
   * Append an object to the list. 
   * Silently fail if there is no room left in the array.
   */
  public void add(E o)
  {
    if(size < list.length) {
      
      list[size++]=(Object) o;

    }
  }
  
  @SuppressWarnings("unchecked")
  public E get(int i)
  {
    return ( i >= 0 && i < list.length ) ? (E) list[i] : null;
  }

  /**
   * Set an index to an object.  This will not change the size
   * of the list if i is not in the list.
   */
  @SuppressWarnings("unchecked")
  public E set(int i, E o)
  {
    
    E tmp = null;
    
    if ( i >= 0 && i < list.length ) {
      tmp = (E) list[i];
      list[i] = o;
    }
    
    return tmp;
    
  }
  
  /**
   * This method puts the last element in the list at index i and returns
   * the previous value of index i.  This effectively deletes the value
   * at index i from the list.
   */
  @SuppressWarnings("unchecked")
  public E del(int i)
  {
    E tmp = null;
    
    if ( i >= 0 && i < list.length ) { /* check that i is in the active part of the array */

      tmp = (E) list[i];
        
      /* Decrement fill so it points to the last element. */
      /* This slot in the array will eventually be null. */
      size--;
        
      list[i] = null;

    } /* Close big if(i<fill) statement */


    return tmp;
  }
  

  /**
   * This method is a call to find and del(int i).
   */
  public E del(E o){ return del(find(o)); }


  public int find(E o)
  {
    Object target = (Object) o;

    for(int i=0; i < size; i++)
      if(list[i] == target)
        return i;

    return -1;
  }
  
  public boolean empty(){ return size==0; }
  public int size(){ return size; }
  public int tableSize() { return list.length; }
  
  /**
   * O(n) which copies the object array inside the DynamicTable to a new
   * object array.
   */
  public Object[] toArray()
  {
    Object[] o = new Object[list.length];

    for(int i=0; i<list.length; i++)
      o[i] = list[i];

    return o;
  }

  @SuppressWarnings("unchecked")
  public void foreach(ListVisitor<E> lv)
  {
    for ( int i = 0; i < size; i++ )
      lv.visit( (E) list[i]);
  }


  public Iterator<E> iterator() 
  {
    return new Iterator<E>()
    {
      protected int index = 0;

      public boolean hasNext()
      {
        return index < size;
      }

      public E next()
      {
        /* NOTE: Increment index AFTER returning the "next" value. */
        return get(index++);
      }

      public void remove()
      {
        if ( size == 0 )
          throw new IllegalStateException();
        del(index);
      }
    };
  }
  
  public void clear()
  {
    list = new Object[0];
    size = 0;
  }
}
