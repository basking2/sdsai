/**
 * $Id$
 * Sam Baskinger
 * This software comes with no warrenty.
 */

package org.sdsai;

public class HashTableFactory
{

  /*
  public static HashTable buildXORHash()
  {
    return new HashTable() 
    { 
      public int _hash(byte[] data)
      {
        return 0; 
      } 
    };
  }
  */

  /**
   * Hash used in SDBM project. Souce taken from http://www.partow.net/programming/hashfunctions/#SDBMHashFunction and adapted to java.
   * @param <E>
   */
  public static <E> HashTable<E> buildSDBMHash()
  {
    return new HashTable<E>()
    {  
      public int _hash(byte[] data)
      {
        int hash = 0;
        for ( int i = 0; i < data.length; i++ )
          hash = data[i] + (hash << 6) + (hash << 16) - hash;
        return hash;
      }
    };
  }

 /**
  * A hash algorithm by Donald E. Knuth.
  * Souce adapted from http://www.partow.net/programming/hashfunctions/#SDBMHashFunction.
  */
  public static <E> HashTable<E> buildDEKHash()
  {
    return new HashTable<E>()
    {  
      public int _hash(byte[] data)
      {
        int hash = data.length;
 
        for (int i = 0; i < data.length; i++)
          hash = ((hash << 5) ^ (hash >>> 27)) ^ data[i];

        return hash;
      }
    };
  }
}
