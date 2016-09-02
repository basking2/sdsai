/* $Id: Queue.java 770 2008-09-03 21:39:19Z sam $ */

package com.github.basking2.sdsai.sandbox;

public class Queue<E> 
{

  /**
   * This is a Queue Element.
   */
  protected class QE<F>
  {
    public QE<F> next;
    public F     data;

    public QE(F d, QE<F> n){ data = d; next = n; }
  }

  private QE<E>  head = null;
  private QE<E>  tail = null;
  
  /**
   * If a queue is closed it will never block but perpetually returns
   * NULL. Future enhancements to this be to change that to 
   * be some default END-OF-QUEUE object.
   */
  private boolean closed = false;

  protected int size = 0;

  /**
   * We want no other thing to be able to lock/unlock/notify 
   * this queue. Thus, we hide the lock in here.
   */
  private Object lock;
  
  /**
   * if greater than 0 then this Queue will not hold more than <i>limit</i>
   * elements.
   */
  protected int limit = 0;

  /**
   * If there is a limit on the queue length, block the thread that
   * is attempting to add more data.  This also causes dequeue to
   * notifyAll on this object.
   */
  protected boolean block = false;

  /**
   * If limit is greater than 0 do we dropTail (drop the oldest element)
   * or ignore the added element.
   */
  protected boolean dropHead = false;
  
  public Queue()
  {
    lock = new Object();
  }


  public void setDropHead(boolean b)
  {
    dropHead = b;
  }

  public boolean getDropHead()
  {
    return dropHead;
  }

  /**
   * If this is set to true, the the queue will block when the limit
   * is exceeded on an enqueue. This takes precedence over drop head or
   * drop tail methods. 
   */
  public void setBlock(boolean b)
  {
    block = b;
  }

  public boolean getBlock()
  {
    return block;
  }

  public int getLimit()
  {
    return limit;
  }

  public boolean willBlock()
  {
    return (size >= limit && limit <= 0);
  }

  /**
   * Set a limit on the length of this Queue. Zero is no limit.
   */
  public void setLimit(int l)
  {
    limit = l;
  }

  public E enqueue (E o)
  {
    return enqueue(o, 0);
  }

  /**
   * This method will attempt to enqueue an object according to the
   * rules of this queue. If an element is dropped (or in the case
   * of "drop head", and element is not added) it is returned.
   * If the object is added, then null is returned.
   * 
   * @param o object to enqueue.
   * @param timeout number of milliseconds to wait for an object if this queue causes a block.
   * @return If the element o could not be added due to a timeout, then it is returned. 
   * If because of a drop tail policy in the queue, the dropped element is returned. If
   * o is added successfully, null is returned.
   *
   */
  public E enqueue(E o, long timeout)
  {
    E droppedElement = null;
    
    synchronized(lock) {

      // If there is no limit, just add it.
      if(limit <= 0){

        forceEnqueue(o);

        // If we haven't met the limit, add it.
      } else if(size < limit) {

        forceEnqueue(o);

        // We prefer to block over dropTail.
      } else if(block) {

        synchronized(lock){

          if ( timeout > 0 ) {

            long startTime = System.currentTimeMillis();
            long stopTime  = 0;
            long currentTimeout = timeout;

            do {

              try {

                lock.wait(currentTimeout);
                stopTime = System.currentTimeMillis();

              } catch(InterruptedException e) {  

                // upon spurious wake-up, adjust our timeout.
                currentTimeout = timeout - ( System.currentTimeMillis() - startTime );

                // Just in the wild case that we get a negative currentTimeout...
                // ... we setup to exit the loops as if we have exceeded the timeout...
                // ... ... (which we did)... (cause it's negative)...
                if ( currentTimeout > 0 ) {
                  stopTime = System.currentTimeMillis();
                }          
              }

            } while ( willBlock() && stopTime <= 0 ); 

            if ( stopTime - startTime <= timeout )
              forceEnqueue(o);
            else
              droppedElement = o;

            // Else, the timeout is zero.
          } else {

            // The do-while loop accounts for the occasional accidental wakeup. 
            do {

              try 
              { lock.wait(); }

              catch(InterruptedException e) 
              { /* No recovery action. */ }

            } while(willBlock());
          }
        } // end synchronized.

        forceEnqueue(o);

        /* If we have met the limit and our solution is dropTail. */
      } else if(dropHead){

        droppedElement = dequeue();
        forceEnqueue(o);

        /* If we cannot fit in the new element and we can't drop the head
         * we drop the tail. */
      } else {
       
        droppedElement = o;
        
      }

    }
    
    return droppedElement;
  }

  /**
   * Enqueue the object without checking any limits to this Queue's length.
   * This method is called by enqueue when it concludes that we can add
   * an object to this object.
   */
  public void forceEnqueue(E o)
  {
    synchronized(lock) {

      if(head==null){

        head = new QE<E>(o, null);
        tail = head;

      } else {     

        tail.next = new QE<E>(o, null);
        tail      = tail.next;

      }

      size++;
      
      lock.notifyAll();
    }
  }

  public E dequeue()
  {
    return dequeue(0);
  }
  
  /**
   * Block until there is data to return if this
   * is a blocking queue. Returns the default end of queue object 
   * (null by default) if the timeout 
   * expires. A timeout of zero indicates that this should 
   * block until the queue is non-empty.
   * @return
   */
  public E dequeue(long timeout)
  {
    E o = null;
    
    synchronized(lock) 
    {
      
      if ( closed )
        return getEndOfQueueObject();
       
      
      if ( empty() ) 
      {
        if ( block ) 
        {
          if ( timeout > 0 ) 
          {
            // Block with a timeout.
            
            long startTime = System.currentTimeMillis();
            long currentTimeout = timeout;
            
            do {
              
              try {
                lock.wait(currentTimeout);
                currentTimeout = timeout - ( System.currentTimeMillis() - startTime );
              } catch(InterruptedException e) {
                // do nothing.
              }
              
            } while(empty() && currentTimeout > 0 && !closed);
            
            // If still empty, quit.
            if ( empty() || closed )
              return getEndOfQueueObject();
          
          } else {
            
            // Block with no timeout.
            
            while(empty() && !closed) {
              try {
                lock.wait(); 
              } catch(InterruptedException e) {
                // do nothing. 
              }
            }
            
            if ( closed )
              return getEndOfQueueObject();
            
          }
        
        } else {
          // non blocking with no data. Very easy. :)
          return getEndOfQueueObject();
        }
      }
      
      // Not closed and not empty. Just take an element.
      o = head.data;

      if(size==1){
        head = null;
        tail = null;
      } else {
        head = head.next;
      }

      size--;

      lock.notifyAll();

    }


    return o;
  }
  
  /**
   * Safely empty the queue and notify all thread waiting on this.
   * This is useful for shutting down a queue. Set it to non-blocking, then
   * flush it.
   */
  public void flush()
  {
    synchronized(lock)
    {
      if ( size > 0 ) {
        size = 0;
        head = null;
        tail = null;
      }
      
      lock.notifyAll();
    }
  }

  public boolean empty(){ return size == 0; }
  public E       peek(){  return head==null ? null : head.data; }
  public int     size(){  return size; }
  
  public E getEndOfQueueObject()
  {
    return null;
  }
  
  public void setClosed(boolean closed)
  {
    synchronized(lock){
      this.closed = closed;
      lock.notifyAll();
    }
  }
}
