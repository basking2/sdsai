/**
 * Copyright (c) 2020-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.selectiontree;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class GroupingFilterTest {
    @Test
    public void testBasicOperations() {
        assertTrue(new GroupingFilter.LessThan(0).test(-1));
        assertFalse(new GroupingFilter.LessThan(0).test(0));
        assertFalse(new GroupingFilter.LessThan(0).test(1));

        assertTrue(new GroupingFilter.LessThanEqual(0).test(-1));
        assertTrue(new GroupingFilter.LessThanEqual(0).test(0));
        assertFalse(new GroupingFilter.LessThanEqual(0).test(1));

        assertFalse(new GroupingFilter.GreaterThan(0).test(-1));
        assertFalse(new GroupingFilter.GreaterThan(0).test(0));
        assertTrue(new GroupingFilter.GreaterThan(0).test(1));

        assertFalse(new GroupingFilter.GreaterThanEqual(0).test(-1));
        assertTrue(new GroupingFilter.GreaterThanEqual(0).test(0));
        assertTrue(new GroupingFilter.GreaterThanEqual(0).test(1));

        assertTrue(new GroupingFilter.Equal(0).test(0));
        assertFalse(new GroupingFilter.Equal(0).test(1));

        assertTrue(new GroupingFilter.True().test(null));
        assertFalse(new GroupingFilter.False().test(null));
    }

    @Test
    public void testRangeShrinks() {
        GroupingFilter.RangeImpl<Float> r1 = new GroupingFilter.RangeImpl<>(1f, true, 2f, true);
        GroupingFilter.RangeImpl<Float> r2 = new GroupingFilter.RangeImpl<>(1.5f, true, 2.5f, true);
        GroupingFilter.RangeImpl<Float> r3 = r1.simplify(r2);

        assertFalse(r3.test(1.5f));
        assertTrue(r3.test(1.6f));
        assertTrue(r3.test(1.9f));
        assertFalse(r3.test(2.0f));
    }

    @Test
    public void testNormalForm() {
        assertEquals("(lt 0)", new GroupingFilter.LessThan(0).normalForm());

        assertEquals("(lte 0)", new GroupingFilter.LessThanEqual(0).normalForm());

        assertEquals("(gt 0)", new GroupingFilter.GreaterThan(0).normalForm());

        assertEquals("(gte 0)", new GroupingFilter.GreaterThanEqual(0).normalForm());

        assertEquals("(eq 0)", new GroupingFilter.Equal(0).normalForm());

        assertEquals("(false)", new GroupingFilter.False().normalForm());

        assertEquals("(true)", new GroupingFilter.True().normalForm());

        assertEquals("[5.0, _)", new GroupingFilter.RangeImpl(5f, false, null, true).normalForm());
    }

}
