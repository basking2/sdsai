package org.sdsai;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class QueueTest
{

    @Test
    public void testQueueBlocking()
    {
        Queue<Object> q = new Queue<Object>();

        q.setLimit(2);
        q.setBlock(true);

        long timeout = 3000; // 3 seconds
        long startTime = System.currentTimeMillis();
        q.enqueue("1");
        q.enqueue("2");

        assertTrue("Elapsed time is not less than a second.", System.currentTimeMillis() - startTime < 1000);

        startTime = System.currentTimeMillis();

        q.enqueue("3", timeout); // block for 3 seconds

        long deltat = System.currentTimeMillis() - startTime;

        assertTrue(String.format("Time should be greater than or equal to 3 seconds: delta was %d.", deltat), deltat >= 3000);

    }

}
