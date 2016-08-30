/* $Id: HiddenNode.java 313 2006-07-10 02:26:03Z sam $ */

package com.github.basking2.sdsai.ai.nnet;

import com.github.basking2.sdsai.Key;
import com.github.basking2.sdsai.graph.Edge;
import com.github.basking2.sdsai.graph.GraphNode;

public class HiddenNode extends GraphNode implements NeuralNetElement
{ 
    
  protected double output;
  protected int outputVersion;
  protected double sigma;
  protected int sigmaVersion;

  
  /**
   * Computes output for this node by getting input from the in-Edge.
   */
  public double getOutput(int v)
  {
    if(v!=outputVersion){
      outputVersion = v; /* change version */
      output=0;          /* zero the output */
      
      for ( Key<Edge> key : inEdge )
        output = output + ((HiddenEdge) key.getData()).getOutput(v);
      
      /* Squash using 1/(1+(e^(-output))) */
      output = 1 / (1+Math.pow(Math.E,-output));
    }
    return output;
  }
  
  
  /**
   * Compute the error for this node.
   * This calls getOutput to ensure a current version of the output
   * exists.
   */
  public double getSigma(int v)
  {
    if(v!=sigmaVersion){
      
      sigmaVersion = v;     /* change version */
      double E=0;           /* make a variable for storing the sum. */
      double o=getOutput(v);/* get a current output for this version */
      
      /* Sum the outEdge */
      for ( Key<Edge> key : outEdge )
        E = E + ((HiddenEdge) key.getData()).getSigma(v);
      
      sigma = o*(1-o)*E;
    }
    return sigma;
  }
  
  /**
   * This update simply calls update on the inEdges.
   */
  public void update(int v){ update(v,ETA); }
  
  public void update(int v, double eta)
  {

    for ( Key<Edge> key : inEdge )
      ((HiddenEdge) key.getData()).update(v, eta);
  }
  
}
