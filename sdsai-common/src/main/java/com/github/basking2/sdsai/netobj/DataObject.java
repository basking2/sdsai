/* $Id: DataObject.java 358 2007-11-19 20:58:29Z sam $ */

package com.github.basking2.sdsai.netobj;

import java.io.*;

public class DataObject extends NetObject
{
  protected byte[] data;

  public DataObject(String name, int type, byte[] data)
  {
    this.type = type;
    this.name = name;
    this.data = data;
  }

  public DataObject(String name, byte[] data)
  {
    this.type = DATA;
    this.name = name;
    this.data = data;
  }

  public DataObject(String name, String data)
  {
    this.type = STRING;
    this.name = name;
    this.data = data.getBytes();
  }

  public DataObject(String name, byte data)
  {
    this.type    = INT8;
    this.name    = name;
    this.data    = new byte[1];
    this.data[0] = data;
  }

  public DataObject(String name, short data)
  {
    this.type    = INT16;
    this.name    = name;
    this.data    = new byte[2];
    this.data[0] = (byte)((data & 0xff00) >>> 8);
    this.data[1] = (byte) (data & 0x00ff)       ;
  }

  public DataObject(String name, int data)
  {
    this.type = INT32;
    this.name = name;
    this.data    = new byte[4];
    this.data[0] = (byte)((data & 0xff000000) >>> 24);
    this.data[1] = (byte)((data & 0x00ff0000) >>> 16);
    this.data[2] = (byte)((data & 0x0000ff00) >>>  8);
    this.data[3] = (byte) (data & 0x000000ff)       ;
  }

  public DataObject(String name, long data)
  {
    this.type    = INT64;
    this.name    = name;
    this.data    = new byte[8];
    this.data[0] = (byte)((data & 0xff00000000000000L) >>> 56);
    this.data[1] = (byte)((data & 0x00ff000000000000L) >>> 48);
    this.data[2] = (byte)((data & 0x0000ff0000000000L) >>> 40);
    this.data[3] = (byte)((data & 0x000000ff00000000L) >>> 32);
    this.data[4] = (byte)((data & 0x00000000ff000000L) >>> 24);
    this.data[5] = (byte)((data & 0x0000000000ff0000L) >>> 16);
    this.data[6] = (byte)((data & 0x000000000000ff00L) >>>  8);
    this.data[7] = (byte) (data & 0x00000000000000ffL)        ;
  }

  public DataObject(String name, double ddata)
  {
    long data = Double.doubleToRawLongBits(ddata);
    
    this.type    = DOUBLE;
    this.name    = name;
    this.data    = new byte[8];
    this.data[0] = (byte)((data & 0xff00000000000000L) >>> 56);
    this.data[1] = (byte)((data & 0x00ff000000000000L) >>> 48);
    this.data[2] = (byte)((data & 0x0000ff0000000000L) >>> 40);
    this.data[3] = (byte)((data & 0x000000ff00000000L) >>> 32);
    this.data[4] = (byte)((data & 0x00000000ff000000L) >>> 24);
    this.data[5] = (byte)((data & 0x0000000000ff0000L) >>> 16);
    this.data[6] = (byte)((data & 0x000000000000ff00L) >>>  8);
    this.data[7] = (byte) (data & 0x00000000000000ffL)        ;
  }

  public byte toInt8(){ return data[0]; }

  public short toInt16()
  {
    return data.length >= 2? 
      (short)(((data[0] << 8) & 0x0000ff00) | 
              ((data[1]     ) & 0x000000ff)) :
      0;
  }

  public int toInt32()
  {
    return data.length >= 4? 
      (((data[0] << 24)) & 0xff000000) | 
      (((data[1] << 16)) & 0x00ff0000) | 
      (((data[2] <<  8)) & 0x0000ff00) | 
      (( data[3]       ) & 0x000000ff) :
      0;
  }

  
  public long toInt64()
  {
    return data.length >= 8? 
      ((( ((long)data[0]) << 56 )) & 0xff00000000000000L ) | 
      ((( ((long)data[1]) << 48 )) & 0x00ff000000000000L ) | 
      ((( ((long)data[2]) << 40 )) & 0x0000ff0000000000L ) | 
      ((( ((long)data[3]) << 32 )) & 0x000000ff00000000L ) | 
      ((( ((long)data[4]) << 24 )) & 0x00000000ff000000L ) | 
      ((( ((long)data[5]) << 16 )) & 0x0000000000ff0000L ) | 
      ((( ((long)data[6]) <<  8 )) & 0x000000000000ff00L ) | 
      ((          data[7]        ) & 0x00000000000000ffL ) :
      0;
  } 

  public double toDouble()
  {
    return data.length >= 8?
        Double.longBitsToDouble(toInt64()) : 
          0;
  } 

  public String toString(){ return new String(data); }

  /**
   * Return the byte array contained in this object.
   */
  public byte[] toByteArray(){ return data; }

  public void write(OutputStream o) throws IOException
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

  public int getType(){ return type; }

}
