package org.sdsai.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils
{
  /**
   * Loops until len bytes are read or the end of stream is reached.
   * The total number of bytes read is returned. If it does not equal
   * len, then the end of stream was reached after reading the returned
   * number of bytes into the byte buffer.
   * 
   * This is best moved to some utility class suitable for holding
   * stuff like this. Some day...
   * 
   * @param buffer buffer to read into.
   * @param len number of bytes to read.
   * @return
   * @throws IOException
   */
  public static int mustread(InputStream i, byte[] buffer, int offset, int len)
  throws IOException
  {
    int myoffset = 0;
    int bytesread;
    
    do
    {
      bytesread = i.read(buffer, offset+myoffset, len);
      
      myoffset += bytesread; 
      
    } while ( bytesread >= 0 && myoffset < len );
    
    // fix the bytes read if we hit the end-of-stream.
    if ( bytesread < 0 )
      myoffset++;
    
    return myoffset;
  }
  
  /**
   * Copy len bytes from the input stream to the output stream using the 
   * supplied buffer. The total length of bytes copied is returned.
   * If the input is exhausted before the length is satisfied, it may be detected 
   * by comparing the returned length with the supplied length.
   * @param i
   * @param o
   * @param buffer
   * @param len
   * @return
   */
  public  static long copy(final InputStream i, final OutputStream o, final byte[] buffer, long len)
  throws IOException
  {
    long written = 0;
    int  tmp;
    do {
      
      tmp = i.read(buffer, 0, (int) Math.min(buffer.length, (len - written)));
      
      if ( tmp > 0 ) {
        
        o.write(buffer, 0, tmp);
        written += tmp;
        
      } else if ( tmp == -1 ) {
        
        return written;
        
      }
      
    } while ( tmp >= 0 && written < len );
    
    return written;
  }
  
  public static long copy(final InputStream i, final OutputStream o, long len)
  throws IOException
  {
    return copy(i, o, new byte[10240], len);
  }


}
