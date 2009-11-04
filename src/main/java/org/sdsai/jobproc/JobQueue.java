/* $Id: JobQueue.java 636 2008-04-22 22:04:25Z sbaskin $ */

package org.sdsai.jobproc;

import org.sdsai.CircularLinkedList;
import org.sdsai.Key;
import org.sdsai.MinHeap;
import org.sdsai.Queue;

/**
 * This class must be thread-safe.
 */
public class JobQueue 
{
  /**
   * The relative priority to the jobs queue with immediately less absolute
   * priority that this queue. This is the maximum number of concecutive 
   * runs.
   */
  protected int priority = 10;

  protected int maxRunning = 10;

  /**
   * Tracks how often this has be run consecutively.
   * The job manager must reset this value to zero when it exceeds the
   * queue priority.
   */
  protected int consecutiveRuns = 0;

  /* runQueue   - a circular queue of things running.
   * sleepQueue - jobs for which we do not have enough slots to run.
   * waitQueue  - jobs which do not want to run until a time stamp is reached. 
   */
  protected CircularLinkedList<ScheduledJob> runQueue   = new CircularLinkedList<ScheduledJob>();
  protected Queue<ScheduledJob>              sleepQueue = new Queue<ScheduledJob>();
  protected MinHeap<ScheduledJob>            waitQueue  = new MinHeap<ScheduledJob>();

  //protected long minSleep = Long.MAX_VALUE;

  public int size()
  {
    return runQueue.size() + sleepQueue.size() + waitQueue.size();
  }

  
  public void add(Job j, int sticky)
  {
    add(j, sticky, 0);
  }
  
  /**
   * Add a job. If wait is > 0 it will be put in the wait queue.
   * When a job enters the wait Queue it will run after the waittime
   * expires and after there are slots avaialbe in the run Queue. 
   */
  public void add(Job j, int sticky, long waitTime)
  {
    ScheduledJob sj = new ScheduledJob(j, sticky);

    synchronized(this){
      
      /* Do we wait? */
      if(waitTime > 0){

	waitQueue.add(new Key<ScheduledJob>(waitTime + System.currentTimeMillis(), 
			      new ScheduledJob(j, sticky)));
	
	/* Do we run? */
      } else if(runQueue.size() < priority){

	waitTime = j.start();
	
	if(waitTime > 0){

	  waitQueue.add(new Key<ScheduledJob>(waitTime + System.currentTimeMillis(),
				new ScheduledJob(j, sticky)));
	} else {
	  runQueue.add(sj); 
	}
	
	/* Can we do nothing else but put this in the sleep Queue. */
      } else {
	  
	j.sleep();
	sleepQueue.enqueue(sj);
	
      }
    }
  }

  /**
   * This is essentially the same as add(Job, int).
   * The start() method is never called in this as add(ScheduledJob, long)
   * is only used to safely move jobs from queue to queue.
   */
  protected void add(ScheduledJob sj, long waitTime)
  {
    synchronized(this){

      if(waitTime > 0){
	
	waitQueue.add(new Key<ScheduledJob>(waitTime + System.currentTimeMillis(), sj));
	
      } else if(runQueue.size() < priority){
	
        runQueue.add(sj); 
        
      } else {

	sleepQueue.enqueue(sj);

      }
    }
  }

  /**
   * Moves to the next job and processes it.
   * returns the job processed if the job is done.  No promotion 
   * occures by this.
   */
  public ScheduledJob process()
  {
    return process(null);
  }

  /**
   * The same as process(), but if the SchedulableJob is can be
   * promoted, it is promoted to this queue.  Jobs are not promoted to
   * queues withut empty slots in the running queue.
   */
  public ScheduledJob process(JobQueue promotionQueue)
  {
    ScheduledJob sj = null;

    /* Get a job to work on. */
    synchronized(this){
      sj = (ScheduledJob) runQueue.next();
    }

    /* If no job found, try to wake a waiting job to process. Otherwise
     * simply just try to wake a job as part of our queue mainanence. */
    synchronized(this){

      if(sj == null) {

        sj = wakeJob();      

      } else if ( hasSlots() ) {

        wakeJob();

      }

    }

    if(sj != null){

      long waitTime = sj.process();

      /* When the job is done... */
      if(sj.getJob().isDone()){

        synchronized(this){
          runQueue.del(sj);

          /* We have space so wake up a job. */
          wakeJob();
        }

	/* When the job is eligable for promotion... */
      } else if(promotionQueue != null && sj.promote() && 
		promotionQueue.hasSlots()){

        sj.resetPriority();

        synchronized(this){
          runQueue.del(sj);

          wakeJob();
        }

        promotionQueue.add(sj, waitTime);
       
	/* If the wait time is non-zero. */
      } else if(waitTime > 0){

	synchronized(this){
	  runQueue.del(sj);

	  waitQueue.add(new Key<ScheduledJob>(waitTime + System.currentTimeMillis(), sj));

	  wakeJob();
	}
      }
    }

    consecutiveRuns++;
    
    return sj;
  }

  /**
   * Wakes one job up from the dormant queue and puts it in the running queue.
   * This does not lock the running queue as the running queue should already
   * be locked by the calling method.
   */
  protected ScheduledJob wakeJob()
  {
    ScheduledJob nj = null;
    
    synchronized(this){
      Key<ScheduledJob> k = waitQueue.peek();

      if(k!=null){

	/* If our time has marched passed the requested sleep time. */
	if(k.lt(new Key<ScheduledJob>(System.currentTimeMillis()))){

	  waitQueue.del();
	  nj = (ScheduledJob) k.getData();

	  runQueue.add(nj);
	}
      }
    }
    
    /* Did we not find an elidgeable job to queue up? */
    if(nj == null){
      synchronized(this){
	if(sleepQueue.size()>0){
	  nj = (ScheduledJob) sleepQueue.dequeue();

	  long waitTime = nj.getJob().start();
	  
	  if(waitTime > 0){

	    waitQueue.add(new Key<ScheduledJob>(waitTime+System.currentTimeMillis(), nj));

	  } else {

	    runQueue.add(nj);

	  }
	}
      }
    }

    return nj;
  }

  public ScheduledJob forceWakeJob()
  {
    ScheduledJob sj = null;

    synchronized(this){
      sj = wakeJob();
    }
    
    return sj;
  }

  public int getRuns()
  {
    return consecutiveRuns;
  }

  public void resetRuns()
  {
    consecutiveRuns = 0;
  }

  /**
   * Has the consecutive runs this queue has enjoyed exceeded the 
   * priority it wants to run at.
   * This is also a good place to have statistical computations.
   */
  public boolean exhaustedRuns()
  {
    return consecutiveRuns >= priority;
  }

  public int getPriority()
  {
    return priority;
  }

  public void setPriority(int i)
  {
    priority = i;
  }
  
  public void setMaxRunning(int i)
  {
    maxRunning = i;
  }

  /**
   * Does this queue have slots in which jobs can run and not be put to sleep.
   * If maxRunning is zero then there are infinite slots.
   */
  public boolean hasSlots()
  {
    return maxRunning == 0 || maxRunning > runQueue.size();
  }
  
  public int getMaxRunning()
  {
    return maxRunning;
  }
  
  /**
   * How long till this queue may do something.
   */
  public long getWait()
  {
    Key<ScheduledJob>  k    = null;
    long time = Long.MAX_VALUE;
    
    
    synchronized(this){
      if(runQueue.size() > 0){
	return 0;
      }
    }

    synchronized(this){
      k = waitQueue.peek();
    }

    if(k!=null){
      /* get the long value and rebuild the long. */
      byte[] ba = k.getByteArray();
      time = 
	((( ((long)ba[0]) << 56 )) & 0xff00000000000000L ) | 
	((( ((long)ba[1]) << 48 )) & 0x00ff000000000000L ) | 
	((( ((long)ba[2]) << 40 )) & 0x0000ff0000000000L ) | 
	((( ((long)ba[3]) << 32 )) & 0x000000ff00000000L ) | 
	((( ((long)ba[4]) << 24 )) & 0x00000000ff000000L ) | 
	((( ((long)ba[5]) << 16 )) & 0x0000000000ff0000L ) | 
	((( ((long)ba[6]) <<  8 )) & 0x000000000000ff00L ) | 
	((         ba[7]         ) & 0x00000000000000ffL ) ;

    }
    return time;
  }
}
