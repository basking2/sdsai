/* $Id: Heap.java 633 2008-04-21 18:34:01Z sbaskin $ */

package org.sdsai;

import java.util.Iterator;

public abstract class Heap<E> implements Iterable<Key<E>> {

  protected List<Key<E>> heap = new DynamicTable<Key<E>>();
  
  protected int  branches = 2;
  
  protected int computeParentIndex(int childIndex)
  {
    return (childIndex-1)/branches;
  }
  
  protected int computeChildIndex(int parentIndex, int childNumber)
  {
    return (parentIndex * branches) + childNumber + 1;
  }
  
  /**
   * (ChildIndex - ChildNum) / Branches = ParentIndex
   * Alternately we can just insert ChildNum with 1.
   * (ChildIndex - 1) / Branches = ParentIndex.
   * @param i - the index of the child in the heap.
   * @return Null or the key.
   */
  protected Key<E> getParent(int i)
  {
    Key<E> k = null;
    
    if(i > 0 && i < heap.size())
      k = heap.get(computeParentIndex(i));
    
    return k;
  }
  
  /**
   * @param parenti - the index of the parent.
   * @param childnum - the index of the child within the parent. That is, 
   * the childnum can only meaningfully be 0 through branches-1.
   * Checking is not done in this method but is done by users of it.
   *
   * @return Null or they key.
   */
  protected Key<E> getChild(int parenti, int childnum)
  {
    Key<E> k = null;
    
    if(parenti >= 0){
      childnum = computeChildIndex(parenti, childnum);
      
      if(childnum < heap.size())
	k = heap.get(childnum);
    }
    
    return k;
  }
  
  /**
   * Is index i a leaf node?
   */
  public boolean isLeaf(int i)
  {
    return getChild(i, 0) == null;
  }

  /**
   * Linear time search returning the key or null.
   */
  public Key<E> find(Key<E> k1)
  {
    Key<E> k2 = null; 

    for(int i=0; i<heap.size(); i++){

      k2 = heap.get(i);

      if(k1.eq(k2))
	break;
    }

    return k2;
  }

  /**
   * Returns the index where the key is or -1.
   */
  public int findi(Key<E> k1)
  {
    Key<E> k2 = null; 
    int    ki = -1;

    for(int i=0; i<heap.size(); i++){

      k2 = heap.get(i);

      if(k1.eq(k2)){
	ki = i;
	break;
      }
    }

    return ki;
  }

  public int size()
  {
    return heap.size();
  }

  public void print()
  {
    for(int i=0; i<heap.size(); i++)
      System.out.println(i +": "+heap.get(i).getData());
  }

  public Key<E> peek()
  {
    return heap.get(0);
  }
  
  public void foreach(ListVisitor<Key<E>> lv)
  {
    heap.foreach(lv);
  }

  public Iterator<Key<E>> iterator()
  {
    return new Iterator<Key<E>>()
    {
      protected int index = 0;

      public boolean hasNext()
      {
        return index < size();
      }

      public Key<E> next()
      {
        /* NOTE: Increment index AFTER returning the "next" value. */
        return heap.get(index++);
      }

      public void remove()
      {
        /**
         * NOTE: Because all re-arrangement of the heap caused by a delete
         * happens after the index deleted we know that this call to
         * delete will remove the element we just saw and only disturb
         * elements after it.
         *
         * Recall the delete algorithm for heaps: 
         *   1. Swap with the last element.
         *   2. Delete the last element.
         *   3. Heap-down element at the index just deleted.
         */
        /* Key<E> k = */ heap.del(--index);
      }
    };
  }

  /**
   * Examine and heap-up the value at index i.
   */
  public void heapUp(int keyi)
  {
    Key<E> key = heap.get(keyi);

    int parenti   = computeParentIndex(keyi);

    Key<E> parentKey = heap.get(parenti);

    while(parentKey != key && ! relationOk(parentKey, key) ) {

      heap.set(parenti, key);
      heap.set(keyi, parentKey);

      keyi = parenti;
      parenti = computeParentIndex(keyi);
      parentKey = heap.get(parenti);
    }
  }

  public void heapDown(int parenti)
  {
    Key<E> key = heap.get(parenti);
    
    while(! isLeaf(parenti)){
      
      int childrenStart = computeChildIndex(parenti, 0);

      int childKeyIndex = childrenStart;

      /* ChildKey cannot be null by a shape property of the heap. */
      Key<E> childKey      = heap.get(childKeyIndex);

      /* Find smallest child to potentially promote. */
      for(int i=1; i<branches; i++){
	Key<E> tmpkey = heap.get(childrenStart+i);	

	if(tmpkey != null && relationOk(tmpkey, childKey) ) {
	  childKey      = tmpkey;
	  childKeyIndex = childrenStart+i;
	}
      }

      /* If the keys are reversed, swap the parent with the child. */
      if( ! relationOk(key, childKey) ) {
	heap.set(parenti, childKey);
	heap.set(childKeyIndex, key);

	/* Update the parenti. */
	parenti = childKeyIndex;
      } else {

	break;

      }
    }
  }

 
  public void add(Key<E> k)
  {
     heap.add(k);
     heapUp(heap.size()-1);
  }
 
  protected abstract boolean relationOk(Key<E> parent, Key<E> child);
  
  public Key<E> del(Key<E> k)
  {
    return del(find(k));
  }

  public Key<E> del(int i)
  {
    Key<E> k = heap.get(i);
    
    /* Just like del(). */
    if ( k != null ) {

      /* Put the last element where i is. */
      heap.set(i, heap.del(heap.size()-1));

      /* If the heap is bigger than 1 element, heapDown i */
      if ( heap.size()>1 )
        heapDown(i);

    }

    return k;
  }

  public Key<E> del()
  {
    return del(0);
  }
}
