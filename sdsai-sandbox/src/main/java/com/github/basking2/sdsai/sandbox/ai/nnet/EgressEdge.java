/**
 * Copyright (c) 2005-2021 Sam Baskinger
 *
 * $Id: EgressEdge.java 281 2005-12-29 22:59:47Z sam $
 */

package com.github.basking2.sdsai.sandbox.ai.nnet;


public class EgressEdge extends HiddenEdge {
  
  int outputVersion;
  double output;
  double target;
  
  public EgressEdge(HiddenNode s){
    src = s;
    dst = null;
    s.addOutEdge(this);
    weight=1;
  }
  
  public double getOutput(int v){ 
    if(outputVersion!=v){
      outputVersion=v;
      output=((HiddenNode)src).getOutput(v);
    }
    
    return output;
  }
  
  public double getSigma(int v) { 
    if(outputVersion!=v){
      outputVersion = v; 
      output = ((HiddenNode)src).getOutput(v);
    }
    return output*(1-output)*(target-output);
  }
  
  public void update(int v){ update(v, ETA); }
  public void update(int v, double eta){ ((HiddenNode)src).update(v,eta); }
  public void setTarget(double d){ target=d; }
}

