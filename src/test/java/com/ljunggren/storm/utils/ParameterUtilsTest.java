package com.ljunggren.storm.utils;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ljunggren.reflectionUtils.ReflectionUtils;

public class ParameterUtilsTest {
    
    private class DummyClass {
        @SuppressWarnings("unused")
        public void find(int id, boolean asc) {}
    }
    
    private ParameterUtils parameterUtils = new ParameterUtils();
    
    @Test
    public void mapArgumentsToParameterNamesTest() throws NoSuchMethodException, SecurityException {
        Method method = DummyClass.class.getMethod("find", int.class, boolean.class);
        Parameter[] parameters = ReflectionUtils.getMethodParameters(method);
        Object[] arguments = new Object[] { 1, true};
        Map<String, Object> map = parameterUtils.mapArgumentsToParameterNames(parameters, arguments);
        assertEquals(2, map.size());
        assertEquals(arguments[0], map.get(parameters[0].getName()));
        assertEquals(arguments[1], map.get(parameters[1].getName()));
    }

    @Test
    public void findParameterIdsTest() {
        String sql = "select * from users where id = #{id} and active = #{active}";
        List<String> parameterIds = parameterUtils.findParameterIds(sql);
        assertEquals(2, parameterIds.size());
        assertEquals("id", parameterIds.get(0));
        assertEquals("active", parameterIds.get(1));
    }
    
    @Test
    public void findParameterIdsWithOutSpacesTest() {
        String sql = "select * from users where id in (#{id1},#{id2})";
        List<String> parameterIds = parameterUtils.findParameterIds(sql);
        assertEquals(2, parameterIds.size());
        assertEquals("id1", parameterIds.get(0));
        assertEquals("id2", parameterIds.get(1));
    }
    
    @Test
    public void replaceParamaterIdsWithQuestionMarksTest() {
        String sql = "select * from users where id = #{id} and active = #{active}";
        String actual = parameterUtils.replaceParamaterIdsWithQuestionMarks(sql);
        String expected = "select * from users where id = ? and active = ?";
        assertEquals(expected, actual);
    }

}
