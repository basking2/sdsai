/* $Id: BasicKeySelection.java 633 2008-04-21 18:34:01Z sbaskin $ */

package org.sdsai;

public class BasicKeySelection implements KeySelection
{
  protected Key<? extends Object> lowkey;
  protected Key<? extends Object> highkey;

  public BasicKeySelection(Key<? extends Object> low, Key<? extends Object> high)
  {
    lowkey  = low;
    highkey = high;
  }

  public boolean inSet(Key<? extends Object> k)
  {
    return (k.gte(lowkey) && k.lte(highkey));
  }
}