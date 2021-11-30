package com.ljunggren.storm.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;

public class ExceptionUtilsTest {

    @Test
    public void instantiationTest() {
        ExceptionUtils exceptionUtils = new ExceptionUtils();
        assertTrue(exceptionUtils instanceof ExceptionUtils);
    }
    
    @Test
    public void rethrowFunctionTest() {
        int sum = Arrays.stream(new Integer[] {1, 1})
                .map(ExceptionUtils.rethrowFunction(value -> 1/value))
                .collect(Collectors.summingInt(Integer::intValue));
        assertEquals(2, sum);
    }

    @Test(expected = Exception.class)
    public void rethrowFunctionThrowTest() {
        Arrays.stream(new Integer[] {0, 1})
                .map(ExceptionUtils.rethrowFunction(value -> 1/value))
                .collect(Collectors.summingInt(Integer::intValue));
    }

}
