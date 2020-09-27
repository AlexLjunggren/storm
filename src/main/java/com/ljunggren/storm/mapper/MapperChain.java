package com.ljunggren.storm.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.ljunggren.storm.annotation.ColumnProperty;
import com.ljunggren.storm.utils.ReflectionUtils;

public abstract class MapperChain {

    protected MapperChain nextChain;
    
    public MapperChain nextChain(MapperChain nextChain) {
        this.nextChain = nextChain;
        return this;
    }
    
    public abstract Object map(ResultSet resultSet, Type returnType) throws SQLException;
    
    protected Class<?> getClassFromType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ReflectionUtils.getParameterizedType(type);
        }
        return (Class<?>) type;
    }
    
    protected Object instantiateObject(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected Object mapToObject(ResultSet resultSet, Class<?> clazz, List<Field> fields) {
        if (ReflectionUtils.isPrimitive(clazz) || ReflectionUtils.isString(clazz)) {
            return mapToPrimitive(resultSet);
        }
        Object object = instantiateObject(clazz);
        return mapToObject(resultSet, object, fields);
    }
    
    private Object mapToPrimitive(ResultSet resultSet) {
        return getColumnValue(resultSet, 1);
    }

    private Object mapToObject(ResultSet resultSet, Object object, List<Field> fields) {
        ResultSetMetaData metaData = getMetaData(resultSet);
        IntStream.range(1, getColumnCount(metaData) + 1).forEach(column -> {
            String columnName = getColumnName(metaData, column);
            Field field = findFieldByName(fields, columnName);
            Object value = getColumnValue(resultSet, column);
            if (columnName != null && field != null) {
                writeToObject(field, object, value);
            }
        });
        return object;
    }
    
    protected Object getColumnValue(ResultSet resultSet, int column) {
        try {
            return resultSet.getObject(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected ResultSetMetaData getMetaData(ResultSet resultSet) {
        try {
            return resultSet.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected int getColumnCount(ResultSetMetaData metaData) {
        try {
            return metaData.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    protected String getColumnName(ResultSetMetaData metaData, int column) {
        try {
            return metaData.getColumnName(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected Field findFieldByName(List<Field> fields, String name) {
        return fields.stream().filter(field -> isFieldByName(field, name) || isFieldByAnnotation(field, name))
                .findFirst()
                .orElse(null);
    }
    
    private boolean isFieldByName(Field field, String name) {
        return field.getName().toLowerCase().equals(name.toLowerCase());
    }
    
    private boolean isFieldByAnnotation(Field field, String name) {
        Annotation[] annotations = field.getAnnotations();
        Annotation columnPropery = Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType() == ColumnProperty.class)
                .findFirst()
                .orElse(null);
        return columnPropery == null ? false : ((ColumnProperty) columnPropery).name().toLowerCase().equals(name.toLowerCase());
    }
    
    protected void writeToObject(Field field, Object object, Object value) {
        try {
            FieldUtils.writeField(field, object, value, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
}
