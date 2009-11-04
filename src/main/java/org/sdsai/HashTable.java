/**
 * $Id$
 * This software comes with absolutely no warranty.
 */

package org.sdsai;

import java.util.Iterator;

public abstract class HashTable<E> implements Iterable<Key<E>>
{

  protected ArrayList<List<Key<E>>> hashTable;
  int size;
  int tableFill;
  int collisions;

  public HashTable()
  {
    empty(4096);
  }

  
  public HashTable(int size)
  {
    empty(size);
  }

 /**
  * This is how the HashTable produces hash values. This is defined in the
  * HashTableFactory.
  */
  abstract protected int _hash(byte[] data);

 /**
  * Compute a hash using _hash and return an index from that number that
  * fits into the hash table.
  */
  public int hash(byte[] data)
  { return Math.abs( _hash( data             ) ) % hashTable.tableSize(); }

 /**
  * Compute a hash using _hash and return an index from that number that
  * fits into the hash table.
  */
  public int hash(Key<? extends Object> k)
  { return Math.abs( _hash( k.getByteArray() ) ) % hashTable.tableSize(); }

/**
 */
  public void add(Key<E> k)
  {
    int  hash = hash(k);
    
    List<Key<E>> tab  = hashTable.get(hash);

    if ( tab == null ) {
      
      tab = new LinkedList<Key<E>>();

      tableFill++;
      
      hashTable.set(hash, tab);
      
    } else {
      
      collisions++;
      
    }

    tab.add(k);
    
    size++;
    
  }

  public Key<E> del(final Key<? extends Object> key)
  {
    int hash   = hash(key);
    Key<E> retKey = null;
    List<Key<E>> tab = hashTable.get(hash);

    if ( tab != null ) {

      for ( Iterator<Key<E>> i = tab.iterator() ; i.hasNext(); ) {
        Key<E> k = i.next();
        
        if ( key.eq(k) ) {
          retKey = k;
          i.remove();
          
          if ( tab.size() == 0 ) {
            hashTable.del(hash);
            tableFill--;
          } else {
            collisions--;
          }
          
          break;
        }
      }
    }
      
    return retKey;
  }

  public Key<E> find(final Key<? extends Object> key)
  {
    int hash   = hash(key);
    Key<E> retKey = null;
    List<Key<E>> tab = hashTable.get(hash);
    
    if ( tab != null ) {
      for ( Key<E> k : tab ) {
        System.out.println("Trying...");
        if ( key.eq(k) ) {
          retKey = k;
          break;
        }
      }
    }
    
    return retKey;
  }

  /* Return the number of elements contained in this object. */
  public int size() { return size; }
  
  /* Return the number of entries in collisions lists in the collision table. */
  public int collisions() { return collisions; }
  
  /* Return the number of elements contained in the hash table (and not the collision table). */
  public int tableFill() { return tableFill; }
  
  public int tableSize() { return hashTable.tableSize(); }
  
  /**
   * Change the table size by removing and re-adding all the elements
   * of the hash. This is a very expensive operation. The
   * previous size is returned.
   */
  public int changeTableSize(int newSize)
  {
    ArrayList<List<Key<E>>> oldHashTable      = hashTable;
    
    empty(newSize);

    for ( List<Key<E>> lk : oldHashTable )
      for ( Key<E> k : lk )
        add(k);
    
    return oldHashTable.tableSize();
  }
  
  /**
   * This expensive operation will iterate through the entire table
   * space and add every key it can find to the given list
   * using l.add(Key<E>). 
   * <p>The order in which the keys are added are first the key in the 
   * hash table at index i is added (i starting at 0), then the contents
   * of the corrosponding collision table is added (that collision table
   * located at index i) in the order in which the keys are listed
   * in the collision table. The index i+1 is processed.
   * @param l the list to collect objects into.
   */
  public void list(final List<Key<E>> l)
  {
    /* Recycle the foreach code and just add this to list. */
    foreach( new ListVisitor<Key<E>>() {
      public void visit(Key<E> k){
        l.add(k); } } ) ;
  }
  
  /**
   * This is a very expensive method as it iterates through every
   * slot in our table looking for any key that might be found and
   * then checking the corrosponding collision table for other keys.
   */
  public void foreach(ListVisitor<Key<E>> l)
  {
    for ( List<Key<E>> lk : hashTable )
      for ( Key<E> k : lk )
        l.visit(k);
  }

  
  /**
   * Empty the entire object so that it is in a null state
   * with tables of the given size.
   * @param size
   */
  public void empty(int size)
  {
    hashTable  = new ArrayList<List<Key<E>>>(size);
    collisions = 0;
    tableFill  = 0;
    size       = 0;
  }
  
  public void empty() { empty(4096); }
  
  public Iterator<Key<E>> iterator()
  {
    return new Iterator<Key<E>>()
    {
      /**
       * i < 0      - this is an unused iterator.
       * ct == null - iterator that just returned i
       * ct != null - iterator that just returned something from ct.
       */
      
      private Iterator<Key<E>> ct = null;     // collisionTable[i]
      private int              i  = -1;       // hashTable[i]
      private int              re = 0;        // number of calls to next().

      /** 
       * Set i to the next index that is greater than i and
       * has an entry in it.
       * Set i to -1 otherwise.
       * @returns the value that i was set to.
       */
      private int _next_index()
      {
        for ( i++ ; i < hashTable.tableSize(); i++ )
          if ( hashTable.get(i) != null )
            return i;
        
        return ( i = -1 );
      }
      
      public boolean hasNext() { return re < size(); }
      
      public Key<E> next() 
      {
        if ( ct == null || ! ct.hasNext() )
          ct = hashTable.get(_next_index()).iterator();
        
        /**
         * Increase re. When re equals the size, then there are no
         * objects left to return. This is used by hasNext().
         */
        re++;
        
        return ct.next();
      }
      
      public void remove()
      {
        ct.remove();
        
        List<Key<E>> e = hashTable.get(i);
        
        if ( e.size() == 0 ) {
          hashTable.del(i);
          tableFill--;
        } else {
          collisions--;
        }
        
        re--;
        size--;
      }
    };
  }
}
