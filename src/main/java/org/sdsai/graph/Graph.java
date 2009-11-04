/* $Id: Graph.java 312 2006-07-03 22:20:47Z sam $ */

package org.sdsai.graph;
import org.sdsai.*;

/**
 * This is a wrapper class for working with nodes.
 * The Graph class takes much of the complexity out of dealing
 * with node and edge objects but at the cost of time.
 * The Graph class seeks to enforce definitions of a Graph.  Those being
 * that no node can be in a graph twice.  No edge can exist twice.
 * There is no edges from a node to itself and there is no more than 
 * one edge in a given direction from one node too another.
 * Note that while the Graph class tries for correctness, the users has
 * many opertunities to break that correctness.
 */
public class Graph 
{
  Set<Node> nodes;
  Set<Edge> edges;

  public Graph(){ nodes = new Set<Node>(); edges = new Set<Edge>(); }
  
  /**
   * Add and edge from start to stop.
   * If an edge already exists between start and stop that edge is returned.
   * Other wise, the new edge created from start to stop is created.
   */
  public Edge addEdge(Node start, Node stop)
  {
    Key<Node> nodekey;

    /* If we don't already have these nodes, add them. */
    nodekey = new Key<Node>(start.hashCode(), start);

    if( ! nodes.member( nodekey ) )  nodes.add(nodekey);

    nodekey = new Key<Node>(stop.hashCode(), stop);

    if( ! nodes.member( nodekey ) )  nodes.add(nodekey);

    /* Note: This works for undirected nodes too. */
    Edge e[] = start.getOutEdges();
    
    for(int i=0; i<e.length;i++)
      if(e[i].get(start)==stop) /* works for undirected nodes and directed */
        return e[i];

    Edge tmpe = new GraphEdge(start, stop);
    edges.add(new Key<Edge>(tmpe.hashCode(), tmpe));

    return tmpe;
  }
}

