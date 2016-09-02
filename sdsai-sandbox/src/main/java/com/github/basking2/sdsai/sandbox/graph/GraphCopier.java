/* $Id: GraphCopier.java 312 2006-07-03 22:20:47Z sam $ */

/**
 * Copying an directed graph efficiently seemed 
 * sufficiently complicated to make a seperate class
 * for the purpose.<p>
 * This will duplicate a connected graph.
 */
package com.github.basking2.sdsai.sandbox.graph;

import com.github.basking2.sdsai.Key;

import com.github.basking2.sdsai.Set;

public class GraphCopier 
{
  /**
   * Place to put a list of all nodes in the graph.
   */
  Node[] src;
  
  /** 
   * Copy of the graph.  Initially null.
   */
  Node[] cpy;

  /**
   * Several start points can be given.  In the case of neural nets,
   * just give the ingress edge list.<p>
   * Note that the visited boolean must be false for all nodes.  It is used
   * here.<p>
   * NOTE: So long as nodes in the graph are not replaced or removed,
   * this object can just sit around and copy() can be called as needed.
   */
  public GraphCopier(Node[] startPoints)
  {
    cpy               = null;
    Set<Node> nodeSet = new Set<Node>();

    for(int i=0 ; i < startPoints.length ; i++)
      populate(nodeSet, startPoints[i]);

    src = new Node[nodeSet.size()];

    int i = 0;

    for ( Key<Node> n : nodeSet ) {

      src[i]  = n.getData();   /* convert this thingy! */
      src[i++].clearVisited(); /* UNSET visited after all this!*/

    }
  }

  /**
   * Used primarily by the constructor to 
   * recursivly walk through the graph and get
   * all the nodes given some root set. Note that all nodes
   * have their <i>visited</i> flag set after this method is called.
   */
  private void populate(Set<Node> v, Node n)
  {
    if(!n.visited()){

      n.setVisited();

      v.add(new Key<Node>(n.hashCode(), n));

      Node[] neighbors = n.getNeighbors();

      /**
       * Recursivly walk through unvisited nodes.
       * NOTE: We DO check for nulls.
       */
      for(int i=0;i<neighbors.length;i++)
        if(neighbors[i]!=null)
          populate(v,neighbors[i]);
    }    
  }

  /**
   * Copy the nodes from src to dst.
   * Then (in an E*V*V fasion) add all the
   * edges between them.<p>
   * <b>Ugly Details</b> - 
   * What happens if first, all the nodes in <i>src</i> are copied 
   * (using <i>copy()</i>) and the new nodes are given empty
   * edge lists.  Note that if the original node is NOT a directed
   * graph node (determined via a call to <i>isDirected()</i>) then
   * the new node is given only ONE new Vector for BOTH the in and out
   * edge sets.  That is, when one changes, they both change.<p>
   * After this the edges must be added for each new node using
   * <i>copyEdgeSet(...)</i>.  If the graph is NOT directed, then only
   * the in-edges are copied, because there should be NO difference
   * between in and out edges, right? If the graph or node is directed,
   * then both the out-edge set is also copied.
   * Note that in this implementation you can (technically) have a node
   * that is directed and a node that does NOT behave as a directed node
   * in the same graph.  Maybe I shouldn't have mentioned that that... :-)
   */
  public void copy(){
    cpy = new Node[src.length];

    /* Copy nodes */
    for(int i=0;i<src.length; i++){
      cpy[i]=src[i].copy();
      cpy[i].setEdges(new Set<Edge>()); /* empty the edge set */

      /**
       * If its a directed graph node, in and out edges
       * are DIFFERENT sets.
       */
      if(src[i].isDirected())
        cpy[i].setOutEdges(new Set<Edge>());
    }

    /* Copy edges among nodes */
    for(int i=0;i<src.length;i++){
      Edge[] e=src[i].getInEdges();
      copyEdgeSet(e,cpy[i],false);

      if(src[i].isDirected()){
        e=src[i].getOutEdges();
        copyEdgeSet(e,cpy[i],true);
      }
    }

    /* unset the visited flag set by copyEdgeSet */
    for(int i=0;i<cpy.length;i++)
      cpy[i].clearVisited();
  }
   
  /** 
   * Will copy an edge set into a node.
   * This is used by copy() and makes use of cpy array for
   * setting edge terminals appropriately.
   * Nodes which are processed by this are marked as visited.
   * If nodes at the oppsing ends of edges are marked as visited,
   * the associated edge is NOT added, as it is assumed to be
   * added already.
   * @param e set of edges from original node.
   * @param n the copy of a node. N is assumed to have an empty set of edges.
   * @param nIsSource indicates that n is the <i>source</i> node.
   * Thus, the edge set is an OutEdge set, relative to that node.
   * This does not make a whole lot of difference if the
   * graph is undirected. :-)
   */
  private void copyEdgeSet(Edge[] e, Node n, boolean nIsSource){

    Edge copiedEdge; /* guess. :-) */
    Node otherEnd;   /* node which should be at opposing end of an edge.*/

    /* Copy the Edges. "FOR ALL EDGES j..." */
    for(int i=0; i<e.length; i++){
      
      /* Get copied node that SHOULD be opposing this copied node */
      if(nIsSource)
        otherEnd = get(e[i].getDestination());
      else
        otherEnd = get(e[i].getSource());
      
      if(otherEnd==null){

        /* Shallow-copy the edge*/
        copiedEdge = e[i].copy();
        
        if(nIsSource)
          copiedEdge.setSource(n);
        else
          copiedEdge.setDestination(n);
        
        /* IF the other node hasn't been visted/copied... */
      } else if(!otherEnd.visited()){
        
        /* Shallow-copy the edge. */
        copiedEdge = e[i].copy();
        
        /* Set the edges of copiedEdge to the correct values */
        if(nIsSource){
          copiedEdge.setSource(n);
          copiedEdge.setDestination(otherEnd);
        } else {
          copiedEdge.setSource(otherEnd);
          copiedEdge.setDestination(n);
        }
      }
    } // close copy of in-edges

    /**
     * set visited so that we don't RE-ADD edges to this node.
     * Otherwise we get a double connected copy of the graph!
     */
    n.setVisited();
  }
  
  public Node[] getSrcNodes(){ return src; }
  public Node[] getCpyNodes(){ return cpy; }

  /**
   * Given a node, src is scanned and the corrosponding
   * node in cpy is returned.
   * If cpy is null, n is null,
   * or if the node is  not found, this returns null.
   */
  public Node get(Node n){

    if(cpy!=null && n!=null)
      for(int i=0;i<src.length;i++)
        if(src[i]==n)
          return cpy[i];

    return null;
  }

  /**
   * Given an edge, the edge in the copied graph which connects the
   * same two nodes in the SAME direction and has the same weight
   * is returned.  If all these condidtions are not met, then the node
   * is considered to be a different node.
   */
  public Edge get(Edge e){
    Edge[] eSet;

    /* Get SOME edge set that has the correct edge in it*/
    if(e.getSource()!=null)
      eSet = get(e.getSource()).getEdges();
    else if(e.getDestination()!=null)
      eSet = get(e.getDestination()).getEdges();
    else 
      return null;

    for(int i=0; i<eSet.length; i++){
      if(eSet[i].getWeight()==e.getWeight()&&
         eSet[i].getSource()==get(e.getSource())&&
         eSet[i].getDestination()==get(e.getDestination()))
        return eSet[i];
    }
    return null;
  }
}
