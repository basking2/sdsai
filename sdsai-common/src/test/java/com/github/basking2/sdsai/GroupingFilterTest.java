package com.github.basking2.sdsai;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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


}
