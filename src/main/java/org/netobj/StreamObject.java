/* $Id: StreamObject.java 663 2008-05-01 21:40:35Z sbaskin $ */

package org.netobj;

import java.io.*;

/**
 * Represents data that must be stored on disk or 
 * actively consumed. This object does not hold any data but an input
 * stream reference. When the write method is called a single 
 * segment object is generated and sent over the given output stream.
 * When the end of the given input stream is detected the type of this
 * object is changed from NetObject.STREAM_SEG to NetObject.LAST_STREAM_SEG
 * and the wasLastSegment() call will return true.
 */
public class StreamObject extends NetObject
{
  protected InputStream ins;
  protected int         segSize;
  protected byte[]      header;
  protected int         segCount;

  /** 
   * Pass in a file name. 
   */
  public StreamObject(String name, InputStream ins)
  {
    super(name, STREAM_SEG);
    this.ins      = ins;
    this.segSize  = 102400;
    this.segCount = 0;
  }

  public StreamObject()
  {
    super();
    ins    = null;
    type    = STREAM_SEG;
    segSize = 102400;
  }

  /**
   * Write a single data segment of the InputStream
   * used to construct this object. If the end of the input
   * stream is reached then the type is changed from
   * STREAM_SEG to LAST_STREAM_SEG.
   */
  public void write(OutputStream o) throws IOException
  {
    int     read   = 0;
    int     offset = 0;
    byte[]  data   = new byte[segSize];

    // Init the header if it isn't already
    if ( header == null ) 
    {
      byte[]  header = new byte[10];
      
      /* Write type id. */
      header[0] = (byte)((type & 0xff000000) >>> 24);
      header[1] = (byte)((type & 0x00ff0000) >>> 16);
      header[2] = (byte)((type & 0x0000ff00) >>>  8);
      header[3] = (byte) (type & 0x000000ff)       ;

      /* Write object name length. */
      header[4] = (byte)((name.length() & 0x000000ff) >>> 8 );
      header[5] = (byte) (name.length() & 0x000000ff)        ;
    }
    
    do 
    {
      read    = ins.read(data, read, data.length - offset);
      offset += read;
    } while( read > 0 && offset < data.length);
    
    // If we hit the end of the input stream...
    if ( read < 0 )
    {
      offset++;               // put back the lost byte (offset += read)...
      type = LAST_STREAM_SEG; // change the type...
    }
    
    // Put the read data length into the header. 
    header[6] = (byte)((offset & 0xff000000) >>> 24 );
    header[7] = (byte)((offset & 0x00ff0000) >>> 16 );
    header[8] = (byte)((offset & 0x0000ff00) >>>  8 );
    header[9] = (byte) (offset & 0x000000ff)         ;
    
    // Write the header.
    o.write(header);

    // Write the name 
    o.write(name.getBytes());

    // Write the data. 
    o.write(data, 0, offset);
    
    segCount++;
  }
  
  /**
   * Returns true if the last call to write(OutputStream) sent the final
   * segment and the InputStream was detected to have been at the end.
   * @return
   */
  public boolean wasLastSegment()
  {
    return type == LAST_STREAM_SEG;
  }
  
  /**
   * Repeatedly call write(OutputStream) until the end of the input stream
   * is reached.
   * @param o write the generated segments to.
   * @return the count of how many segments were sent.
   */
  public int writeAll(OutputStream o) throws IOException
  {
    while ( ! wasLastSegment() )
    {
      write(o);
    }
    
    return segCount;
  }
  
  public int getSegCount()
  {
    return segCount;
  }
}
