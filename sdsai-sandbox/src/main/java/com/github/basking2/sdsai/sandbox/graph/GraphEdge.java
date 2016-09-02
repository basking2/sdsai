/* $Id: GraphEdge.java 281 2005-12-29 22:59:47Z sam $ */

package com.github.basking2.sdsai.sandbox.graph;
import com.github.basking2.sdsai.EObject;

public class GraphEdge implements Edge, EObject {
  
  protected Node src;
  protected Node dst;
  protected double weight;
  protected GraphEdge(){ src=null; dst=null; weight=0; }
  
  public GraphEdge(Node s, Node d){
    src = s;
    dst = d;
    s.addOutEdge(this);
    d.addInEdge(this);
    weight=Math.random()/20;
    if(Math.random()>0.5)
      weight=-weight;
  }
  
  public Node getDestination(){ return dst; }
  public Node getSource(){ return src; }
  
  /**
   * This will add this edge to the in edge set of node n.
   * This will remove itself from the previous destination node.
   */
  public void setDestination(Node n){
    if(dst!=null) dst.delInEdge(this);
    dst=n;
    dst.addInEdge(this);
  }
  
  /**
   * This will add this edgs to the out edge set of node n.
   * This will remove itself from the previous source node.
   */
  public void setSource(Node n){
    if(src!=null) src.delOutEdge(this);
    src=n;
    src.addOutEdge(this);
  }
  
  public Edge copy(){
    Edge e;
    try { e = (Edge)clone(); }
    catch(java.lang.CloneNotSupportedException c){ e = null; }
    return e;
  }
  
  public double getWeight(){ return weight; }
  
  public Node get(Node n){
    if(n==src) return dst;
    if(n==dst) return src;
    return null;
  }

  public double getKey(){ return weight; }
  public void   setKey(double k){ weight = k; }
}
  
