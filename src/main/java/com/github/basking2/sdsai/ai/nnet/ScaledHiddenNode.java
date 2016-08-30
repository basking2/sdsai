/* $Id: ScaledHiddenNode.java 670 2008-05-02 20:42:07Z sbaskin $ */

package com.github.basking2.sdsai.ai.nnet;



/**
 * This class adds a scaling edge to the HiddenNode.
 * Some times this increases accuracy and some times this just
 * confuses the NeuralNet.  It will invariably increase the wall clock time
 * of training and evaluation, though it may make some hypothesies learnable
 * where a Net without this scaling edge might not otherwise converge.
 */
public class ScaledHiddenNode extends HiddenNode {

  protected double output;
  protected int outputVersion;
  protected double sigma;
  protected int sigmaVersion;

  private class ScalarEdge extends HiddenEdge 
  {
    public ScalarEdge(HiddenNode d
    ){
      dst = d;
      d.addInEdge(this);
      weight=Math.random()/2;
      if(Math.random()>0.5)
        weight=-weight;
    }

    public double getOutput(int v){ return weight; }

    public void update(int v, double eta)
    {      
      oldweight=weight; /* store old weight */

      if(dst!=null){
        /* Calculate assuming output is always 1 */
        double m = eta*((HiddenNode)dst).getSigma(v);
        weight = weight + m + momentum/3;
        momentum = m;
      }
    }
  }

  public ScaledHiddenNode()
  {
    super();
    new ScalarEdge(this);
  }
}
