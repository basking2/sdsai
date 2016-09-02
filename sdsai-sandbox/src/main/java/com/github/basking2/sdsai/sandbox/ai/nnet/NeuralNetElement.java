/* $Id: NeuralNetElement.java 281 2005-12-29 22:59:47Z sam $ */

package com.github.basking2.sdsai.sandbox.ai.nnet;

import com.github.basking2.sdsai.sandbox.graph.GraphElement;

public interface NeuralNetElement extends GraphElement {

  public static final double ETA = 10;
  
  /**
   * Get output of the given version.
   */
  double getOutput(int version);
  
  /**
   * Return the error (or sigma value) of this node in the neural network.
   * Phi is dependent on the result prodced by the neural network
   * with respect to the acctual value of the result in t.
   */
  double getSigma(int v);
  
  /**
   * When called all elements <i>preceeding</i> the one this is called on
   * will be trained be getting a sigma value from the next element,
   * computing their sigma value, and calling update on the previous node.
   * @param v - v is the version of the neural net state.  If the node has
   * a different version recorded for its sigma value, it will recompute it.
   * If the versions match, then no recomputation is necessary.
   */
  void update(int v);
  
  /**
   * Like other update methods, but the <i>importance</i>, or eta
   * can be specified.
   */
  void update(int v, double eta); 
  
}
