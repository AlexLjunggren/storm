package com.ljunggren.storm.builders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import com.ljunggren.storm.annotation.entity.ColumnProperty;
import com.ljunggren.storm.annotation.entity.Table;
import com.ljunggren.storm.utils.AnnotationUtils;
import com.ljunggren.storm.utils.ReflectionUtils;

public abstract class QueryBuilder {

    public abstract String buildSQL();
    public abstract Object[] getArgs() throws IllegalAccessException ;
    
    protected String getTableName(Class<?> clazz) {
        Table table = AnnotationUtils.getAnnotationFromClass(Table.class, clazz);
        return table == null ? null : table.name();
    }
    
    protected List<String> getColumnNames(List<Field> fields) {
        return fields.stream()
                .filter(field -> !AnnotationUtils.isAutoGeneratedId(field))
                .filter(field -> !AnnotationUtils.isTransient(field))
                .filter(field -> !field.isSynthetic())
                .filter(field -> ReflectionUtils.isPrimitive(field.getType()) || ReflectionUtils.isString(field.getType()))
                .map(field -> {
                    Annotation columnProperty = AnnotationUtils.getAnnotationFromField(ColumnProperty.class, field);
                    if (columnProperty != null) {
                        return ((ColumnProperty) columnProperty).name();
                    }
                    return field.getName();
                }).collect(Collectors.toList());
    }
    
    protected List<String> generateQuestionMarks(List<String> list) {
        return list.stream().map(item -> "?").collect(Collectors.toList());
    }
    
}