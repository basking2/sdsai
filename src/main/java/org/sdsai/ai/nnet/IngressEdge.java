/* $Id: IngressEdge.java 670 2008-05-02 20:42:07Z sbaskin $ */

package org.sdsai.ai.nnet;

public class IngressEdge extends HiddenEdge 
{
  
  boolean outputVersion;
  double input;
  double weight;
  
  @SuppressWarnings("unused")
  private IngressEdge(){};
  
  public IngressEdge(HiddenNode d)
  {
    dst = d;
    src = null;
    weight=1;
    d.addInEdge(this);
  }
  
  public double getOutput(int v){ return weight*input; }
  
  public void setInput(double i){ input=i; }
  
  public void update(int v)
  {
    //oldweight=weight;
    //weight = weight + ETA*((HiddenNode)dst).getSigma(v)*input; 
  }
  
  public void update(int v, double eta)
  {
    //weight = weight + eta*((HiddenNode)dst).getSigma(v)*input;
  }
}
