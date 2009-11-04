/* $Id: KeySelection.java 633 2008-04-21 18:34:01Z sbaskin $ */

package org.sdsai;

/**
 * This is an interface to objects that represent
 * sets of keys.
 */
public interface KeySelection
{
  public boolean inSet(Key<? extends Object> k);
}