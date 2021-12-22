/**
 * Copyright (c) 2005-2021 Sam Baskinger
 *
 * $Id: HiddenEdge.java 281 2005-12-29 22:59:47Z sam $
 */

package com.github.basking2.sdsai.sandbox.ai.nnet;

import com.github.basking2.sdsai.sandbox.graph.Edge;
import com.github.basking2.sdsai.sandbox.graph.GraphEdge;

public class HiddenEdge extends GraphEdge implements NeuralNetElement {

  protected boolean undoVersion;
  double momentum;
  double oldweight;

  protected HiddenEdge(){ 
    src=null;
    dst=null; 
    weight=Math.random()/2;
    if(Math.random()>0.5)
      weight=-weight;
  }
  
  public HiddenEdge(HiddenNode s, HiddenNode d){
    src = s;
    dst = d;
    s.addOutEdge(this);
    d.addInEdge(this);
    weight=Math.random()/2;
    if(Math.random()>0.5)
      weight=-weight;
  }
  
  public double getOutput(int v){ 
    return weight * ((HiddenNode)src).getOutput(v); }

  public double getSigma(int v) { 
    return weight * ((HiddenNode)dst).getSigma(v); }
  
  public void update(int v){ update(v,ETA); }
  
  /**
   * Like update(boolean v) but eta (or the drastic-ness) of the change
   * can be specified.
   */
  public void update(int v, double eta){
    oldweight=weight;

    /* ONLY update if we connect to a node */
    if(dst!=null){

      double m = 
        eta*
        ((HiddenNode)dst).getSigma(v)*
        ((HiddenNode)src).getOutput(v);
        
      weight = weight + m + momentum/3;
      //weight = weight + m;
      
      momentum = m;
    }

    /* Propogate backwards. */
    if(src!=null)
      ((HiddenNode)src).update(v,eta);
  }
  
 /**
  * Return to a previous weight.
  */
  public void undo(){ 
    weight=oldweight;
    undoVersion=!undoVersion;
    if(src!=null){
      Edge[] e = src.getEdges();
      for(int i=0;i<e.length;i++){
        HiddenEdge he = (HiddenEdge)e[i];
        if(he.undoVersion!=undoVersion)
          he.undo();
      }
    }
  }
}
  
