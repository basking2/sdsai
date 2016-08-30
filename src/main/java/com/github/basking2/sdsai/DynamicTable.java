/* $Id: DynamicTable.java 702 2008-05-27 21:53:19Z sbaskin $ */

package com.github.basking2.sdsai;

import java.util.Iterator;

/**
 * A simple dynamic table implementation of a list.
 * Running time is O(n), but O(1) amortized.
 * Most other classes use this class.
 * This class does not guarantee order.
 * If strick ordering is required, use an ordered object such as
 * an ordered list, heap or tree. (Note that heaps do not maintain complete
 * order). HOWEVER, items added to the List classes are guaranteed to be
 * the last element in the list until an item is removed.  It is removal
 * that upsets the order of this list.
 */
public class DynamicTable<E> implements List<E>
{

  protected Object[] list;

  protected boolean keepOrdered;

  protected int fill;


 
 /**
  * Create a new List object with keepOrdered set to false.  That is, the list
  * will delete objects in constant time, but not keep the original ordering of object.
  */
  public DynamicTable(){ list = new Object[0]; fill=0; keepOrdered=false;}

  public DynamicTable(int i){ list = new Object[i]; fill=0; keepOrdered=false;}
  
 /**
  * When an item is deleted from a list, if order is <i>not</i>
  * to be preserved, the last element in the list takes the deleted element's
  * place in the list so as to keep the list compact.  This saves memory and
  * keeps the algorithm quick.  This is the default behavior.
  * If order is to be maintained, then empty "slots" in the list
  * are bubbled to the end of the list when an item is deleted.
  * This is obviously slow.  A Linked List should be used instead in cases
  * like these.
  */
  public void keepOrder(boolean b){ keepOrdered = b; }

  /**
   * Append an object to the list.
   */
  public void add(E o)
  {
    if(fill==list.length)
      grow();

    list[fill]=(Object) o;

    fill++;
  }
  
  @SuppressWarnings("unchecked")
  public E get(int i)
  {
    return ( i >= 0 && i < fill ) ? (E) list[i] : null;
  }

  /**
   * Set an index to an object.  This will not change the size
   * of the list if i is not in the list.
   */
  @SuppressWarnings("unchecked")
  public E set(int i, E o){
    try { 
      E tmp      = (E)list[i];
      list[i]    = o; 
      return tmp;
    }

    catch(Exception e) {}
    /* We really can't recover from something like this. */
    finally{ }
    
    return null;
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
    
    if ( i >= 0 && i < fill ) { /* check that i is in the active part of the array */

      tmp = (E) list[i];
        
      /* Decrement fill so it points to the last element. */
      /* This slot in the array will eventually be null. */
      fill--;
        
      if(keepOrdered){
          
        /* Note, here fill has already been decremented for this delete.*/
        for( ;  i < fill ; i++)
          list[i]=list[i+1];
          
      } else {
          
        list[i]    = list[fill]; /* don't inhibit garbage collection */          
        list[fill] = null;      /* don't inhibit garbage collection */
          
      }
        
      if(fill<list.length/4)
        shrink();

    } /* Close big if(i<fill) statement */


    return tmp;
  }
  

  /**
   * This method is a call to find and del(int i).
   */
  public E del(E o){ return del(find(o)); }

  /**
   * Make the size 1/2 the current size.
   */
  private void shrink(){
    Object[] o = (fill==0)?new Object[0]:new Object[list.length/2];
    for(int i=0; i<fill;i++)
      o[i]=list[i];
    list=o;
  }
  
  private void grow(){
    Object o[] = (list.length==0)?new Object[1]:new Object[2*list.length];
    for(int i=0;i<fill;i++)
      o[i]=list[i];
    list=o;
  }
  
  /**
   * Make the size of the list equal to the fill-value.
   * This is very handy if the list will not change in size much and
   * memory conservation is an issue.
   */
  public void trim(){
    Object[] o = new Object[fill];
    for(int i=0;i<fill;i++)
      o[i]=list[i];
    list=o;
  }

  public int find(E o)
  {
    Object target = (Object) o;

    for(int i=0; i < fill; i++)
      if(list[i] == target)
        return i;

    return -1;
  }
  
  public boolean empty(){ return fill==0; }
  public int size(){ return fill; }
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
    for ( int i = 0; i < fill; i++ )
      lv.visit( (E) list[i]);
  }


  public Iterator<E> iterator() 
  {
    return new Iterator<E>()
    {
      protected int index = 0;

      public boolean hasNext()
      {
        return index < size();
      }

      public E next()
      {
        /* NOTE: Increment index AFTER returning the "next" value. */
        return get(index++);
      }

      public void remove()
      {
        if ( size() == 0 )
          throw new IllegalStateException();
        del(index);
      }
    };
  }
  
  public void clear()
  {
    list = new Object[0];
    fill = 0;
  }
}
