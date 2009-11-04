/* $Id: HeapTest.java 777 2008-09-09 21:36:57Z sam $ */

package org.sdsai;

import org.testng.annotations.Test;

public class HeapTest
{
  @Test(groups = {"sdsai"})
  public void generalHeapTest() 
  {
    MinHeap<Long> h1 = new MinHeap<Long>(4);
    MaxHeap<Long> h2 = new MaxHeap<Long>(3);

    for(int i=0;i<100;i++){
      
      long l = (long)(Math.random() * 100);
      h1.add(new Key<Long>(l, new Long(l)));
      h2.add(new Key<Long>(l, new Long(l)));
      
    }

    while(h1.size()>0){

      Key<Long> k =  h1.del();
      System.out.println("Deleted: "+(Long)k.getData());

    }

    while(h2.size()>0){

      Key<Long> k =  h2.del();
      System.out.println("Deleted: "+(Long)k.getData());

    }

  }
  
}
