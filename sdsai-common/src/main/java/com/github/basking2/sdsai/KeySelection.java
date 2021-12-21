/**
 * Copyright (c) 2008-2021 Sam Baskinger
 *
 * $Id: KeySelection.java 633 2008-04-21 18:34:01Z sbaskin $
 */

package com.github.basking2.sdsai;

/**
 * This is an interface to objects that represent
 * sets of keys.
 */
public interface KeySelection
{
  boolean inSet(Key<? extends Object> k);
}