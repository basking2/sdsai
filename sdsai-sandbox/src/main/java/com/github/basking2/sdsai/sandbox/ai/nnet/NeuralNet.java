/* $Id: NeuralNet.java 670 2008-05-02 20:42:07Z sbaskin $ */

package com.github.basking2.sdsai.sandbox.ai.nnet;

import com.github.basking2.sdsai.sandbox.graph.GraphCopier;
import com.github.basking2.sdsai.sandbox.graph.Node;

public class NeuralNet
{
  
  protected boolean learn;
  protected IngressEdge[] input;
  protected EgressEdge[] output;
  
  /**
   * Version of input or output.
   */
  protected int version = 0;
  
  /**
   * Speed at which to "learn."
   */
  protected double eta = 10;

  /**
   * A scaler used to alter the range of eta values computed.
   */
  protected double scaler = 1;

  /**
   * This constructor takes a user-constructed neural net.  The only
   * nodes required are ingress and egress edge arrays.  All hidden
   * nodes are manipulated by recursivly operating on the egress edges.
   * The ingress edges are needed to set the input values.
   */
  public NeuralNet(IngressEdge[] i, EgressEdge[] o){
    input = i;
    output = o;
  }

  /**
   * This constructor automates the process of building a neural net.
   * The number of input and outputs is specified.  The layerSize is an
   * array of layer sizes.  An array of size 0 or null may be passed if no
   * hidden units are desired.  The boolean sc, if true, uses a 
   * ScaledHiddenNode instead of a HiddenNode for the hidden nodes.
   * The difference is that a ScaledHiddenNode has one extra edge for input.
   * The extra, scaling, edge can sometimes allow for better versatility in
   * training but may also confuse the neural net in some instances.
   * This is why the option of choices is given.  Note the neural nets with
   * scaled hidden nodes tend to have significantly longer training epoches.
   * @param innumber the number of inputs.
   * @param outnumber the number of classes or outputs.
   * @param layerSizes an array of sizes for each hidden layer from the
   * inputs to the outputs.
   * @param sc if true, ScaledHiddenNodes are used instead of HiddenNodes.
   */
  public NeuralNet(int innumber, int outnumber, int[] layerSizes, boolean sc)
  {
    input  = new IngressEdge[innumber];
    output = new EgressEdge[outnumber];
    
    if(layerSizes==null)
      layerSizes=new int[0];

    HiddenNode[] layer1 = new HiddenNode[innumber]; /* set up input */
    HiddenNode[] layer2; /* another layer used later*/

    /**** CREATE INPUT LAYER ****/
    for(int i=0;i<innumber;i++){
      layer1[i] = new HiddenNode();
      input[i]  = new IngressEdge(layer1[i]);
    }

    /**** CREATE/LINK HIDDEN LAYERS TO INPUT ****/
    for(int i=0;i<layerSizes.length;i++){
      layer2 = new HiddenNode[layerSizes[i]];

      for(int j=0;j<layer2.length;j++){

        layer2[j] = new HiddenNode();

        for(int k=0;k<layer1.length;k++)
          new HiddenEdge(layer1[k],layer2[j]);
      }
      
      layer1=layer2; /* advance a layer and move on*/
    }


    /**** LINK LAST LAYER TO OUTPUTS ****/
    layer2=new HiddenNode[outnumber];
    
    for(int i=0;i<outnumber;i++){
      layer2[i] = new HiddenNode();
      output[i] = new EgressEdge(layer2[i]); /* why layer2 is in outer loop */

      for(int j=0;j<layer1.length;j++)
        new HiddenEdge(layer1[j], layer2[i]);

    }
  }

  public void nextVersion(){ version = (version+1)%65000; }
  
  public void setInputs(double[] in) {
    for(int i=(in.length>input.length?input.length:in.length-1)-1;i>=0;i--)
      input[i].setInput(in[i]);
    
    nextVersion();
  }
  
  public void setTargets(double[] t){
    for(int i=(t.length>output.length?output.length:t.length)-1;i>=0;i--)
      output[i].setTarget(t[i]);
    
    nextVersion();
  }
  
  public void set(TrainingInstance instance){
    double[] x = instance.getTargets();
    for(int i=(x.length>output.length?output.length:x.length)-1;i>=0;i--)
      output[i].setTarget(x[i]);

    x = instance.getData();
    for(int i=(x.length>input.length?input.length:x.length)-1;i>=0;i--)
      input[i].setInput(x[i]);

    nextVersion();
  }
  
  /**
   * Train the neural net with the data input instance and the target values.
   */
  public void train(TrainingInstance in){ 
    /**
     * If the error gradiant is steep, then we want to take small
     * steps so we don't RUSH past the minimum.
     * If the error gradiant is gradual, then we want to take bigger
     * setps to cause significant change in the error.
     */

    eta=scaler/sigma();
    set(in);
    
    for(int i=0; i<output.length; i++)
      output[i].update(version, eta);     
  }
  
  /**
   * Find the training instance that performs the worst.
   */
  public int worst(TrainingSet s){
    int curr=0, i=s.size()-1;
    double currE=0, tmpE;
    do {
      tmpE=errorDistance(s.getInstance(i));
      if(tmpE>currE){
        currE=tmpE;
        curr=i;
      }
      i--;
    } while(i>=0);
    
    return curr;
  }

  /**
   * Return the index of the training instance that is
   * the nth worst.
   */
  public int worst(TrainingSet s, int n){
    int      i     = s.size()-1; 
    int[]    curr  = new int[n+1];
    double[] currE = new double[n+1];

    do {
      /* to sort, we need a tmp integer value for indecies. */
      int tmpi = i; 

      /* Get some error value for i */
      double tmpE = errorDistance(s.getInstance(tmpi));

      /* Insert it in the error array and mark the index */
      for(int j=0; j<currE.length; j++){


        /**
         * If tmpE is bigger, insert it here.
         * Then try to insert the old values somewhere.
         * This is essentially bubble sort.
         */
        if(currE[j]<tmpE){
          double swap  = currE[j];
          int    swapi = curr[j];
          currE[j]     = tmpE;
          curr[j]      = i;
          tmpE         = swap;
          tmpi         = swapi;
        }
      }

      i--;
    } while(i>=0);
    
    return curr[0]; /* This SHOULD be the largest error value index */
  }
  
 /**
  * Find the training instance that does nth worst.  That is,
  * there are <i>nth</i> instance that perform more baddly
  * that the index of the instance returned.
  * <p> Note that getWorst(dataSet,1) is equivalent to getWorst(dataSet).
  */
  public TrainingInstance getWorst(TrainingSet s, int nth)
  {
    return s.getInstance(worst(s,nth));
  }

  /**
   * Return the training instance which we think is least well
   * performed on.
   */
  public TrainingInstance getWorst(TrainingSet s){
    return s.getInstance(worst(s));
  }
  
  /**
   * In an asyncronous environement it may be benificial to
   * be able to cause the train() method to stop and exit.
   * This will set the field learn equal false which will cause
   * train() to gracefully exit on its next iteration.
   */
  public void stopTraining(){ learn = false; }


  /**
   * Traing i times.
   */
  public void train(TrainingSet s, int i){
    for(;i>0;i--){
      try { 
        train(s); 
      } 
      catch(TrainingException e){
        train(getWorst(s));
      }
    }
  }

  /**
   * This is intended to be the generic training method for
   * a NeuralNet.  It trains the NeuralNet until any further training
   * will decrease the accuracy.<p>
   * Training is done by calling train(TrainingSet s) repeatedly.
   */
  public double trainUp(TrainingSet s)
  {
    try {
      train(s);
    }
    catch(TrainingException e){}
    return accuracy(s);
  }

 /**
  * This method will train the neural net once given the TrainingSet s.
  * Starting at the first TrainingInstance this will try to train
  * that instance.  If there is no loss in accuracy, that training is
  * accepted and the method returns the current accuracy.
  * If the first TrainingInstance decreases the accuracy, then the next
  * instance is tried and so on until either the training set is exhausted
  * or an instance is found which does not decrease the accuracy.
  * If no TrainingInstance will not decrease accuracy, then a
  * TrainingException is thrown.
  */
  public double train(TrainingSet s) throws TrainingException {
      int nth=0;
      double accuracy, prevAccuracy;
      
      prevAccuracy=accuracy(s); /* compute our value to beat */

      train(getWorst(s, nth++));

      accuracy=accuracy(s);

      if(prevAccuracy==1) /* we CAN't do better */
        return 1;
      
      /* If we LOSE accuracy, try another instance */
      while(prevAccuracy>accuracy && nth<s.size()){
        undo();
        train(getWorst(s, nth++));
        accuracy=accuracy(s);
      }

      if(nth>s.size()){ /* If we did NOT acctually train...*/
        undo();
        throw new TrainingException();
      }

      return accuracy;
  }

  /**
   * Train n times or until the accuracy is met.
   * If the value of n is exceeded and the training level is not met, then
   * a TrainingException is thrown.
   * Returns the number of iterations acctually taken to reach the accuracy.
   */
  public int train(TrainingSet s, double acc, int n) 
    throws TrainingException 
  {
    int    rounds=0; /* how many rounds of training have we been through */
    double accuracy=accuracy(s);
    //double previousAccuracy;
    learn=true;   /* keep learning once we start. */

    while(learn && accuracy<acc){
      try {
        //previousAccuracy = accuracy;      
        accuracy         = train(s);
      }
      catch (TrainingException e){ 
        /* When train(s) can't train 
         * we try training the worst
         * performing example
         */
        train(getWorst(s));
      }

      if(rounds < n)
        rounds++;
      else
        throw new TrainingException();
    }
    return rounds;
  }

  /**
   * Traing until stopped (using stopTraining) or until the accuracy
   * exceeds <i>acc</i>.
   * @param s a set of training data.
   * @param acc lowerbound for accuracy.
   */
  public void train(TrainingSet s, double acc)
  {
    int    rounds=0; /* how many rounds of training have we been through */
    double accuracy=accuracy(s);
    //double previousAccuracy;
    learn=true;   /* keep learning once we start. */

    while(learn && accuracy<acc){

      try {
        //previousAccuracy = accuracy;      
        accuracy         = train(s);
      }
      catch (TrainingException e){ 
        /* When train(s) can't train 
         * we try training the worst
         * performing example
         */
        train(getWorst(s));
      }

      /******** DEBUG/DATA GATHER **********/
      rounds++;
      if(rounds%1000==0) 
        System.out.println(rounds+","+
                           sigma()+","+
                           accuracy+
                           "");
      /*************************************/

    }

    System.out.println(rounds+","+
                       sigma()+","+
                       accuracy+
                       "");
  }
  
 /**
  * Undo last training results.
  */
  public void undo()
  {
    for(int i=0;i<output.length;i++)
      output[i].undo();
  }
  
  public double[] decide(double[] in) 
  {
    setInputs(in);
    return getOutputs();
  }
  
  public double[] getOutputs()
  {
    double[] result = new double[output.length];
    for(int i=0; i<output.length; i++)
      result[i] = output[i].getOutput(version);
    
    return result;
  }
  
  /**
   * How "far" are we from being right?
   */
  public double errorDistance(TrainingInstance in)
  {
    set(in);
    
    double max=0; /* output range is 0 to 1 */
    //int answer=0;
    for(int i=0;i<output.length;i++)
      if(max<output[i].getOutput(version)) 
        max = output[i].getOutput(version);

    /* What is the differnce between max and the output? */
    return max-output[in.getAnswer()].getOutput(version);
  }
  
  /**
   * Get the summed error gradient of the outputs.
   */
  public double sigma()
  {
    double sig=0;
    for(int i=0;i<output.length; i++)
      sig = sig+Math.abs(output[i].getSigma(version));
    return sig;
  }
  
  /**
   * Test the neural net presupposing that only ONE output is "on" at any
   * given time.
   */
  public boolean test(TrainingSet tset)
  {  
    for(int i=0; i<tset.size() ; i++){
      setInputs(tset.getData(i));
      
      /* Test our answer against the traing set answer */
      if(tset.getTargetIndex(i) != answer()) return false;
    }
    
    /* If we don't fail, we pass! */
    return true;
  }
  
  public double accuracy(TrainingSet tset){
    double right=0, wrong=0;
    for(int i=0;i<tset.size(); i++){
      TrainingInstance ti = tset.getInstance(i);
      setInputs(ti.getData());

      if(ti.getAnswer()==answer())
        right++;
      else 
        wrong++;
    }
    return right/(right+wrong);
  }

  /**
   * Return the index of what the NeuralNet thinks is the current answer.
   */
  public int answer(){
    double[] o=getOutputs(); 
    int tmpi=0;
    
    /* find max element */
    for(int i=1;i<o.length; i++)
      if(o[tmpi]<o[i])
        tmpi=i;
    
    return tmpi;
  }

  /**
   * This will make a copy of the NeurlaNet and <b>ALL</b> nodes and edges.
   * <p>Some times it is desirable to gate a <i>snap shot</i> of a
   * Neural net as further training my worsen performance or
   * perhaps two nets would like to be trained on seperate sets of data.
   * This method is handy for JUST such a purpose!
   */
  public NeuralNet copy(){
    IngressEdge[] inedge = new IngressEdge[input.length];
    EgressEdge[] outedge = new EgressEdge[output.length];
    Node[]       inNodes = new Node[input.length];

    for(int i=0;i<input.length; i++)
      inNodes[i] = input[i].getDestination();

    GraphCopier gc = new GraphCopier(inNodes);
    gc.copy();

    for(int i=0;i<input.length;i++)
      inedge[i] = (IngressEdge)gc.get(input[i]);
    for(int i=0;i<output.length;i++)
      outedge[i] = (EgressEdge)gc.get(output[i]);

    return new NeuralNet(inedge,outedge);
  }
  
}
