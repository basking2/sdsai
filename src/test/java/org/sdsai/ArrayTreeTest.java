package org.sdsai;

import org.junit.Assert;
import org.junit.Test;

public class ArrayTreeTest
{
  @Test
  public void testIndexLevel() 
  {
    Assert.assertEquals(0, ArrayTree.indexLevel(0, 3));
    Assert.assertEquals(1, ArrayTree.indexLevel(1, 3));
    Assert.assertEquals(1, ArrayTree.indexLevel(3, 3));
    Assert.assertEquals(2, ArrayTree.indexLevel(4, 3));
    Assert.assertEquals(2, ArrayTree.indexLevel(5, 3));
    Assert.assertEquals(2, ArrayTree.indexLevel(12, 3));
    Assert.assertEquals(3, ArrayTree.indexLevel(13, 3));
  }

  @Test
  public void testLevelOffset() 
  {
    Assert.assertEquals(0, ArrayTree.levelOffset(0, 3));
    Assert.assertEquals(1, ArrayTree.levelOffset(1, 3));
    Assert.assertEquals(4, ArrayTree.levelOffset(2, 3));
    Assert.assertEquals(13, ArrayTree.levelOffset(3, 3));
  }

  @Test
  public void testParent() 
  {
    Assert.assertEquals(0, ArrayTree.parent(0, 3));

    Assert.assertEquals(0, ArrayTree.parent(1, 3));
    Assert.assertEquals(0, ArrayTree.parent(2, 3));
    Assert.assertEquals(0, ArrayTree.parent(3, 3));

    Assert.assertEquals(1, ArrayTree.parent(4, 3));
    Assert.assertEquals(2, ArrayTree.parent(7, 3));
    Assert.assertEquals(3, ArrayTree.parent(10, 3));
  }
}

