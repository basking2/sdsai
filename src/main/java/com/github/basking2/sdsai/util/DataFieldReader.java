package com.github.basking2.sdsai.util;

import java.io.IOException;
import java.io.InputStream;

import com.github.basking2.sdsai.io.IOUtils;

public class DataFieldReader
{
  private InputStream i;
  
  public DataFieldReader(InputStream i)
  {
    this.i = i;
  }
  
  public void read(DataField d)
  throws IOException
  {
    byte[] lenbytes = new byte[12];
    byte[] field;
    int    len      = 0;
    int    tmp;
    
    
    if ( 4 != ( tmp = IOUtils.mustread(i, lenbytes, 0, 4) ) )
      throw new IOException("Could only read "+ tmp +" bytes of 4 byte length.");

    // decode the field length and re-size the byte buffer.
    len = PrimitiveByteConverter.toInt(lenbytes, 0);
    
    // Almost all data fields' byte buffers are length 12.
    // If the data field length is not 8 (8+4=12) then resize
    // the field. Otherwise, the field can be the lenbytes array.
    if ( len > 8 ) {
      // copy over field.
      field    = new byte[len+4];
      field[0] = lenbytes[0];
      field[1] = lenbytes[1];
      field[2] = lenbytes[2];
      field[3] = lenbytes[3];
    } else {
      field = lenbytes;
    }

    if ( len != ( tmp = IOUtils.mustread(i, field, 4, len ) ) )
      throw new IOException("Could only read "+tmp+" bytes of a "+len+" byte field.");
    
    d.setBytes(field);
  }
  
}
