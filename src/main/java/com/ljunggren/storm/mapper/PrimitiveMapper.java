package com.ljunggren.storm.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;

import com.ljunggren.storm.utils.ReflectionUtils;

public class PrimitiveMapper extends MapperChain {

    @Override
    public Object map(ResultSet resultSet, Type returnType) throws Exception {
        if (ReflectionUtils.isPrimitive(returnType) || ReflectionUtils.isString(returnType)) {
            Class<?> clazz = getClassFromType(returnType);
            return map(resultSet, clazz);
        }
        return nextChain.map(resultSet, returnType);
    }
    
    private Object map(ResultSet resultSet, Class<?> clazz) throws Exception {
        return resultSet.next() ? mapToPrimitive(resultSet) : null;
    }
    
}
