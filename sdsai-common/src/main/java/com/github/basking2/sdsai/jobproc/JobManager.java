/* $Id: JobManager.java 636 2008-04-22 22:04:25Z sbaskin $ */

package com.github.basking2.sdsai.jobproc;


/**
 * This manages JobQueues processing jobs with its thread.
 * Because of how this has been written it is not safe to have more
 * than one thread processing any JobManager at a time.  Part of the problem
 * is that JobQueues cannot guarantee that a job will not be run in two threads
 * at the same instance. 
 */
public class JobManager extends Thread
{

  /**
   * The cumulative size of all the stuff added to this manager.
   */
  protected int size = 0;

  /**
   * Do we promote jobs from the low priority queue to the mid normal
   * priority queue in the run() method. 
   */
  protected boolean promote = false;

  protected boolean run;

  protected JobQueue[] jobQueue;

  /**
   * Creat a JobManager with the default 5 queues in it. This means that
   * priorities can run from 0-4.
   */
  public JobManager()
  {
    jobQueue = new JobQueue[5];

    for(int i = 0; i < jobQueue.length; i++){

      jobQueue[i] = new JobQueue();
      
    }
  }
  
  /**
   * Create a JobManager with <i>i</i> job queues in it. A JobManager may have
   * no less than 2 JobQueue.  
   */
  public JobManager(int i)
  {
    if(i<2)
      i = 2;

    jobQueue = new JobQueue[i];

    for(i-- ; i >= 0; i--)
      jobQueue[i] = new JobQueue();

  }

  public JobQueue getJobQueue(int i)
  {
    if(i>=0 && i<jobQueue.length)
      return jobQueue[i];

    return null;
  }

  /**
   * @param j - The job.
   * @param queue - The queue to put this in. This may be 0 - 
   * jobQueue.length-1.  Note that jobs may be promoted from any lower 
   * queue up to queue 1.  Jobs in queue 0 are considerd special 
   * high-priority queues.
   * @param sticky - how many iterations does this job "stick" in one queue 
   * before promotion.
   */
  public void schedule(Job j, int queue, int sticky)
  {
    if(queue >= 0 && queue < jobQueue.length)
      jobQueue[queue].add(j, sticky);

    synchronized(this){
      notifyAll();
    }
  }

  public int getQueueCount()
  {
    return jobQueue.length;
  }

  public void setPromotion(boolean b)
  {
    promote = b;
  }

  public boolean getPromotion()
  {
    return promote;
  }

  public void shutdown()
  {
    synchronized(this){
      run = false;
      notifyAll();
    }
  }

  public void run()
  {
    run = true;
    
    while(run){

      ScheduledJob sj = process();
      
      /**
       * if we've processed nothing there are two reasons. No jobs or all
       * jobs are in the waitQueue. 
       */
      if(sj == null){

	long waitTime = Long.MAX_VALUE;
	
	for(int i=0; i<jobQueue.length; i++){
	  
	  long waitSwp = jobQueue[i].getWait();

	  if(waitSwp > 0 && (waitTime == 0 || waitSwp < waitTime)){
	    waitTime = waitSwp;
	  }
	}
	
	/* Readjust the time back to now. */
	waitTime -= System.currentTimeMillis();

	synchronized(this){
	  try {
	    if(waitTime > 0){
	      this.wait(waitTime);
	    }
	  } catch(InterruptedException e){
	  }
	}
      }  
    }
  }

  /**
   * Find and process the next job. Null is returned if no job is processed.
   * Otherwise the ScheduledJob containing the executed Job is returned.
   */
  public ScheduledJob process()
  {
    JobQueue jq;
    ScheduledJob sj = null;

    boolean workToDo = false;

    do {

      /* Run over each job queue and find the one that should be processed. */
      for(int i=0; i<jobQueue.length; i++){
        
        jq = jobQueue[i];

        /**
         * If this queue has exhausted its consecutive runs, don't run it, 
         * just reset it. 
         */
        if(jq.size() == 0){
          
          jq.resetRuns();
          
        } else if(jq.exhaustedRuns()){
          
          /**
           * If we get here we must have failed jq.size() == 0 and so at
           * least 1 queue is not empty AND we should NOT return null.
           * Otherwise we will cause a block in the run() method.
           */
          jq.resetRuns();
          workToDo = true;
          
        } else {
          
          /* Ignore emptyQueues in this section. */
          
          if(promote && i > 1)
            sj = jq.process(jobQueue[i-1]);
          else
            sj = jq.process(null);
          
          // slightly different way to not promote.
          //sj = jobQueue[i].process(jobQueue[i]);
          
	  /* Having processed a job, return. We are done. */
          return sj;
          
        }
      }
    } while(workToDo);

    return null;
  }

  /**
   * Compute the minimum priority the JobManager can accomodate.
   */
  public int getMinPriority(){ return jobQueue.length - 1; }
}
