/* $Id: BTree2.java 752 2008-08-06 21:03:46Z sam $ */

package org.sdsai;

/**
 * BTree2 is almost exactly like the BTree class except that it stores
 * lists of keys.
 * When a new key is added a list is created for it.
 * When a key is added again it is added to the existing list.
 * When a key is removed, any of the matching keys are deleted.
 * When a specific key is removed with delobj, 
 * that exact key object is removed.
 */
public class BTree2<E>
{
  private BTree<List<Key<E>>> tree = new BTree<List<Key<E>>>(5);

  public void add(Key<E> key)
  {
    Key<List<Key<E>>> k  = tree.find(key);

    List<Key<E>> l = null;

    if ( k == null ) {

      l = new DynamicTable<Key<E>>();

      tree.add(new Key<List<Key<E>>>(key.getByteArray(), l));

    } else {

      l = k.getData();

    }

    l.add(key);

  }

  public void addUnique(Key<E> key) throws DuplicateDataException
  {
    List<Key<E>> l = new DynamicTable<Key<E>>();

    tree.addUnique(new Key<List<Key<E>>>(key.getByteArray(), l));

    l.add(key);
  }

 /**
  * Delete the key that is the exact object "key." Null is returned
  * if key is not found. Notice that this is the Key object's 
  * reference. It is not the object contained in the key object.
  */
  public Key<E> delObj(Key<E> key)
  {
    Key<List<Key<E>>> lkey = tree.find(key);
    Key<E> k = null;

    List<Key<E>> l = null;

    if ( lkey != null )
    {
      l = lkey.getData();
      
      for ( int i = 0 ; i < l.size(); i++ ) 
      {
    	  if ( l.get(i) == key ) 
    	  {
    		  k = l.del(i);
    		  
    	      // Delete empty lists.
    	      if ( l.size() == 0 )
    	        tree.del(k);
    	      
    	      // Get out of the loop.
    	      break;
    	  }
      }
    }

    return k;
  }

  /**
   * Delete the first occurrence of the key, usually the first 
   * object entered into the data structure with a particular key.
   */
  public Key<E> del(Key<E> key)
  {
    Key<List<Key<E>>> lkey = tree.find(key);
    Key<E> k = null;

    List<Key<E>> l = null;

    if ( lkey != null ) {
      l = lkey.getData();
      k = l.del(0);

      /* Delete empty lists. */
      if ( l.size() == 0 )
        tree.del(k);
    }

    return k;
  }


  public Key<E> find(Key<? extends Object> key)
  {
    Key<List<Key<E>>> klist = tree.find(key);
    Key<E> k = null;

    if ( klist != null )
      k = klist.getData().get(0);

    return k;
  }

  public int size() { return tree.size() ; }
}
