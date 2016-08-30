/* $Id: Key.java 666 2008-05-02 14:19:45Z sbaskin $ */

package com.github.basking2.sdsai;

/**
 * This is a more general version of a key.  This
 * represents a shift in thinking in the implementation of this
 * library... we will see what follows.
 * <p>The Key class lets the user specify an arbitrary length
 * value which is comparable with other keys.  Unlike an EObject,
 * a key is a separate value associated with a chunk of data.
 * This prevents the EObject from changing what its key is without
 * being reinserted into the data structure.
 * <p>
 * NOTE: When keys are of equal length, the 0 index of the byte array
 * is the most significant.  Since java is bigendian, this means
 * that integers sort correctly. It also means that you can compose
 * keys together to qualify different objects while maintaining
 * a key's location in a range.
 * <p>
 * NOTE: Keys are compared like words in an English dictionary.
 */
public class Key<D> implements Comparable<Key<? extends Object>>
{
  protected byte[] value;

  /**
   * This is optional satellite data to associate with the key.
   */
  protected D data;

  public Key()         { value = new byte[1]; }

  public Key(String s) { value = s.getBytes(); }

  public Key(String s, D o) { data  = o; value = s.getBytes(); }

  public Key(byte[] b)
  {
    value = new byte[b.length];

    for(int i=0; i<b.length; i++)
      value[i] = b[i];

  }

  public Key(int i)
  {
    value = new byte[4];
    data  = null;

    value[0] = (byte)((i & 0xff000000)>>>24);
    value[1] = (byte)((i & 0x00ff0000)>>>16);
    value[2] = (byte)((i & 0x0000ff00)>>> 8);
    value[3] = (byte) (i & 0x000000ff);
  }

  public Key(long l)
  {
    value = new byte[8];

    value[0] = (byte)(( l & 0xff00000000000000L ) >>> 56 );
    value[1] = (byte)(( l & 0x00ff000000000000L ) >>> 48 );
    value[2] = (byte)(( l & 0x0000ff0000000000L ) >>> 40 );
    value[3] = (byte)(( l & 0xff0000ff00000000L ) >>> 32 );
    value[4] = (byte)(( l & 0xff000000ff000000L ) >>> 24 );
    value[5] = (byte)(( l & 0xff00000000ff0000L ) >>> 16 );
    value[6] = (byte)(( l & 0xff0000000000ff00L ) >>>  8 );
    value[7] = (byte) ( l & 0x00000000000000ffL );
  }

  public Key(long l, D o) { this(l); data  = o; }

  public Key(int i, D o) { this(i); data  = o; }

  public Key(byte[] b, D o) { this(b); data = o; }

 /**
  * Create a key with the same key value as parameter k but with o as
  * the object.
  * @param k the key that this key's value will be set equal to.
  * @param o the object that this key will be set to.
  */
  public Key(Key<? extends Object> k, D o) { this(k.value, o); }

  public Key(Key<? extends Object> k) { this(k.value); }

  /**
   * Compose key k with this key by appending the key value of k
   * onto this key's key value.  The new key has object o as its data value.
   */
  public Key<D> compose(D o, Key<? extends Object> k)
  {
    byte[] k1 = getByteArray();
    byte[] k2 = k.getByteArray();
    byte[] k3 = new byte[k1.length + k2.length];

    for(int i=0; i<k1.length; i++)
      k3[i] = k1[i];

    for(int i=0; i<k2.length; i++)
      k3[i+k1.length] = k2[i];

    return new Key<D>(k3, o);
  }

  /**
   * Compose key k with this key by appending the key value of k
   * onto this key's key value.  The two new keys share the data
   * object of this key.
   */
  public Key<D> compose(Key<D> k)
  {
    return compose(getData(), k);
  }

 /**
  * Return the internal byte array used to represent this key.
  */
  public byte[] getByteArray(){ return value; }

  public String toString(){ return new String(value); }
  public int    size()    { return value.length;      }
  
  /**
   * Calls Key.cmp(this, k). Used to implement Comparable.
   */
  public int compareTo(Key<? extends Object> k)
  {
    return Key.cmp(this, k);
  }

  /**
   * This is really the core comparison function. 
   * A return value of 1 means k1 is larger.  A return value of -1 means
   * k1 is smaller.  A return value of 0 means they keys are equal.
   */
  public static int cmp(Key<? extends Object> k1, Key<? extends Object> k2)
  {
    /* Should we check/not check this???
      if(k1 == k2)                      return  0;
      else if(k1 == null && k2 != null) return  1;
      else if(k1 != null && k2 == null) return -1;
    */

    int len = (k1.value.length < k2.value.length)? 
               k1.value.length : k2.value.length;
    
    for(int i=0; i<len; i++){
      if(k1.value[i] > k2.value[i]) return  1;
      if(k1.value[i] < k2.value[i]) return -1;
    }

    /* If we end up here, the only remaining difference could be
     * the length. */
    if(k1.value.length > k2.value.length) {
      return  1;
    } else if(k1.value.length < k2.value.length) {
      return -1;
    }
    /* We can't find a difference! Just return 0. :-) */
    return 0;
  }

  /** Are <i>this</i> and k not equal? */
  public boolean ne (Key<? extends Object> k) { return cmp(this, k) != 0; }

  /** Are <i>this</i> and k equal? */
  public boolean eq (Key<? extends Object> k) { return cmp(this, k) == 0; }

  /** Is <i>this</i> less than or equal to k? */
  public boolean lte(Key<? extends Object> k) { return cmp(this, k) <= 0; }

  /** Is <i>this</i> less than k? */
  public boolean lt (Key<? extends Object> k) { return cmp(this, k) <  0; }

  /** Is <i>this</i> greater than or equal to k? */
  public boolean gte(Key<? extends Object> k) { return cmp(this, k) >= 0; }

  /** Is <i>this</i> greater than k? */
  public boolean gt (Key<? extends Object> k) { return cmp(this, k) >  0; }

  public D getData(){ return data; }
  public void   setData(D o){ data = o; }

  public boolean isZero()
  {
    for(int i=0; i<value.length; i++)
      if(value[i] != 0)
        return false;
    
    return true;
  }
}
