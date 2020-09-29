package com.ljunggren.storm.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ljunggren.storm.utils.ReflectionUtils;

public class SetMapper extends MapperChain {

    @Override
    public Object map(ResultSet resultSet, Type returnType) throws SQLException {
        if (ReflectionUtils.isSet(returnType)) {
            Class<?> clazz = (Class<?>) ReflectionUtils.getParameterizedType(returnType);
            return map(resultSet, clazz);
        }
        return nextChain.map(resultSet, returnType);
    }

    private Object map(ResultSet resultSet, Class<?> clazz) throws SQLException {
        Set<Object> set = new HashSet<>();
        List<Field> fields = ReflectionUtils.getObjectFields(clazz);
        while (resultSet.next()) {
            Object object = mapToObject(resultSet, clazz, fields);
            set.add(object);
        }
        return set;
    }
    
}