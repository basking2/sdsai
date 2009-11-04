/* $Id: BTreeSelectTest.java 767 2008-09-03 13:37:20Z sam $ */

package org.sdsai;

import org.sdsai.BTree;
import org.sdsai.DynamicTable;
import org.sdsai.Key;
import org.sdsai.List;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@Test
public class BTreeSelectTest
{

  @Test(testName="btreeSelect", groups="sdsai")
  public void btreeSelectTest()
  {
    BTree<Integer> bt = new BTree<Integer>();
    List<Integer> results;

    /* Insert 10 Integer objects of the value 0-9. */
    for ( int i = 0; i < 100; i++ ) {
      Integer obj = new Integer(i%10);
      bt.add(new Key<Integer>(obj.intValue(), obj ));
    }

    results = testRange(bt, 2, 2);        assertTrue(results.size() == 10) ;
    results = testRange(bt, -2, 2);       assertTrue(results.size() == 30) ;
    results = testRange(bt, 2, -2);       assertTrue(results.size() == 30) ;
    results = testRange(bt, -2, -6);      assertTrue(results.size() == 0) ;
    results = testRange(bt, 1000, -6);    assertTrue(results.size() == 100) ;
    results = testRange(bt, 1000, 10004); assertTrue(results.size() == 0) ;
    results = testRange(bt, 2, 50);       assertTrue(results.size() == 80) ;

  }

  /**
   * Don't ever enable this. It's a helper function. :)
   * @param bt The tree to analyze.
   * @param top The first element in the range to select.
   * @param bottom The last element in the range to select.
   */
  @Test ( enabled=false )
  public static List<Integer> testRange(BTree<Integer> bt, int top, int bottom)
  {
    List<Integer>   ls   = new DynamicTable<Integer>();
    Key<Integer>    k1   = new Key<Integer>(new Integer(top));
    Key<Integer>    k2   = new Key<Integer>(new Integer(bottom));
    
    System.out.println("Selecting ("+top+", "+bottom+")");

    bt.select(ls, k1, k2);

    
    Integer prev = ls.get(0);
    for ( Integer sm : ls) {
      assertTrue(prev.intValue() <= sm.intValue(), prev+"<="+sm);
      System.out.print(sm+" ");
      prev = sm;
    }
    
    System.out.println("");
    
    return ls;
  }
}

