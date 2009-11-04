/* $Id: CacheListener.java 633 2008-04-21 18:34:01Z sbaskin $ */

package org.sdsai.util;

import org.sdsai.Key;

public interface CacheListener<E>
{
  /**
   * Key removed because it has expired.
   */
  public void keyExpirationEvent(Key<E> k);

  /**
   * Key is removed because the Cache size is too big. 
   */
  public void keySizeLimitEnforcementEvent(Key<E> k);

  /**
   * Key is removed because the Cache score is too large. 
   */
  public void keyScoreLimitEnforcementEvent(Key<E> k);
  
}
