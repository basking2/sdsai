/* $Id: Edge.java 281 2005-12-29 22:59:47Z sam $ */

package com.github.basking2.sdsai.graph;

public interface Edge extends GraphElement {
  
  Node getDestination();
  Node getSource();
  void setDestination(Node n);
  void setSource(Node n);
  public double getWeight();
  public Edge copy();

  /**
   * Returns null if the edge does not contain n.
   * Returns the source if n is the destination.
   * Returns the destination if n is the source.
   * @param n the node of the edge not to return.
   */
  public Node get(Node n);
}
