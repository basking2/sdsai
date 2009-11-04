/* $Id: GraphNode.java 312 2006-07-03 22:20:47Z sam $ */

package org.sdsai.graph;

import org.sdsai.Key;
import org.sdsai.Set;
import org.sdsai.UFSet;

public class GraphNode extends UFSet implements Node {
    
  /**
   * Visited boolean
   */
  protected boolean v   = false;
  protected Set<Edge> inEdge  = new Set<Edge>();
  protected Set<Edge> outEdge = new Set<Edge>();

  public void delOutEdge(Edge e){ outEdge.del(new Key<Edge>(e.hashCode(), e));}
  public void delInEdge(Edge e){  inEdge.del(new Key<Edge>(e.hashCode(), e));}
    
  public boolean visited()  { return v; }
  public void setVisited()  { v=true;   }
  public void clearVisited(){ v=false;  }

  public Edge[] getOutEdges()
  { 
    Edge[] elist = new Edge[outEdge.size()];
    int    i     = 0;

    for ( Key<Edge> e : outEdge )
      elist[i++] = e.getData();

    return elist;
  }

  public Edge[] getInEdges()
  { 
    Edge[] elist = new Edge[inEdge.size()];
    int    i     = 0;

    for ( Key<Edge> e : inEdge )
      elist[i++] = e.getData();

    return elist;
  }

  public Edge[] getEdges()
  {

    int i = 0;
    Edge[] elist;

    /* If this is a directed graph, then
       the inEdge and outEdge set are different sets.
       If the graph is undirected the sets are
       the same object.
     */
    if ( isDirected() ) {

      elist = new Edge [ inEdge.size() + outEdge.size() ];

      for ( Key<Edge> e : inEdge )
        elist[i++] = e.getData();
    
      for ( Key<Edge> e : outEdge )
          elist[i++] = e.getData();

    } else {

      elist = new Edge [ inEdge.size() ];

      for ( Key<Edge> e : inEdge )
        elist[i++] = e.getData();

    }

    return elist;
  }

  /**
   * Get all neighboring nodes.  This does not respect edge
   * direction.
   */
  public Node[] getNeighbors(){
    Edge[] e = getEdges();
    Node[] n=new Node[e.length];

    for(int i=0;i<e.length;i++)
      n[i]=e[i].get(this);

    return n;    
  }

  public void setInEdges(Set<Edge> v){ inEdge = v; }
  public void setOutEdges(Set<Edge> v){ inEdge = v; }

  /**
   * This will set bot inEdges and outEdges equal to v.
   * Note that in this case, this node will behave as if it is
   * part of an undirected graph.
   */
  public void setEdges(Set<Edge> v){ inEdge=v; outEdge=v; }

  /**
   * Add an in-edge to this node.  This edge will be asked for
   * information when this node does certain computations.
   */
  public void addInEdge(Edge e){ inEdge.add(new Key<Edge>(e.hashCode(), e)); }
  
  /**
   * Adding an out-edge.  These are used in phi-computations.
   */
  public void addOutEdge(Edge e){ outEdge.add(new Key<Edge>(e.hashCode(), e)); }
  
  public Node copy(){
    Node n;
    try { n = (Node)clone(); }
    catch(CloneNotSupportedException e){ n=null; }
    return n;
  }

  public boolean isDirected(){ return inEdge != outEdge; }

}
