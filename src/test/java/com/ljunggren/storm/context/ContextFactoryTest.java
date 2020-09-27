package com.ljunggren.storm.context;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContextFactoryTest {

    @Test
    public void getTest() {
        Context context = new ContextFactory().getContext("H2");
        assertEquals("H2", context.getName());
        assertEquals("org.h2.Driver", context.getDriver());
    }

    @Test
    public void getNoContextTest() {
        Context context = new ContextFactory().getContext("DNE");
        assertEquals(null, context.getName());
        assertEquals(null, context.getDriver());
    }

}
