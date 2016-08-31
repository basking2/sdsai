/* $Id: ScheduledJob.java 636 2008-04-22 22:04:25Z sbaskin $ */

package com.github.basking2.sdsai.jobproc;


public class ScheduledJob {
  /**
   * When this is 0 we promote the job and reset the priority.
   */
  public int priority;
  
  /**
   * How much does this job stick to its queue. If this job has a stickiness
   * of 2, then it must run twice in this queue before it is promoted.
   */
  public int sticky; 
  public Job job;

  public ScheduledJob(Job j, int prio)
  {
    priority = prio;
    sticky   = prio;
    job      = j;
  }
  
  /**
   * Calls process() on the job and decrements the priority value.
   * When the priority value == 0, this job is elijable for promotion.
   * The time to sleep as returned by Job.process() is returned.
   */
  public long process()
  {
    long l = job.process();

    if(priority>0)
      priority--;

    return l;
  }

  public boolean promote()
  {
    return priority == 0;
  }

  public void setPriority(int p)
  {
    priority = p;
  }

  public void resetPriority()
  {
    priority = sticky;
  }

  public Job getJob()
  { 
    return job;
  }
}

