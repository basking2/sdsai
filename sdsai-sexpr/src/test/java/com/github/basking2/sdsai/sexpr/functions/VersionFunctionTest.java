package com.github.basking2.sdsai.sexpr.functions;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class VersionFunctionTest {
    @Test
    public void testGetVersion() {
        final String s = new VersionFunction().apply(null, null);

        assertNotNull(s);
    }
}
