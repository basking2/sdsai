/* $Id: TrainingInstance.java 281 2005-12-29 22:59:47Z sam $ */

package org.sdsai.ai.nnet;

public class TrainingInstance {
  protected double[] data;
  protected double[] target;
  protected int answer;  /* a cache of the answer for this instance */

  protected TrainingInstance(){}
  
  public TrainingInstance(double[] d, double[] t){ 
    data=new double[d.length]; 
    target=new double[t.length]; 

    for(int i=0;i<d.length;i++)
      data[i]=d[i];
    for(int i=0;i<t.length;i++)
      target[i]=t[i];

    answer=0;
    for(int i=0;i<t.length;i++){
      if(t[answer]<t[i])
        answer=i;
    }
  }

  public double[] getData()   { return data;   }
  public double[] getTargets(){ return target; }
  public int      getAnswer() { return answer; }
}
