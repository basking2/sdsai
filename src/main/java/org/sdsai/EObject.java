/* $Id: EObject.java 281 2005-12-29 22:59:47Z sam $ */

package org.sdsai;

/**
 * An EObject is an enumerable object.  This implies that the object
 * has a relative order to other EObjects.  To use objects wich order
 * things, be sure your objects implement this interface.
 */
public interface EObject extends Cloneable {
  /**
   * Return the key value for this object.  This is the value
   * that this object is sorted by.
   */
  public double getKey();

  /**
   * Set this object's key value.  To my knowledge, only decreaseKey
   * operations in heaps would ever make use of this (apart from 
   * programmers).
   */
  public void setKey(double d);
}
