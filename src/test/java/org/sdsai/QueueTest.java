package org.sdsai;

import org.sdsai.Queue;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

public class QueueTest
{
  
  @Test(groups="sdsaiQueue")
  public void testQueueBlocking()
  {
    Queue<Object> q = new Queue<Object>();
    
    q.setLimit(2);
    q.setBlock(true);
    
    long timeout = 3000; // 3 seconds
    long startTime = System.currentTimeMillis();
    q.enqueue("1");
    q.enqueue("2");
    
    assertTrue(System.currentTimeMillis() - startTime < 1000, "Elapsed time is not less than a second.");
    
    startTime = System.currentTimeMillis();
    
    q.enqueue("3", timeout); // block for 3 seconds

    long deltat = System.currentTimeMillis() - startTime;
    
    assertTrue(deltat >= 3000, String.format("Time should be greater than or equal to 3 seconds: delta was %d.", deltat));
    
  }

}
