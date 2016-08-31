/* $Id: NetObject.java 763 2008-08-27 21:52:19Z sam $ */

package com.github.basking2.sdsai.netobj;

import java.io.*;

/**
 * This holds a name and a type.  These are the most basic requirements
 * for a Network Object.  A network object is essentially an object that
 * can be written to a stream.  The user can extend NetObject and may
 * use types 10 and above for their objects. Type 0 is reserved as
 * an undefined class while types 1-9 are used for LIST, STREAM_SEG, 
 * LAST_STREAM_SEG, INT8, INT16, INT32, INT64, STRING and DATA.
 */
public abstract class NetObject
{

 /**
  * A total list. The size indicates the
  * number of contained objects, not the size in bytes.
  */
  public static final int LIST            =   0x010001;

  public static final int STREAM_SEG      =   0x020002;
  public static final int LAST_STREAM_SEG =   0x020003;

  public static final int INT8            =   0x040004;
  public static final int INT16           =   0x040005;
  public static final int INT32           =   0x040006;
  public static final int INT64           =   0x040007;
  public static final int DOUBLE          =   0x040008;
  public static final int STRING          =   0x040009;
  public static final int DATA            =   0x04000A;
  
  public static final int IS_LIST_MASK    =   0x010000;
  public static final int IS_SEG_MASK     =   0x020000;
  public static final int IS_DATA_MASK    =   0x040000;

  protected String name;
  protected int    type;

  protected NetObject()
  {
    type = 0;
    name = "";
  }

 /**
  * Make an object with no data with the type
  * t and name n.
  */
  public NetObject(String n, int t)
  {
    type = t;
    name = n;
  }

  public String  getName(){ return name; }
  public int     getType(){ return type; }
  public boolean isList() { return 0 != ( type & IS_LIST_MASK ) ; }
  public boolean isSeg()  { return 0 != ( type & IS_SEG_MASK ); }
  public boolean isData() { return 0 != ( type & IS_DATA_MASK ); }
  

  public abstract void write(OutputStream s) throws IOException;
}
