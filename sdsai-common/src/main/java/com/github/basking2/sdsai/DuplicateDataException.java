/**
 * Copyright (c) 2008-2021 Sam Baskinger
 *
 * $Id: DuplicateDataException.java 633 2008-04-21 18:34:01Z sbaskin $
 */

package com.github.basking2.sdsai;

public class DuplicateDataException extends java.lang.Exception
{

  private static final long serialVersionUID = 1L;
	
  /**
   * When this exception is thrown, the key in the database
   * that raised the exception is returned.
   */
  protected Key<? extends Object> key;

  public DuplicateDataException(Key<? extends Object> k)
  { 
    key = k; 
  }

  public Key<? extends Object> getKey()
  { 
    return key;
  }
}
