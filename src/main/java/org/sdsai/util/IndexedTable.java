/* $Id: IndexedTable.java 659 2008-05-01 21:15:45Z sbaskin $ */

package org.sdsai.util;

import org.sdsai.Key;
import org.sdsai.RedBlackTree;

/**
 * An IndexedTable is a table of Keys that can be accessed in log(n) time
 * by the Key value or log(n)*log(n) time by one of the named indexes it has.
 * The ideas is to store a large number of values and be able to look them up
 * quickly.
 */
public class IndexedTable<E>
{

 /**
  * Stores BTree2s indexed by their unique name.
  * IndexTable, itself, need not be a BTree2.
  */
  private RedBlackTree<RedBlackTree<MultiKey>>   indexTable = new RedBlackTree<RedBlackTree<MultiKey>>();

 /**
  * Stores MultiKeys indexed by the reference key of an object.
  * This is the single, global index of things in an IndexedTable.
  */
  private RedBlackTree<MultiKey>  refTable   = new RedBlackTree<MultiKey>();

 /**
  * A Reference object is a key and a table.
  * This is used by the MultiKey to track where an object is referenced.
  */
  public class Reference
  {
    public Key<MultiKey>          key;
    public RedBlackTree<MultiKey> index;
    public Reference(Key<MultiKey> k, RedBlackTree<MultiKey> i)
    { key = k; index = i; }
  }

 /**
  * A MultiKey is a key that contains the original key of an object
  * (called the reference key) and any other existing keys for 
  * any included indexes.
  */
  public class MultiKey
  {

   /**
    * The original user-submitted key and object. 
    */
    protected Key<E>  refKey;

   /**
    * This is a table of all the indexes that this MultiKey is listed in.
    * The index is filed under the same key which it is filed in
    * indexTable in the enclosing class.
    */
    protected RedBlackTree<Reference> indexes = new RedBlackTree<Reference>();

    public MultiKey(Key<E> refKey) { this.refKey = refKey; }
  }

  @SuppressWarnings("unchecked")
  public void add(Key<E> id, Key<? extends Object> ik, String indexName)
  {
    add(id, new Key[]{ ik }, new String[]{ indexName });
  }

 /**
  * Insert the data object in Key <i>id</i> into the index named 
  * <i>indexName</i> under the key <i>ik</i>.
  * Note that id must be unique.
  * @param id the key that uniquely identifies the object contained in that id
  *           in the entire IndexedTable.
  * @param ik the index key.
  * @param indexName the name of the index that ik will be inserted into.
  */
  public void add(Key<E> id, Key<? extends Object>[] ik, String[] indexName)
  {
    /* Get the multi key for this object (if it exists) */
    Key<MultiKey> refTabKey = refTable.find(id);

    MultiKey multiKey;

    if ( refTabKey == null ){

      /* Build a new MultiKey for this new object and insert it. */
      multiKey = new MultiKey(id);
      refTable.add(new Key<MultiKey>(id.getByteArray(), multiKey));
    
    } else {

      multiKey = refTabKey.getData();

    }



    for ( int i = 0; i < ik.length; i++ ) {

     /* Find the index (if it exists) to insert the key into. */
      Key<RedBlackTree<MultiKey>> indexKey = 
        indexTable.find(new Key<MultiKey>(indexName[i]));

      /* This will be the index we are adding the ik (index key) elements to. */
      /* This is the data object held in the indexKey we try to find in 
       * the indexTable. */
      RedBlackTree<MultiKey> index;

      /* If the table index does not exist, build it. */
      if ( indexKey == null ) { 

        index    = new RedBlackTree<MultiKey>();
        indexKey = new Key<RedBlackTree<MultiKey>>(indexName[i], index);
        indexTable.add(indexKey);

      } else {

        index = indexKey.getData();

      }

      /* Create a key that contains the multi key from the index key (ik[i])
       * that we want to file the object in under the index. */
      Key<MultiKey> mkk = new Key<MultiKey>(ik[i].getByteArray(), multiKey);

     /** 
      * At this point we have:
      *   1. our index to put the key in,
      *   2. a MultiKey to chain back to the original user key (and data),
      *   3. and a unique reference key to store the MultiKey under
      * So lets tie them all together...
      */

      /* Add index and ik to the multiKey so we can find this ik later. */
      multiKey.indexes.add(new Key<Reference>(indexKey, new Reference(mkk, index)));
    
      /* Add ik to the index so we can look it up by its new value. */
      index.add(mkk);
    }
  }

 /**
  * Delete key from the specified index.
  * The reference key is returned.
  * NOTE: No array based version of this method exists because unless a 
  * specific key object is being deleted many reference keys may be removed
  * as their index keys will match.
  * @param ik the user's key.
  * @param indexKey the key of the index in the indexTable.
                    This is used to delete empty indexes.
  * @param index the index object referenced by indexKey.
  */
  protected Key<E> del(Key<? extends Object> ik, Key<RedBlackTree<MultiKey>> indexKey, RedBlackTree<MultiKey> index)
  {
    Key<E>        returnKey = null;
    Key<MultiKey> multiKey  = null;

    /* Find the true key to return in the global index. */
    multiKey = index.del(ik);

    if ( multiKey != null ) {

      MultiKey mk = multiKey.getData();
      returnKey   = mk.refKey;

      /* Remove the index from the multiKey's list of indexes. */
      mk.indexes.del(indexKey);

      /* If this MultiKey is listed in NO other indexes, remove it from
       * the table of reference keys. */
      if ( mk.indexes.size() == 0 )
        refTable.del(returnKey);

      /* If the index is empty, remove it from the index table. */
      if ( index.size() == 0 )
        indexTable.del(indexKey);

    }

    /* Finally, give the user the key they deleted. */
    return returnKey;
  }

 /**
  * Remove the key ik from the given index.
  * Necessarily the returned key and ik are the same object unless ik is not
  * found.
  * @param mk the key object the user wants deleted.
  * @param indexKey the key value of the index from which the value k is being
  *                 deleted.
  * @param index    the index from which ik is being removed.
  */
  protected Key<MultiKey> delObj(Key<MultiKey> mk, Key<? extends Object> indexKey, RedBlackTree<MultiKey> index)
  {
    mk  = index.delObj(mk);

    if ( mk != null ) {

      MultiKey multiKey = mk.getData();

      if ( multiKey.indexes.size() == 0 )
        refTable.del(multiKey.refKey);

      if ( index.size() == 0 )
        indexTable.del(indexKey);
    }

    return mk;
  }

 /**
  * To save time on building keys for index names the user may wish to call
  * build their own index key by calling new Key("Index Name").
  */
  public Key<E> del(Key<? extends Object> k, Key<? extends Object> ik)
  {
    Key<RedBlackTree<MultiKey>> indexKey = indexTable.find(ik);
    Key<E>            returnKey = null;

    /* If we find the index, we keep processing. Otherwise we are done. */
    if ( indexKey != null ) {

      returnKey   = del(k, indexKey, indexKey.getData());

    }

    return returnKey;
  }

  public Key<E> del(Key<? extends Object> k, String indexName)
  {
    /**
     * ik       = index key of an object in an index.
     * indexKey = the key of an index in the indexTable. 
     */
    Key<RedBlackTree<MultiKey>> indexKey  = indexTable.find(new Key<String>(indexName));
    Key<E>                      returnKey = null;

    /* If we find the index, we keep processing. Otherwise we are done. */
    if ( indexKey != null ) {

      returnKey   = del(k, indexKey, indexKey.getData());

    }

    return returnKey;
  }

 /**
  * Calls delete on all the indexes a key is in.
  * Either null or the reference key is returned.
  * Note that if there are more than 1 keys with the same value, only 
  * one is removed.
  */
  public Key<E> del(Key<? extends Object> rk)
  {
    Key<MultiKey> mk           = refTable.del(rk);
    MultiKey      multiKey     = null;
    Key<E>        refKey       = null;

    if ( mk != null ) {

      multiKey = mk.getData();     /* Get the multi key. */
      refKey   = multiKey.refKey;  /* Get the origianl reference key. */

      /* For each reference to refkey, remove it. */
      for ( Key<Reference> r : multiKey.indexes ) {

        Reference ref = r.getData();

        /* Remove the key from the index. */
        ref.index.delObj(ref.key);

        /* If the index is zero, remove it from our list of indexes. */
        if ( ref.index.size() == 0 ) { refTable.del(r); }

      }
    }

    return refKey;
  }

 /**
  * Return the refernce key, that is the original key and data
  * the user submitted using an index key in a named index.
  * If no value is found then null is returned.
  */
  public Key<E> find(Key<? extends Object> ik, String indexName)
  {
    /**
     * ik       = index key of an object in an index.
     * indexKey = the key of an index in the indexTable. 
     */
    Key<RedBlackTree<MultiKey>> indexKey = indexTable.find(new Key<String>(indexName));

    Key<E> returnKey = null;

    /* If we find the index, we keep processing. Otherwise we are done. */
    if ( indexKey != null ) {

      /* Get the index. */
      RedBlackTree<MultiKey> index = indexKey.getData();

      /* Replace the user's index key w/ the index key in the index. */
      Key<MultiKey> mk = index.find(ik);

      if ( mk != null )
        returnKey   = mk.getData().refKey;

    }

    return returnKey;
  }

 /**
  * Find the original reference key and data given a reference key.
  */
  public Key<E> find(Key<? extends Object> rk)
  {
    Key<MultiKey> mk     = refTable.find(rk);
    Key<E>        refKey = null;

    if ( mk != null ) 
      refKey = mk.getData().refKey;

    return refKey;
  }


 /** Returns the size of the reference table. */
  public int size() { return refTable.size(); }

  public static void main(String[] argv)
  {
    IndexedTable<String> it = new IndexedTable<String>();

    Key<String> hi = new Key<String>("hi", "hi");
    Key<String> bye = new Key<String>("bye", "bye");
    Key<String> hello = new Key<String>("hello", "hello");

    Key<String> k1 = new Key<String>(1);
    Key<String> k2 = new Key<String>(2);
    Key<String> k3 = new Key<String>(3);


    it.add(hi,    k1, "order");
    it.add(bye,   k2, "order");
    it.add(bye,   k2, "order");
    it.add(hello, k3, "order");
    it.add(hi,    k3, "backwards");
    it.add(bye,   k2, "backwards");
    it.add(bye,   k2, "backwards");
    it.add(bye,   k1, "backwards"); /* <-- this messes it up!! */
    it.add(hello, k1, "backwards");

    Key<String> k;

    k = it.find(k1, "order");     pln("f "+(k==null?null:k.getData()));
    k = it.find(k1, "backwards"); pln("f "+(k==null?null:k.getData()));
    k = it.del(k1, "backwards");  pln("d "+(k==null?null:k.getData()));
    k = it.find(k1, "backwards"); pln("f "+(k==null?null:k.getData()));

    k = it.find(hi);              pln("f "+(k==null?null:k.getData()));
    k = it.find(bye);             pln("f "+(k==null?null:k.getData()));
    k = it.find(hello);           pln("f "+(k==null?null:k.getData()));

    it.del(bye);                  pln("Deleted all of "+bye.getData());
    k = it.find(bye);             pln("f "+(k==null?null:k.getData()));
    k = it.find(k2, "order");     pln("f "+(k==null?null:k.getData()));
    k = it.find(k2, "backwards"); pln("f "+(k==null?null:k.getData()));


  }

  public static void pln(String s) { System.out.println(s); }
  public static void p(String s) { System.out.print(s); }
}


