package com.github.basking2.sdsai.util;

public class PrimitiveByteConverter
{

  public static void toBytes(short d, byte[] b, int offset)
  {
    b[offset]   = (byte)((d & 0xff00) >>> 8);
    b[offset+1] = (byte) (d & 0x00ff)       ;
  }
  
  public static void toBytes(int d, byte[] b, int offset)
  {
    b[offset]   = (byte)((d & 0xff000000) >>> 24);
    b[offset+1] = (byte)((d & 0x00ff0000) >>> 16);
    b[offset+2] = (byte)((d & 0x0000ff00) >>>  8);
    b[offset+3] = (byte) (d & 0x000000ff)       ;
  }
  
  public static void toBytes(long d, byte[] b, int offset)
  {
    b[offset]   = (byte)((d & 0xff00000000000000L) >>> 56);
    b[offset+1] = (byte)((d & 0x00ff000000000000L) >>> 48);
    b[offset+2] = (byte)((d & 0x0000ff0000000000L) >>> 40);
    b[offset+3] = (byte)((d & 0x000000ff00000000L) >>> 32);
    b[offset+4] = (byte)((d & 0x00000000ff000000L) >>> 24);
    b[offset+5] = (byte)((d & 0x0000000000ff0000L) >>> 16);
    b[offset+6] = (byte)((d & 0x000000000000ff00L) >>>  8);
    b[offset+7] = (byte) (d & 0x00000000000000ffL)        ;
  }

  public static void toBytes(double d, byte[] b, int offset)
  {
    toBytes(Double.doubleToRawLongBits(d), b, offset);
  }
  
  public static short toShort(byte[] b, int offset)
  {
    return 
        (short)(((b[offset]   << 8) & 0x0000ff00) | 
                ((b[offset+1]     ) & 0x000000ff));
  }

  public static int toInt(byte[] b, int offset)
  {
    return 
    (((b[offset]   << 24)) & 0xff000000) | 
    (((b[offset+1] << 16)) & 0x00ff0000) | 
    (((b[offset+2] <<  8)) & 0x0000ff00) | 
    (( b[offset+3]       ) & 0x000000ff);
  }

  public static long toLong(byte[] b, int offset)
  {
    return 
        ((( ((long)b[offset])   << 56 )) & 0xff00000000000000L ) | 
        ((( ((long)b[offset+1]) << 48 )) & 0x00ff000000000000L ) | 
        ((( ((long)b[offset+2]) << 40 )) & 0x0000ff0000000000L ) | 
        ((( ((long)b[offset+3]) << 32 )) & 0x000000ff00000000L ) | 
        ((( ((long)b[offset+4]) << 24 )) & 0x00000000ff000000L ) | 
        ((( ((long)b[offset+5]) << 16 )) & 0x0000000000ff0000L ) | 
        ((( ((long)b[offset+6]) <<  8 )) & 0x000000000000ff00L ) | 
        ((         b[offset+7]        )  & 0x00000000000000ffL );    
  }
  
  public static double toDouble(byte[] b, int offset)
  {
    return Double.longBitsToDouble(toLong(b, offset));
  }
}
