package io.ljunggren.storm.mapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import io.ljunggren.reflectionUtils.ReflectionUtils;

public class ArrayMapper extends MapperChain {

    @Override
    public Object map(ResultSet resultSet, Type returnType) throws Exception {
        if (ReflectionUtils.isArray(returnType)) {
            Class<?> clazz = (Class<?>) ReflectionUtils.getArrayComponentType(returnType);
            return map(resultSet, clazz);
        }
        return nextChain.map(resultSet, returnType);
    }

    @SuppressWarnings("unchecked")
    private <E> Object map(ResultSet resultSet, Class<?> clazz) throws Exception {
        E[] array = (E[]) Array.newInstance(clazz, 0);
        List<Field> fields = ReflectionUtils.getObjectFields(clazz);
        while (resultSet.next()) {
            Object object = mapToObject(resultSet, clazz, fields);
            array = Arrays.copyOf(array, array.length + 1);
            array[resultSet.getRow() - 1] = (E) object;
        }
        return array;
    }
    
}
