package org.sdsai;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Iterator;

import org.sdsai.DynamicTable;
import org.sdsai.HashTable;
import org.sdsai.HashTableFactory;
import org.sdsai.Key;
import org.sdsai.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/* $Id$ */


public class HashTableTest {

  @Test(groups = {"sdsai"})
  public void generalHashTest() 
  {
    HashTable<Long> h1 = HashTableFactory.buildDEKHash();
    
    List<Key<Long>> keylist = new DynamicTable<Key<Long>>();
    
    Assert.assertFalse(0 == h1.tableSize());

    for(int i=0;i<100;i++){
    
      keylist.add(new Key<Long>(i, new Long(i)));
        
    }

    int COLLISION_NUM=4;
    
    for( Key<Long> k : keylist ) {

      for(int i = 0; i<COLLISION_NUM; i++) {
        h1.add(k);
      }
    }
    
    for(int i = 0; i < COLLISION_NUM; i++) {
      
      for( Key<Long> k : keylist ) {
        Key<Long> k2 = h1.find(k);
        assertNotNull(k2, "Could not find key when it should have been "+k.getData()+" iteration "+i);
        Key<Long> k3 = h1.del(k);
        assertNotNull(k3, "Could not find key "+k.getData() +" iteation "+i);
      }
    }
    
    for( Key<Long> k : keylist ) {
      Key<Long> k2 = h1.del(k);
      assertNull(k2, "Found key when it should have been deleted.");
    }
  }
    
  @Test(groups = {"sdsai"})
  public void iteratorHashTest() 
  {
    HashTable<Long> h1 = HashTableFactory.buildDEKHash();
    
    List<Key<Long>> keylist = new DynamicTable<Key<Long>>();

    for(int i=0;i<100;i++){
    
      keylist.add(new Key<Long>(i, new Long(i)));
        
    }

    int COLLISION_NUM=4;
    
    for( Key<Long> k : keylist ) {

      for(int i = 0; i<COLLISION_NUM; i++) {
        h1.add(k);
      }
    }
    
    int i = 0;
    

    for(@SuppressWarnings("unused")
        Key<Long> kl : h1 )
      i++;
    
    Assert.assertTrue(i==h1.size());
    
    
    for ( Iterator<Key<Long>> it = h1.iterator() ; it.hasNext();  ) {
      Long l = it.next().getData();
      it.remove();
      System.out.println("Got "+l+" Col: "+h1.collisions()+" Sz: "+h1.size());
    }
    
    for( Key<Long> k : keylist ) {
      Key<Long> k2 = h1.del(k);
      assertNull(k2, "Found key when it should have been deleted: "+k.getData());
    }
  }
}

