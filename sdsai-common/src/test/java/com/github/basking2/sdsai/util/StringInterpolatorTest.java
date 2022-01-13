/**
 * Copyright (c) 2013-2022 Sam Baskinger
 */
package com.github.basking2.sdsai.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class StringInterpolatorTest {
    @Test
    public void testSimple() {
        final Map<String, String> env = new HashMap<>();
        env.put("name", "Foo");
        Assert.assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name?", env)
        );
    }

    @Test
    public void testEscaping() {
        final Map<String, String> env = new HashMap<>();
        env.put("name", "Foo");
        Assert.assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo with \\$name set to Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name with \\$name set to Foo?", env));
        Assert.assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo with \\\\$name set to Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name with \\\\$name set to Foo?", env));
        Assert.assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo with \\\\$name set to Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name with \\\\$name set to Foo?", env));
        Assert.assertEquals(
                "Hi, Foo, how are things going? Did you know you are Foo with \\\\\\\\$name set to Foo?",
                StringInterpolator.formatString("Hi, ${name}, how are things going? Did you know you are $name with \\\\\\\\$name set to Foo?", env));
    }

    @Test
    public void testProperties() {
        Assert.assertEquals(
                System.getProperty("java.home"),
                StringInterpolator.formatString("$java.home", System.getProperties())
        );
    }

    @Test(expected = NoSuchElementException.class)
    public void testExceptionOnNoParam() {
        StringInterpolator.formatString("$missing", System.getProperties());

    }
}
