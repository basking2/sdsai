/**
 * Copyright (c) 2016-2023 Sam Baskinger
 */

package com.github.basking2.sdsai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayTreeTest {
  @Test
  public void testIndexLevel() {
    assertEquals(0, ArrayTree.indexLevel(0, 3));
    assertEquals(1, ArrayTree.indexLevel(1, 3));
    assertEquals(1, ArrayTree.indexLevel(3, 3));
    assertEquals(2, ArrayTree.indexLevel(4, 3));
    assertEquals(2, ArrayTree.indexLevel(5, 3));
    assertEquals(2, ArrayTree.indexLevel(12, 3));
    assertEquals(3, ArrayTree.indexLevel(13, 3));
  }

  @Test
  public void testLevelOffset() {
    assertEquals(0, ArrayTree.levelOffset(0, 3));
    assertEquals(1, ArrayTree.levelOffset(1, 3));
    assertEquals(4, ArrayTree.levelOffset(2, 3));
    assertEquals(13, ArrayTree.levelOffset(3, 3));
  }

  @Test
  public void testParent() {
    assertEquals(0, ArrayTree.parent(0, 3));

    assertEquals(0, ArrayTree.parent(1, 3));
    assertEquals(0, ArrayTree.parent(2, 3));
    assertEquals(0, ArrayTree.parent(3, 3));

    assertEquals(1, ArrayTree.parent(4, 3));
    assertEquals(2, ArrayTree.parent(7, 3));
    assertEquals(3, ArrayTree.parent(10, 3));
  }
}

