package org.sdsai.util;

import java.io.IOException;
import java.io.OutputStream;

public class DataFieldWriter
{
  private OutputStream o;
  
  public DataFieldWriter(OutputStream o)
  {
    this.o = o;
  }
  
  public void write(DataField df)
  throws IOException
  {
    o.write(df.getBytes(), 0, df.getTotalLength());
  }
  
}
