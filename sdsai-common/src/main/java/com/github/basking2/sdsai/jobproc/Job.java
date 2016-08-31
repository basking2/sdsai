/* $Id: Job.java 303 2006-04-04 01:30:15Z sam $ */

package com.github.basking2.sdsai.jobproc;

/**
 * This interface very job must implement.  A Job is submitted to 
 * a JobManager to be processed over time to completion.
 */
public interface Job
{
  /**
   * The JobManager can choose to only process X number of jobs at a time.
   * This is called when a job moves from the "dormant" queue to the 
   * processing queue.  Because jobs can be put into a sleep queue
   * by the user, the start method may be called a second time. 
   */
  public long start();

  /**
   * Called when this job is sleeped before start() or while running.
   */
  public void sleep();

  /**
   * Do *limited* work. Each Job much self-regulate itself
   * so that no one process hogs the JobManager thread.
   * The returned value is the amount of time that this process wants
   * to sleep (at a minimum) before it is run again.  If 0 is returned
   * the the job is assumed to want to run again as soon as possible.
   */
  public long process();
  
  /**
   * Is this job finished. If so, it is forgotten about. 
   */
  public boolean isDone();

}
