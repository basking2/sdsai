/* $Id: Node.java 312 2006-07-03 22:20:47Z sam $ */

package com.github.basking2.sdsai.graph;
import com.github.basking2.sdsai.Set;
public interface Node extends GraphElement
{

  /**
   * Add an in-edge to this node.  This edge will be asked for
   * information when this node does certain computations.
   */
  public void addInEdge(Edge e);
    
  /**
   * Adding an out-edge.  These are used in phi-computations.
   */
  public void addOutEdge(Edge e);
    
  public void delOutEdge(Edge e);
  public void delInEdge(Edge e);
  public Edge[] getOutEdges();
  public Edge[] getInEdges();
  public Edge[] getEdges();
  public Node[] getNeighbors();

  public void setOutEdges(Set<Edge> v);
  public void setInEdges(Set<Edge> v);
  public void setEdges(Set<Edge> v);
    
  /**
   * Check if this node has been visited or not.
   * This is here for cycle checks.
   */
  public boolean visited();
  public void setVisited();
  public void clearVisited();
  public Node copy();

  /**
   * Will return if this graph is directed or not.
   * If the graph is not directed, then in and out edges are
   * the same set.
   */
  public boolean isDirected();
}
