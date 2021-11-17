package com.ljunggren.storm.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.ljunggren.reflectionUtils.ReflectionUtils;
import com.ljunggren.storm.annotation.entity.ColumnProperty;
import com.ljunggren.storm.utils.AnnotationUtils;

public abstract class MapperChain {

    protected MapperChain nextChain;
    
    public MapperChain nextChain(MapperChain nextChain) {
        this.nextChain = nextChain;
        return this;
    }
    
    public abstract Object map(ResultSet resultSet, Type returnType) throws Exception;
    
    protected Class<?> getClassFromType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ReflectionUtils.getParameterizedType(type);
        }
        return (Class<?>) type;
    }
    
    protected Object instantiateObject(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        if (ReflectionUtils.hasNoArgsContrustor(clazz)) {
            return clazz.newInstance();
        }
        return null;
    }
    
    protected Object mapToObject(ResultSet resultSet, Class<?> clazz, List<Field> fields) throws SQLException, IllegalAccessException, InstantiationException {
        if (ReflectionUtils.isPrimitive(clazz) || ReflectionUtils.isString(clazz) || ReflectionUtils.isNumber(clazz)) {
            return mapToPrimitive(resultSet);
        }
        Object object = instantiateObject(clazz);
        return mapToObject(resultSet, object, fields);
    }
    
    protected Object mapToPrimitive(ResultSet resultSet) throws SQLException {
        return getColumnValue(resultSet, 1);
    }

    private Object mapToObject(ResultSet resultSet, Object object, List<Field> fields) throws SQLException, IllegalAccessException {
        ResultSetMetaData metaData = getMetaData(resultSet);
        for (int column = 1; column < getColumnCount(metaData) + 1; column++) {
            String columnName = getColumnName(metaData, column);
            Field field = findFieldByName(fields, columnName);
            Object value = getColumnValue(resultSet, column);
            if (columnName != null && field != null) {
                writeToObject(field, object, value);
            }
        }
        return object;
    }
    
    protected Object getColumnValue(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getObject(column);
    }
    
    protected ResultSetMetaData getMetaData(ResultSet resultSet) throws SQLException {
        return resultSet.getMetaData();
    }
    
    protected int getColumnCount(ResultSetMetaData metaData) throws SQLException {
        return metaData.getColumnCount();
    }
    
    protected String getColumnName(ResultSetMetaData metaData, int column) throws SQLException {
        return metaData.getColumnName(column);
    }
    
    protected Field findFieldByName(List<Field> fields, String name) {
        return fields.stream()
                .filter(field -> !AnnotationUtils.isTransient(field))
                .filter(field -> isFieldByName(field, name) || isFieldByAnnotation(field, name))
                .findFirst()
                .orElse(null);
    }
    
    private boolean isFieldByName(Field field, String name) {
        return field.getName().toLowerCase().equals(name.toLowerCase());
    }
    
    private boolean isFieldByAnnotation(Field field, String name) {
        ColumnProperty columnPropery = AnnotationUtils.getAnnotationFromField(ColumnProperty.class, field);
        return columnPropery == null ? false : ((ColumnProperty) columnPropery).name().toLowerCase().equals(name.toLowerCase());
    }
    
    protected void writeToObject(Field field, Object object, Object value) throws IllegalAccessException {
        FieldUtils.writeField(field, object, value, true);
    }
    
}
