/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VersionFunctionTest {
    @Test
    public void testGetVersion() {
        final String s = new VersionFunction().apply(null, null);

        assertNotNull(s);
    }
}
