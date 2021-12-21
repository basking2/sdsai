/**
 * Copyright (c) 2013-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.util;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class VersionTest
{
    @Test
    public void test_eq_digits()
    {
        assertTrue(0 == Version.compare("1.1.1", "1.1.1"));
    }
    @Test
    public void test_gt_digits()
    {
        assertTrue(0 < Version.compare("1.1.3", "1.1.1"));
    }
    @Test
    public void test_lt_digits()
    {
        assertTrue(0 > Version.compare("1.1.1", "1.1.3"));
    }
    @Test
    public void test_lt_digits_len()
    {
        assertTrue(0 > Version.compare("1.1.1", "1.1.1.2"));
    }
    @Test
    public void test_gt_digits_len()
    {
        assertTrue(0 < Version.compare("1.1.1.2", "1.1.1"));
    }
    @Test
    public void test_lt_str()
    {
        assertTrue(0 > Version.compare("1.1.1-test1", "1.1.1-test2"));
    }
    @Test
    public void test_gt_str()
    {
        assertTrue(0 < Version.compare("1.1.1-test2", "1.1.1-test1"));
    }
}
