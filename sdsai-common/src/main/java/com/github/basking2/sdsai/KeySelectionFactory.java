/**
 * Copyright (c) 2008-2021 Sam Baskinger
 *
 * $Id: KeySelectionFactory.java 633 2008-04-21 18:34:01Z sbaskin $
 */

package com.github.basking2.sdsai;

public class KeySelectionFactory
{

  /**
   * Select all.
   */
  public static KeySelection all()
  {
    return new BasicKeySelection(null, null)
      {
        public boolean inSet(Key<? extends Object> k){ return true; }
      };
  }

 /**
  * Represents an interval of (k1, k2)
  */
  public static KeySelection GTEandLTE(Key<? extends Object> k1, Key<? extends Object> k2)
  {
    return new BasicKeySelection(k1, k2)
      {
        public boolean inSet(Key<? extends Object> k)
        {
          return (k.gte(lowkey) && k.lte(highkey));
        }
      };
  }

 /**
  * Represents an interval of [k1, k2)
  */
  public static KeySelection GTandLTE(Key<? extends Object> k1, Key<? extends Object> k2)
  {
    return new BasicKeySelection(k1, k2)
      {
        public boolean inSet(Key<? extends Object> k)
        {
          return (k.gt(lowkey) && k.lte(highkey));
        }
      };
  }

 /**
  * Represents an interval of (k1, k2]
  */
  public static KeySelection GTEandLT(Key<? extends Object> k1, Key<? extends Object> k2)
  {
    return new BasicKeySelection(k1, k2)
      {
        public boolean inSet(Key<? extends Object> k)
        {
          return (k.gte(lowkey) && k.lt(highkey));
        }
      };
  }

 /**
  * Represents an interval of [k1, k2]
  */
  public static KeySelection GTandLT(Key<? extends Object> k1, Key<? extends Object> k2)
  {
    return new BasicKeySelection(k1, k2)
      {
        public boolean inSet(Key<? extends Object> k)
        {
          return (k.gt(lowkey) && k.lt(highkey));
        }
      };
  }

  public static KeySelection gt(Key<? extends Object> k1)
  {
    return new BasicKeySelection(k1, null)
      {
        public boolean inSet(Key<? extends Object> k)
        {
          return k.gt(lowkey);
        }
      };
  }

  public static KeySelection gte(Key<? extends Object> k1)
  {
    return new BasicKeySelection(k1, null)
      {
        public boolean inSet(Key<? extends Object> k)
        {
          return k.gte(lowkey);
        }
      };
  }

  public static KeySelection lt(Key<? extends Object> k1)
  {
    return new BasicKeySelection(k1, null)
      {
        public boolean inSet(Key<? extends Object> k)
        {
          return k.lt(lowkey);
        }
      };
  }

  public static KeySelection lte(Key<? extends Object> k1)
  {
    return new BasicKeySelection(k1, null)
      {
        public boolean inSet(Key<? extends Object> k)
        {
          return k.lte(lowkey);
        }
      };
  }
}
