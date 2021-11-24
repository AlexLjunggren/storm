package com.ljunggren.storm.utils;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ljunggren.storm.annotation.Param;

public class ParameterUtilsTest {
    
    private class DummyClass {
        @SuppressWarnings("unused")
        public void find(@Param("id") int id, @Param("asc") boolean asc) {}
    }
    
    
    @Test
    public void mapArgumentsToParameterNamesTest() throws NoSuchMethodException, SecurityException {
        Object[] arguments = new Object[] { 1, true};
        Method method = DummyClass.class.getMethod("find", int.class, boolean.class);
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = ParameterUtils.getParameterNames(parameters);
        Map<String, Object> map = ParameterUtils.mapArgumentsToParameterNames(parameters, arguments);
        assertEquals(2, map.size());
        assertEquals(arguments[0], map.get(parameterNames[0]));
        assertEquals(arguments[1], map.get(parameterNames[1]));
    }

    @Test
    public void findParameterIdsTest() {
        String sql = "select * from users where id = #{id} and active = #{active}";
        List<String> parameterIds = ParameterUtils.findParameterIds(sql);
        assertEquals(2, parameterIds.size());
        assertEquals("id", parameterIds.get(0));
        assertEquals("active", parameterIds.get(1));
    }
    
    @Test
    public void getParameterNamesTest() throws NoSuchMethodException, SecurityException {
        Method method = DummyClass.class.getMethod("find", int.class, boolean.class);
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = ParameterUtils.getParameterNames(parameters);
        assertEquals(2, parameterNames.length);
        assertEquals("id", parameterNames[0]);
        assertEquals("asc", parameterNames[1]);
    }
    
    @Test
    public void findParameterIdsWithOutSpacesTest() {
        String sql = "select * from users where id in (#{id1},#{id2})";
        List<String> parameterIds = ParameterUtils.findParameterIds(sql);
        assertEquals(2, parameterIds.size());
        assertEquals("id1", parameterIds.get(0));
        assertEquals("id2", parameterIds.get(1));
    }
    
    @Test
    public void replaceParamaterIdsWithQuestionMarksTest() {
        String sql = "select * from users where id = #{id} and active = #{active}";
        String actual = ParameterUtils.replaceParamaterIdsWithQuestionMarks(sql);
        String expected = "select * from users where id = ? and active = ?";
        assertEquals(expected, actual);
    }

}
