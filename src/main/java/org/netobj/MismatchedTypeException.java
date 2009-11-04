/* $Id: MismatchedTypeException.java 670 2008-05-02 20:42:07Z sbaskin $ */

package org.netobj;

public class MismatchedTypeException extends java.lang.Exception
{

  private static final long serialVersionUID = 1L;
  
  protected String objectName;
  protected ListObject container;
  
  public MismatchedTypeException(
      String msg, 
      String objName,
      ListObject lo)
  {
    super(msg);
    objectName = objName;
    container = lo;
  }
  
  /** 
   * @return the list object, if any, that contains the malformed object.
   */
  public ListObject getContainer() { return container; }
  
  /**
   * @return the name of the object contained
   * by the container that was of the wrong type.
   */
  public String getObjectName() { return objectName; }
}
