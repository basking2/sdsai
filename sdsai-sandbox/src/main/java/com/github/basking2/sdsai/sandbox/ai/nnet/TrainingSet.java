/* $Id: TrainingSet.java 313 2006-07-10 02:26:03Z sam $ */

package com.github.basking2.sdsai.ai.nnet;

import java.util.List;
import java.util.ArrayList;

public class TrainingSet
{
  List<TrainingInstance> instance;

  public TrainingSet()
  {
    instance = new ArrayList<TrainingInstance>();
  }

  public TrainingSet(TrainingInstance ti) 
  {
    instance = new ArrayList<TrainingInstance>();
    instance.add(ti);
  }

  public TrainingSet(double[][] d, double[][] t)
  {

    /* Make new space: We do this here so that if there is some
     * asychronous work that is done before the first assignment to an
     * allocated object, it can happen while we check the target for
     * sane values. 
     */
    instance = new ArrayList<TrainingInstance>();

    /* Add the data */
    for(int i=0;i<d.length;i++)
      instance.add(new TrainingInstance(d[i],t[i]));
  }
  
  public double[] getData(int i)
  { 
    TrainingInstance t = instance.get(i);
    return t==null?null:(double[])(t.data);
  }

  public double[] getTargets(int i)
  { 
    TrainingInstance t = instance.get(i);
    return t==null?null:(double[])(t.target);
  }

  /**
   * Return the index of the "answer" or the index of the only
   * 1-valued element in a training instance <i>i</i>.
   */
  public int getTargetIndex(int i)
  {
    TrainingInstance t = instance.get(i);
    return t==null?-1:t.getAnswer();
  }

  public TrainingInstance getInstance(int i)
  {
    return instance.get(i);
  }

  public void add(TrainingInstance t){ instance.add(t); }
  
  /**
   * Delete the matching array objects contained in t.  This does not
   * do a value-by-value comparison. 
   */
  public void del(TrainingInstance t){ instance.remove(t); }
  
  public int size(){ return instance.size(); }
}

