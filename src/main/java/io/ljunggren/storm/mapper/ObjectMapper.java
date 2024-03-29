package io.ljunggren.storm.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.List;

import io.ljunggren.reflectionUtils.ReflectionUtils;

public class ObjectMapper extends MapperChain {

    @Override
    public Object map(ResultSet resultSet, Type returnType) throws Exception {
        if (!ReflectionUtils.isPrimitive(returnType) && 
                !ReflectionUtils.isString(returnType) &&
                !ReflectionUtils.isArray(returnType) &&
                !ReflectionUtils.isParameterized(returnType)) {
            Class<?> clazz = getClassFromType(returnType);
            return map(resultSet, clazz);
        }
        return nextChain.map(resultSet, returnType);
    }
    
    private Object map(ResultSet resultSet, Class<?> clazz) throws Exception {
        Object object = null;
        List<Field> fields = ReflectionUtils.getObjectFields(clazz);
        if (resultSet.next()) {
            object = mapToObject(resultSet, clazz, fields);
        }
        return object;
    }

}
