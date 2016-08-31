package com.github.basking2.sdsai.netobj;

import java.io.IOException;

public class MalformedObjectException extends IOException
{


  private static final long serialVersionUID = 1L;

  public MalformedObjectException(String msg)
  {
    super(msg);
  }
}
