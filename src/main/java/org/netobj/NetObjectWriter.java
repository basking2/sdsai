/* $Id: NetObjectWriter.java 365 2007-11-30 02:55:55Z sam $ */

package org.netobj;

import java.io.*;

/**
 * <p>This class provides static methods for encoding and writing data
 * objects on the fly for large arrays of data without 
 * constructing the NetObject.  For example, sometimes the user may wish 
 * to send 128K of data and does not want to have that data copied into
 * a new network object. Rather, the user would much rather just to
 * send the data right from the Array.  The same is true of large lists
 * that exist in the form of an array.
 *
 * <p>Senders for smaller, primitive data types are not written.
 * a copy into an array is needed for them. 
 */
public class NetObjectWriter 
{

  protected static void write(OutputStream o, String name, int type, 
			      byte[] data, int offset, int length)
    throws IOException
  {
    byte[] header  = new byte[10];
    byte[] nameArr = name.getBytes();
    
    /* Write type id. */
    header[0] = (byte)((type & 0xff000000) >>> 24);
    header[1] = (byte)((type & 0x00ff0000) >>> 16);
    header[2] = (byte)((type & 0x0000ff00) >>>  8);
    header[3] = (byte) (type & 0x000000ff)       ;

    /* Write object name length. */
    header[4] = (byte)((nameArr.length & 0x0000ff00) >>> 8 );
    header[5] = (byte) (nameArr.length & 0x000000ff)        ;

    /* Write data portion length. */
    header[6] = (byte)((length & 0xff000000) >>> 24 );
    header[7] = (byte)((length & 0x00ff0000) >>> 16 );
    header[8] = (byte)((length & 0x0000ff00) >>>  8 );
    header[9] = (byte) (length & 0x000000ff)         ;

    /* Write the name, then the data. */
    o.write(header);
    o.write(nameArr);
    o.write(data, offset, length);
  }


  protected static void write(OutputStream o, String name, int type, 
			      byte[] data)
    throws IOException
  {
    byte[] header  = new byte[10];
    byte[] nameArr = name.getBytes();
    
    /* Write type id. */
    header[0] = (byte)((type & 0xff000000) >>> 24);
    header[1] = (byte)((type & 0x00ff0000) >>> 16);
    header[2] = (byte)((type & 0x0000ff00) >>>  8);
    header[3] = (byte) (type & 0x000000ff)       ;

    /* Write object name length. */
    header[4] = (byte)((nameArr.length & 0x0000ff00) >>> 8 );
    header[5] = (byte) (nameArr.length & 0x000000ff)        ;

    /* Write data portion length. */
    header[6] = (byte)((data.length & 0xff000000) >>> 24 );
    header[7] = (byte)((data.length & 0x00ff0000) >>> 16 );
    header[8] = (byte)((data.length & 0x0000ff00) >>>  8 );
    header[9] = (byte) (data.length & 0x000000ff)         ;

    /* Write the name, then the data. */
    o.write(header);
    o.write(nameArr);
    o.write(data);

  }

  public static void writeStreamSeg(OutputStream o, String name, byte[] data,
				    int offset, int length)
    throws IOException
  {
    write(o, name, NetObject.STREAM_SEG, data, offset, length);
  }

  public static void writeStreamSeg(OutputStream o, String name, byte[] data)
    throws IOException
  {
    write(o, name, NetObject.STREAM_SEG, data);
  }

  public static void writeLastStreamSeg(OutputStream o, String name,
					byte[] data, int offset, int length)
    throws IOException
  {
    write(o, name, NetObject.LAST_STREAM_SEG, data, offset, length);
  }

  public static void writeLastStreamSeg(OutputStream o, String name,
					byte[] data)
    throws IOException
  {
    write(o, name, NetObject.LAST_STREAM_SEG, data);
  }

 /**
  * Send 20480 byte chunks from the input stream until there is no date
  * left. When no data is left the LAST_STREAM_SEG is sent.  Note that it is
  * possible for an empty LAST_STREAM_SEG to be sent.
  */
  public static void writeStream(OutputStream o, InputStream i)
    throws IOException
  {
    byte[] buf = new byte[20480]; /* Buffer. */
    int    off = 0;    /* Total offset of data filling the buffer.   */
    int    len = 0;    /* Length of data from the last call to read. */

    /* While the stream is not at its end, loop. */
    do {

      len = i.read(buf);
      off = len;

      /* If the buffer is not full (off <   buf.length) 
       *    and there is data left (len != -1) 
       *    keep filling the buffer */
      while(len != -1 && off < buf.length){
        len = i.read(buf, off, buf.length - off);
        off += len;
      }

      /* If there is still data left, then the buffer is full. */
      if(len != -1){

        /* Send a segment. */
        writeStreamSeg(o, "data", buf);

      } else {

        /* Fix the length. */
        off++;

        /* Send the last segment. The loop will exit. */
        writeLastStreamSeg(o, "data", buf, 0, off);

      }

    } while(len != -1);

  }

 /**
  * Send 1024000 byte chunks from the input stream until there is no date
  * left. When no data is left the LAST_STREAM_SEG is sent.  Note that it is
  * possible for an empty LAST_STREAM_SEG to be sent.
  */
  public static void writeStream(OutputStream o, InputStream i, NetObjectTransferListener listener)
    throws IOException
  {
    byte[] buf = new byte[1024000]; /* Buffer. */
    int    off = 0;    /* Total offset of data filling the buffer.   */
    int    len = 0;    /* Length of data from the last call to read. */

    /* While the stream is not at its end, loop. */
    do {

      len = i.read(buf);
      off = len;

      /* If the buffer is not full (off <   buf.length) 
       *    and there is data left (len != -1) 
       *    keep filling the buffer */
      while(len != -1 && off < buf.length){
        len = i.read(buf, off, buf.length - off);
        off += len;
      }

      /* If there is still data left, then the buffer is full. */
      if(len != -1){

        /* Send a segment. */
        writeStreamSeg(o, "data", buf);

        listener.transferEvent(buf.length);

      } else {

        /* Fix the length. */
        off++;

        /* Send the last segment. The loop will exit. */
        writeLastStreamSeg(o, "data", buf, 0, off);
        listener.transferEvent(off);

      }

    } while(len != -1);

  }

  public static void writeData(OutputStream o, String name,
			       byte[] data, int offset, int length)
    throws IOException
  {
    write(o, name, NetObject.DATA, data, offset, length);
  }

  public static void writeData(OutputStream o, String name, byte[] data)
    throws IOException
  {
    write(o, name, NetObject.DATA, data);
  }

  public static void writeString(OutputStream o, String name, String value)
    throws IOException
  {
    write(o, name,  NetObject.STRING, value.getBytes());
  }
  
}
