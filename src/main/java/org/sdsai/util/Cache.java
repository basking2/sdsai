/* $Id: Cache.java 762 2008-08-20 20:23:17Z sam $ */

package org.sdsai.util;

import org.sdsai.DynamicTable;
import org.sdsai.Key;
import org.sdsai.LinkedList;
import org.sdsai.List;
import org.sdsai.RedBlackTree;

/**
 * Design
 * ------
 * We are asked to store a key k.  To do this, we store a MacroKey
 * which contains the original key, k, the expiration key and the creation
 * key.  This macro key is wrapped various keys and stored so that 
 * if k is deleted from any one table, it can be removed from all.
 *
 * The expiration and creation indicies use keys that have the
 * most significant bytes as the time values and the least significant
 * bytes as the key values. This prevents the wrong object
 * from being removed should two objects have the same
 * expiration time or creation time.
 * 
 * About The Limits
 * Expiration - each object will expire at the given expriation time.
 *              If the time is 0, the object is not expired.
 *              The value of expiration time is the time in milliseconds.
 * Size       - Only n number of objects will be cached. A size limit of 0
 *              means there is no size limit.
 * Score      - A score is some metric the user defines. It could be size
 *              or some other value.  The cache is never allowed to
 *              exceed the scoreLimit unless the scoreLimit is lowered below
 *              the current value of the score.  This will be corrected
 *              during the next prune or add operation.
 *              Negative scores are valid.
 *
 * Enforcement Rules
 * Limits are enforced by first expiring objects and then deleting the
 * next objects to expire. If we still have a problem we delete the
 * oldest objects.
 * 
 */
public class Cache<E>
{
  private RedBlackTree<MacroKey> expirationIndex; /* When things expire */
  private RedBlackTree<MacroKey> creationIndex;   /* When things where entered. */
  private RedBlackTree<MacroKey> store;           /* Keyed objects. */
  private int   score;
  private int   scoreLimit;
  private int   sizeLimit;
  private CacheListener<E> listener;

  /**
   * This is how we manage our data.
   */
  private class MacroKey
  {
    public Key<E>           originalKey;
    public Key<MacroKey>    data;
    public Key<MacroKey>    expiration;
    public Key<MacroKey>    creation;
    public long      expirationTime;
    public int       cost;
    
    public MacroKey(Key<E> k, long expirationTime, int cost)
    {
      originalKey         = k;
      this.cost           = cost;
      this.expirationTime = expirationTime;

      data       = new Key<MacroKey>(k.getByteArray(), this);
      expiration = new Key<MacroKey>(expirationTime).compose(this, k);
      creation   = new Key<MacroKey>(System.currentTimeMillis()).compose(this, k);
    }
  }

  public Cache()
  {
    expirationIndex = new RedBlackTree<MacroKey>();
    creationIndex   = new RedBlackTree<MacroKey>();
    store           = new RedBlackTree<MacroKey>();
    score           = 0;
    scoreLimit      = 0;
    sizeLimit       = 0;
    listener        = null;
  }

  /**
   * Blindly add to all tables.
   */
  private void add(MacroKey mk)
  {
    expirationIndex.add(mk.expiration);
    creationIndex.add(mk.creation);
    store.add(mk.data);
    score += mk.cost;
  }

  private void addNoExpiration(MacroKey mk)
  {
    /* expirationIndex.add(mk.expiration); */
    creationIndex.add(mk.creation);
    store.add(mk.data);
    score += mk.cost;
  }

  /**
   * Blindly del from all tables.
   */
  private void del(MacroKey mk)
  {
    expirationIndex.del(mk.expiration);
    creationIndex.del(mk.creation);
    store.del(mk.data);
    score -= mk.cost;
  }

  public Key<E> del(Key<? extends Object> k)
  {
    Key<MacroKey> keymac  = store.del(k);
    Key<E>        keyorig = null;

    if ( keymac != null ) {
      MacroKey mk = keymac.getData();
      keyorig     = mk.originalKey;

      creationIndex.del(mk.creation);
      expirationIndex.del(mk.expiration);

      score -= mk.cost;
    }

    return keyorig;
  }

  public Key<E> delOldest()
  {
    RedBlackTree<MacroKey>.RBNode min = creationIndex.min();
    Key<MacroKey> keymac  = null; /* MacroKey key. */
    Key<E>        keyorig = null; /* Original key. */
    MacroKey      mk      = null;

    if ( min != null ) {

      keymac  = creationIndex.del(min.getKey());
      mk      = keymac.getData();
      keyorig = mk.originalKey;

      store.del(mk.data);
      expirationIndex.del(mk.expiration);
  
      score -= mk.cost;
    }

    return keyorig;
  }

  public Key<E> delNextToExpire()
  {

    RedBlackTree<MacroKey>.RBNode min = expirationIndex.min();
    Key<E>        keyorig             = null;
    Key<MacroKey> keymac              = null;
    MacroKey      mk                  = null;

    if ( min != null ) {

      keymac      = min.getKey();
      mk          = keymac.getData();
      keyorig     = mk.originalKey;
      expirationIndex.del(keymac);

      store.del(mk.data);
      creationIndex.del(mk.creation);
      score -= mk.cost;

    }

    return keyorig;
  }

  /**
   * Find keys that have expired and remove them.
   */
  public void expireKeys()
  {

    List<MacroKey> lst       = new LinkedList<MacroKey>();
    Key<Object>  timestamp = new Key<Object>(System.currentTimeMillis());

    for ( Key<MacroKey> k : expirationIndex ) {

      if ( timestamp.lt(k) ) {

        lst.add(k.getData());

      } else {

        /* Stop iterating if we move to keys that aren't to expire yet. */
        break;

      }

    }

    if ( listener == null ) {

      for ( MacroKey mk : lst )
        del(mk);

    } else {

      for ( MacroKey mk : lst ) {
        del(mk);
        listener.keyExpirationEvent(mk.originalKey);
      }
    }
  }

  /**
   * Enforce the limits set on this object cache.
   * This does not expire keys.
   */
  public void prune()
  {
    /* If there is a size limit, enforce it. */
    if(sizeLimit > 0)
      enforceSizeLimit();
    
    if(score > scoreLimit)
      enforceScoreLimit();
  }

  /**
   * This iterates through the object and removes first the expired
   * keys, then the soonest to expire keys, and finally, the oldest.
   */
  public void enforceSizeLimit()
  {

    /* Delete exired keys. They shouldn't be here anyway. */
    if(size() >= sizeLimit)
      expireKeys();
    
    /* Delete next to expire keys so long as we have them. */
    while(size() >= sizeLimit && expirationIndex.size() > 0){

      Key<E> k = delNextToExpire();

      if(listener!=null)
        listener.keySizeLimitEnforcementEvent(k);
    }
    
    /* Delete, if we must, the oldest. */
    while(size() >= sizeLimit && creationIndex.size() > 0){
      Key<E> k = delOldest();

      if(listener!=null)
        listener.keySizeLimitEnforcementEvent(k);
    }
  }

  /**
   * Enforce the score limit.
   * If the score limit is reduced below the current score, this is
   * very useful.  This calls enforceScoreLimit(0).
   */
  public void enforceScoreLimit()
  { 
    enforceScoreLimit(0); 
  }

  /**
   * Enforce the score limit assuming an adjustment of cost 
   * to the current score.
   * This is useful if a user wants to make room in the cache for an
   * expensive item.
   */
  public void enforceScoreLimit(int cost)
  {
    /* There is always a score limit. Enforce it. */
    if(score + cost > scoreLimit)
      expireKeys();
    
    /* Delete next to expire keys so long as we have them. */
    while(score + cost > scoreLimit && expirationIndex.size() > 0){
      Key<E> k = delNextToExpire();

      if(listener!=null)
        listener.keyScoreLimitEnforcementEvent(k);
    }

    /* If we must, delete the oldest keys. */
    while(score + cost > scoreLimit && size() > 0){
      Key<E> k = delOldest();
    
      if(listener!=null)
        listener.keyScoreLimitEnforcementEvent(k);
    }
  }

  /**
   * Add a key that expires at expirationTime with cost cost. 
   */
  public void add(Key<E> k, long expirationTime, int cost)
  {
    MacroKey mk = new MacroKey(k, expirationTime, cost);

    /* If there is a size limit, enforce it. */
    if(sizeLimit > 0)
      enforceSizeLimit();

    /* There is always a score limit. */
    enforceScoreLimit(cost);

    /* we've done all we can to lower the score. */
    if(score + cost <= scoreLimit){

      /* Expiration times of 0 mean the object doesn't expire. */
      if(expirationTime == 0)
        addNoExpiration(mk);
      else
        add(mk);
    }
  }

  /**
   * Find and return the time when the next object will expire.
   * Returns 0 when there is no expiration pending.
   */
  public long nextExpiration()
  {
    RedBlackTree<MacroKey>.RBNode node = expirationIndex.min();
    long et = 0;

    if ( node != null )  {
      Key<MacroKey>   keymac = node.getKey();

      et = keymac.getData().expirationTime;
   }

    return et;
  }

 /**
  * Find if data is in the cache.
  */
  public Key<E> find(Key<? extends Object> k)
  {
    Key<MacroKey> keymac  = store.find(k); 
    Key<E>        keyorig = null;

    if ( keymac != null ) { 
      MacroKey mk = keymac.getData();
      
      keyorig = mk.originalKey;
    }

    return keyorig;
  }

 /**
  * Find data but expire and return null if that data has expired.
  * This is useful in some applications where the Cache is used to
  * handle data which we want to expire. If the key is detected to have
  * expired, it is removed from the cache. Think of this method as finding
  * only "fresh" data instead of possibly stale data that hasn't been removed
  * yet.  
  */
  public Key<E> findFresh(Key<? extends Object> k)
  {
    Key<MacroKey> keymac = store.find(k); 
    Key<E>        keyorig = null;

    if ( keymac != null ) {

      MacroKey mk = keymac.getData();
      
      if ( mk.expirationTime > System.currentTimeMillis() ) {

        keyorig = mk.originalKey;

      } else {

        /* Here we realze the key has expired and should not be in the Cache. */
        /* We delete it and hand the user nothing. */
        del(mk);

      }

    }

    return keyorig;
  }

  /**
   * This returns a list of the key objects still in the cache.  
   * This is complexity O(2*n) because the list of keys from the internal
   * structure must be unwrapped and replaced with the original key object.
   */
  public List<E> toList()
  {
    List<E> l = new DynamicTable<E>();

    toList(l);

    return l;
  }

  public List<Key<E>> toKeyList()
  {
    List<Key<E>> l = new DynamicTable<Key<E>>();

    toKeyList(l);

    return l;
  }

  public void toKeyList(List<Key<E>> l)
  {
    for ( Key<MacroKey> keymac : store )
      l.add(keymac.getData().originalKey);
  }
  
  public void toList(List<E> l)
  {

    for ( Key<MacroKey> keymac : store ) 
      l.add(keymac.getData().originalKey.getData());
  }
  
  public void flush()
  {
    expirationIndex.delAll();
    creationIndex.delAll();
    store.delAll();
  }


  /**
   * How big is this.
   */
  public int size(){ return store.size(); }

  public int  getScore()     { return score; }
  public int  getScoreLimit(){ return scoreLimit; }
  public int  getSizeLimit() { return sizeLimit; }

  public void setScore(int s)     { score      = s; }
  public void setScoreLimit(int s){ scoreLimit = s; }
  public void setSizeLimit(int s) { sizeLimit  = s; }

  public void setListener(CacheListener<E> l){ listener = l; }
  public CacheListener<E> getListener(){ return listener; }

}
