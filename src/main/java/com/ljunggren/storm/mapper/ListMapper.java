package com.ljunggren.storm.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ljunggren.reflectionUtils.ReflectionUtils;

public class ListMapper extends MapperChain {

    @Override
    public Object map(ResultSet resultSet, Type returnType) throws Exception {
        if (ReflectionUtils.isList(returnType)) {
            Class<?> clazz = (Class<?>) ReflectionUtils.getParameterizedType(returnType);
            return map(resultSet, clazz);
        }
        return nextChain.map(resultSet, returnType);
    }

    private Object map(ResultSet resultSet, Class<?> clazz) throws Exception {
        List<Object> list = new ArrayList<>();
        List<Field> fields = ReflectionUtils.getObjectFields(clazz);
        while (resultSet.next()) {
            Object object = mapToObject(resultSet, clazz, fields);
            list.add(object);
        }
        return list;
    }
    
}
