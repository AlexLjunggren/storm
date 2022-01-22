package io.ljunggren.storm.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;

import io.ljunggren.reflectionUtils.ReflectionUtils;

public class PrimitiveMapper extends MapperChain {

    @Override
    public Object map(ResultSet resultSet, Type returnType) throws Exception {
        if (ReflectionUtils.isPrimitive(returnType) || ReflectionUtils.isString(returnType) || ReflectionUtils.isNumber(returnType)) {
            Class<?> clazz = getClassFromType(returnType);
            return map(resultSet, clazz);
        }
        return nextChain.map(resultSet, returnType);
    }
    
    private Object map(ResultSet resultSet, Class<?> clazz) throws Exception {
        return resultSet.next() ? mapToPrimitive(resultSet) : null;
    }
    
}
