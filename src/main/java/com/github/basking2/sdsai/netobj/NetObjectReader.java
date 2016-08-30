/* $Id: NetObjectReader.java 763 2008-08-27 21:52:19Z sam $ */

package com.github.basking2.sdsai.netobj;

import java.io.*;

public class NetObjectReader<DATATYPE extends DataObject, LISTTYPE extends ListObject>
{
  public static final int SIZELIMIT = 102400000;

  /**
   * This protocol is designed so that the header is constant for all messages.
   * There are 2 varyiable length feilds, but the lengths for all of them
   * are encoded in the constant size header.
   */
  public static final int HEADER_LEN = 10; 

 /**
  * Objects larger than this are put in files.
  */
  protected int sizeLimit;

  protected InputStream in;
  protected int         type;
  protected String      name;
  protected int         len;
  
  protected NetObjectFactory<DATATYPE, LISTTYPE> netObjectFactory;

  /**
   * Denotes that the header information has been read 
   * into this object. If this is false, the header must
   * be read before the data portion of the NetObject.
   */
  protected boolean     freshHeader;

  @SuppressWarnings("unchecked")
  public NetObjectReader(InputStream i)
  {
    sizeLimit   = SIZELIMIT;
    in          = i;
    freshHeader = false;
    netObjectFactory = 
      (NetObjectFactory<DATATYPE, LISTTYPE>) 
        NetObjectFactory.getDefaultFactory();
  }

  /**
   * Return the name of the object we last read/are reading.
   */
  public String getName(){ return name; }

  public int    getLen(){ return len; }
  public int    getType(){ return type; }

  /**
   * Update the header. 
   * If the header is fresh, this does nothing. If the header is not
   * fresh, the this recieves it.
   */
  public void readHeader() throws IOException, MalformedObjectException
  {
    byte[] header  = new byte[HEADER_LEN];
    byte[] namebuf;
    int read       = 0;
    int off        = 0;

    if(!freshHeader){

      do {
        read  = in.read(header, off, header.length - off);
        off  += read;

      } while(read >= 0 && off < header.length);
      
      if(read < 0)
        throw new MalformedObjectException("Failed to read header.");
      
      /* We have the header read into the header by this point. */
      
      type = 
        ((header[0] << 24) & 0xff000000) |
        ((header[1] << 16) & 0x00ff0000) |
        ((header[2] <<  8) & 0x0000ff00) |
        ( header[3]        & 0x000000ff) ;
      
      /* We borrow len for a few lines. */
      len = 
        ((header[4] <<  8) & 0x0000ff00) |
        ( header[5]        & 0x000000ff) ;
      
      namebuf = new byte[len];
      
      /* Get the data length. */
      len =
        ((header[6] << 24) & 0xff000000) |
        ((header[7] << 16) & 0x00ff0000) |
        ((header[8] <<  8) & 0x0000ff00) |
        ( header[9]        & 0x000000ff) ;
      
      
      off = 0;

      do {
        read  = in.read(namebuf, off, namebuf.length - off);
        off  += read;
      } while(read >= 0 && off < namebuf.length);
      
      if(read<0)
        throw new MalformedObjectException("Unexpected end of object name.");

      if(len > sizeLimit)
        throw new MalformedObjectException("Size limit exceeded. Size:"+
                                           len + " Limit:"+sizeLimit);
      
      name = new String(namebuf);

      freshHeader = true;

    } /* close big if statement */
  }

  /**
   * This will read a dataObject from the stream or throw an 
   * exception. It will never return null.
   */
  public DATATYPE readDataObject()
    throws IOException, MismatchedTypeException
  {
    readHeader();

    byte[] data = new byte[len];

    int off  = 0;
    int read = 0;

    if( 0 == ( type & NetObject.IS_DATA_MASK ) )
      throw new MalformedObjectException("Expecting data object.");

    if(len > 0){
      do {
      
        read  = in.read(data, off, len - off);
        off  += read;
        
      } while(read>=0 && off < len);
    }

    if(read<0)
      throw new MalformedObjectException("Unexpected end of data portion.");

    /* Mark headers as not-fresh. */
    freshHeader = false;


    return netObjectFactory.createDataObject(name, type, data);
  }

  /**
   * Recieve a list object into the provided object.
   */
  public LISTTYPE readListObject() 
    throws IOException, MismatchedTypeException
  {
    readHeader();

    LISTTYPE lo = netObjectFactory.createListObject(name, type);

    if( 0 == ( type & NetObject.IS_LIST_MASK ) )
      throw new MalformedObjectException("Expecting list object.");

    /* recieve all objects */
    /* NOTE: "len" is updated as we recieve each subsequent object. */
    for(int i=len; i>0; i--){
      
      freshHeader = false;
      readHeader();
      
      if( 0 != ( type & NetObject.IS_LIST_MASK ) ){

        lo.add(readListObject());
        
      } else if( 0 != ( type & NetObject.IS_DATA_MASK )){

        lo.add(readDataObject());

      }

    }
    
    /* We exit when we have enough objects, so the headers are NOT current. */
    freshHeader = false;

    return lo;
  }

  

  /**
   * Returns a dataobject with the type set to STREAM_SEG or LAST_STREAM_SEG.
   * The data portion is the data of the stream segment.
   */
  public DATATYPE readStreamObject() 
    throws IOException, MismatchedTypeException
  {
    readHeader();
    
    if( 0 == ( type & NetObject.IS_SEG_MASK ) )
      throw new MalformedObjectException("Expecting stream object.");
    
    int     off = 0;
    byte[] data = new byte[len];
    
    /* Read in the data section. */
    while(off < len){
      int read = in.read(data, off, len - off);

      if(read < 0)
        throw new MalformedObjectException("Unexpected end of Object.");

      off += read;
    }

    return netObjectFactory.createDataObject(name, type, data);
  }


  /**
   * Returns a dataobject with the type set to STREAM_SEG or LAST_STREAM_SEG.
   * The data portion is the data of the stream segment.
   */
  public DATATYPE readStreamObject(NetObjectTransferListener listener)
    throws IOException, MismatchedTypeException
  {
    readHeader();
    
    if( 0 == ( type & NetObject.IS_SEG_MASK ) )
      throw new MalformedObjectException("Expecting stream object.");
    
    int     off = 0;
    byte[] data = new byte[len];
    
    /* Read in the data section. */
    while(off < len){
      int read = in.read(data, off, len - off);

      if(read < 0)
        throw new MalformedObjectException("Unexpected end of Object.");

      off += read;

      listener.transferEvent(read);
    }

    return netObjectFactory.createDataObject(name, type, data);
  }

 /**
  * Recieve stream objects until a non-stream object is recieved.
  * If the last segment received is not a LAST_STREAM_SEG type
  * then a MismatchedTypeException is thrown.  If it is, the method
  * exits normally. The recieved data is written to the output
  * stream o.
  * @param o the stream to which the recieved data is written.
  */
  public void readStreamObjects(OutputStream o)
    throws IOException, MismatchedTypeException
  {
    readHeader();

    /* We know there is 1 stream segment waiting. */
    while(type == NetObject.STREAM_SEG){

      DataObject d = readStreamObject();
      
      o.write(d.toByteArray());
      
      /* This segment is done. Invalidate and update headers. */
      freshHeader = false;
      readHeader();
    }

    /**
     * Leaving the above loop, we have a fresh header that is not
     * a generic segment. 
     */

    /* Same as while loop but we are recieving the last segment. */
    if(type == NetObject.LAST_STREAM_SEG){

      DataObject d = readStreamObject();
      
      o.write(d.toByteArray());
      
      /* This segment is done. Invalidate and update headers. */
      freshHeader = false;

    } else {
      throw new MalformedObjectException("Expecting last stream segment.");
    }

  }

 /**
  * Recieve stream objects until a non-stream object is recieved.
  * If the last segment recieved is not a LAST_STREAM_SEG type
  * then a MismatchedTypeException is thrown.  If it is, the method
  * exits normally.
  * @param o the output stream to which the data is written.
  */
  public void readStreamObjects(OutputStream o, NetObjectTransferListener listener)
    throws IOException, MismatchedTypeException
  {
    readHeader();

    /* We know there is 1 stream segment waiting. */
    while(type == NetObject.STREAM_SEG){

      DataObject d = readStreamObject(listener);
      
      byte[] ba = d.toByteArray();
      o.write(ba);
      
      /* This segment is done. Invalidate and update headers. */
      freshHeader = false;
      readHeader();
    }

    /**
     * Leaving the above loop, we have a fresh header that is not
     * a generic segment. 
     */

    /* Same as while loop but we are recieving the last segment. */
    if(type == NetObject.LAST_STREAM_SEG){

      DataObject d = readStreamObject(listener);
      
      byte[] ba = d.toByteArray();
      o.write(ba);
      
      /* This segment is done. Invalidate and update headers. */
      freshHeader = false;

    } else {
      throw new MalformedObjectException("Expecting last stream segment.");
    }

  }

  /**
   * Recieves lists and data objects. 
   */
  public NetObject readNetObject() throws IOException
  {
    readHeader();
    
    NetObject no = null;
    
    try {

      if( 0 != ( type & NetObject.IS_LIST_MASK ) )
        no = readListObject();

      else if( 0 != ( type & NetObject.IS_DATA_MASK ) )
        no = readDataObject();
      
    } catch(MismatchedTypeException e) {
      // never thrown when called from here.
    }

    return no;
  }

  public InputStream getInputStream()
  {
    return in;
  }

  public NetObjectFactory<DATATYPE, LISTTYPE> getNetObjectFactory()
  {
    return netObjectFactory;
  }

  public void setNetObjectFactory(NetObjectFactory<DATATYPE, LISTTYPE> netObjectFactory)
  {
    this.netObjectFactory = netObjectFactory;
  }
}
