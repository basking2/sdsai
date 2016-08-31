package com.github.basking2.sdsai;
/* $Id: JobManagerTester.java 767 2008-09-03 13:37:20Z sam $ */

import com.github.basking2.sdsai.jobproc.Job;
import com.github.basking2.sdsai.jobproc.JobManager;
import org.junit.Test;


public class JobManagerTester implements Job
{
  int runs = 0;

  boolean started = false;

  static JobManagerTester[] tracking;

  int index;

  public long start()
  {
    if(started){
      System.err.println("*** Started again ***");
    } else {
      started = true;
      System.err.println("Started.");
    }
    
    return 0;
  }
  
  public long process()
  {
    if(runs>100)
      System.out.println("*** OVERWORK ***");

    runs++;
    
    
    return 10;
  }
  
  @Test
  public void main()
  {
    JobManager jm = new JobManager(4);
    
    jm.setPromotion(true);
    //Thread t = new Thread(jm);
    jm.start();

    tracking = new JobManagerTester[10000];
 
    for(int i=0; i<tracking.length; i++){
      tracking[i]       = new JobManagerTester();
      tracking[i].index = i;
      jm.schedule(tracking[i], 3, 3);
    }

    //jm.run();
    
  }
  
  public void sleep()
  {
    System.err.println("Sleeping.");
  }
  
  public boolean isDone()
  {
    if(runs>100){
      System.err.println("Reported as done.");
      tracking[index] = null;

      int count = 0;
      
      for(int i=0; i<tracking.length; i++){
	if(tracking[i] != null){
	  //System.out.print(i + " ");
	  count++;
	}
      }
      
      System.out.println("\nCOUNT:"+count+"/"+tracking.length);

      return true;
    }
    
    return false;
  }
}
