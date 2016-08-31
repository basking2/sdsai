/* $Id: ObjectNotFoundException.java 26 2007-03-11 00:22:10Z sam $ */

package com.github.basking2.sdsai.netobj;

public class ObjectNotFoundException extends java.lang.Exception
{
  protected String objectName;
  protected ListObject container;
  
  private static final long serialVersionUID = 1L;
  
  public ObjectNotFoundException(
      String s, 
      String objName,
      ListObject lo) 
  {
    super(s);
    objectName = objName;
    container = lo;
  }
  
  public ListObject getContainer()
  {
    return container;
  }
  
  public String getObjectName()
  {
    return objectName;
  }
}
