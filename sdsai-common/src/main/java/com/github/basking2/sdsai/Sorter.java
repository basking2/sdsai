/* $Id: Sorter.java 281 2005-12-29 22:59:47Z sam $ */

package com.github.basking2.sdsai;

/** 
 * This interface is so that any sorting structure
 * can use different sorting methods.
 */
public interface Sorter {

  /** 
   * This method will be used to sort an array of EObjects.
   * Note that sorting can be partial sorting.  Perhaps a better name for
   * this method would be "arrange."
   */
  public void sort(EObject[] toSort);
}
