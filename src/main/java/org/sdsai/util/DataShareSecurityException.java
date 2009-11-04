/* $Id: DataShareSecurityException.java 633 2008-04-21 18:34:01Z sbaskin $ */

package org.sdsai.util;

//package org.fastmessaging.datashare;

public class DataShareSecurityException extends Exception
{

  private static final long serialVersionUID = 1L;

  protected String type;

  public DataShareSecurityException(String msg, String type)
  {
    super(msg);
    this.type = type;
  }

  public String getType()
  {
    return type;
  }
}
