package com.ljunggren.storm.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ljunggren.storm.utils.ReflectionUtils;

public class PrimitiveMapper extends MapperChain {

    @Override
    public Object map(ResultSet resultSet, Type returnType) throws SQLException {
        if (ReflectionUtils.isPrimitive(returnType) || ReflectionUtils.isString(returnType)) {
            Class<?> clazz = getClassFromType(returnType);
            return map(resultSet, clazz);
        }
        return nextChain.map(resultSet, returnType);
    }
    
    private Object map(ResultSet resultSet, Class<?> clazz) throws SQLException {
        return resultSet.next() ? mapToObject(resultSet, clazz, null) : null;
    }
    
}
