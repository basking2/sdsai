/**
 * Copyright (c) 2013-2023 Sam Baskinger
 */
package com.github.basking2.sdsai.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringInterpolatorTest {
    @Test
    public void testSimple() {
        final Map<String, String> env = new HashMap<>();
        env.put("name", "Foo");
        assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name?", env)
        );
    }

    @Test
    public void testEscaping() {
        final Map<String, String> env = new HashMap<>();
        env.put("name", "Foo");
        assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo with $name set to Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name with \\$name set to Foo?", env));
        assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo with \\Foo set to Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name with \\\\$name set to Foo?", env));
        assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo with \\$name set to Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name with \\\\\\$name set to Foo?", env));
        assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo with \\\\Foo set to Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name with \\\\\\\\$name set to Foo?", env));
    }

    @Test
    public void testProperties() {
        assertEquals(
                System.getProperty("java.home"),
                StringInterpolator.formatString("$java.home", System.getProperties())
        );
    }

    @Test
    public void testExceptionOnNoParam() {
        assertThrows(NoSuchElementException.class, () ->{
            StringInterpolator.formatString("$missing", System.getProperties());
        });
    }
}
