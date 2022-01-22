package io.ljunggren.storm.context;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.ljunggren.storm.exception.ContextException;

public class ContextFactoryTest {

    @Test
    public void getContextTest() {
        Context context = new ContextFactory().getContext("H2");
        assertEquals("H2", context.getName());
        assertEquals("org.h2.Driver", context.getDriver());
    }

    @Test(expected = ContextException.class)
    public void getNoContextByNameTest() {
        new ContextFactory().getContext("DNE");
    }
    
    @Test(expected = ContextException.class)
    public void getContextNoContextFile() {
        new ContextFactory("noContext.json").getContext("H2");
    }

}
