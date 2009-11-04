package org.sdsai.util;



/**
 * This is a super-simple length-value field encoder.
 * It maintains an internal buffer of a minimum fixed size, but it
 * may grow if ever a byte array of a greater length is set.
 */
public class DataField
{
  /**
   * This will ALWAYS have a min length of 12. 
   * It may be greater if a byte[] is used.
   */
  private byte[] data;
  
  public DataField()
  {
    data = new byte[12];
  }
  
  public DataField(byte[] data)
  {
    this.data = new byte[4+data.length];
    
    for ( int i = 0; i < data.length ; i++ )
      this.data[i+4] = data[i];
  }
  
  public DataField(byte data)
  { 
    this();    
    setValue(data);
  }
  
  public DataField(short data)
  {
    this();    
    setValue(data);
  }

  public DataField(int data)
  {
    this();    
    setValue(data);
  }

  public DataField(long data)
  {
    this();    
    setValue(data);
  }

  public DataField(double data)
  {
    this();    
    setValue(data);
  }

  /**
   * Return the length of the data section of the field, alone.
   * @return
   */
  public int getDataLength()
  {
    return PrimitiveByteConverter.toInt(data, 0);
  }
  
  /**
   * Return the length of the data field plus the 4-byte length.
   * @return
   */
  public int getTotalLength()
  {
    return PrimitiveByteConverter.toInt(data, 0) + 4;
  }
  
  /**
   * Return the byte buffer. This will be a minimum of 12 bytes long.
   * @return
   */
  protected byte[] getBytes()
  {
    return data;
  }
  
  protected void setBytes(byte[] data)
  {
    this.data = data;
  }
  
  /**
   * Alter the data held in this object, possibly having to increase its size.
   * 
   * @param d
   */
  public void setValue(byte[] d)
  {
    if ( d.length > 8 && d.length > data.length-4)
      data = new byte[4+d.length];
    
    for ( int i = 0; i < d.length; i++ )
      data[4+i] = d[i];
    
    PrimitiveByteConverter.toBytes(d.length, data, 0);
  }
  
  public void setValue(boolean b)
  {
    setValue((byte) ( (b)? 1 : 0 ) );
  }
  
  public void setValue(byte d)
  {
    PrimitiveByteConverter.toBytes(1, data, 0);
    data[4] = d;
  }

  public void setValue(short d)
  {
    PrimitiveByteConverter.toBytes(2, data, 0);
    PrimitiveByteConverter.toBytes(d, data, 4);
  }

  public void setValue(int d)
  {
    PrimitiveByteConverter.toBytes(4, data, 0);
    PrimitiveByteConverter.toBytes(d, data, 4);
  }

  public void setValue(long d)
  {
    PrimitiveByteConverter.toBytes(8, data, 0);
    PrimitiveByteConverter.toBytes(d, data, 4);
  }

  public void setValue(double d)
  {
    PrimitiveByteConverter.toBytes(8, data, 0);
    PrimitiveByteConverter.toBytes(d, data, 4);
  }
  
  public boolean toBoolean()
  {
    return data[4] != 0;
  }
  
  public byte toByte()
  {
    return data[4];
  }
  
  public short toShort()
  {
    return PrimitiveByteConverter.toShort(data, 4);
  }

  public int toInt()
  {
    return PrimitiveByteConverter.toInt(data, 4);
  }

  public long toLong()
  {
    return PrimitiveByteConverter.toLong(data, 4);
  }

  public double toDouble()
  {
    return PrimitiveByteConverter.toDouble(data, 4);
  }
  
  public byte[] toByteArray()
  {
    byte[] b = new byte[getDataLength()];
    
    for ( int i=0 ; i < b.length ; i++ )
      b[i] = data[4+i];
    
    return b;
  }
}
