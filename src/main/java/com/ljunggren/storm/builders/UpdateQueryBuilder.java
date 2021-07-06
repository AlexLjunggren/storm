package com.ljunggren.storm.builders;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.ljunggren.reflectionUtils.ReflectionUtils;
import com.ljunggren.storm.annotation.entity.Id;
import com.ljunggren.storm.utils.AnnotationUtils;
import com.ljunggren.storm.utils.ExceptionUtils;

public class UpdateQueryBuilder extends QueryBuilder {

    private Object object;
    private List<Field> fields;
    private Field idField;
    
    public static final String UPDATE = "update %s set %s where %s = ?";

    public UpdateQueryBuilder(Object object) {
        this.object = object;
        this.fields = ReflectionUtils.getObjectFields(object.getClass());
        this.idField = AnnotationUtils.findFieldByAnnotation(Id.class, fields);
    }

    @Override
    public String buildSQL() {
        String table = getTableName(object.getClass());
        List<String> columns = getColumnNames(fields).stream()
                .map(column -> column + " = ?").collect(Collectors.toList());
        String idName = idField.getName();
        return String.format(UPDATE, table, StringUtils.join(columns, ", "), idName);
    }

    @Override
    public Object[] getArgs() throws IllegalAccessException {
        List<Object> arguments = fields.stream()
                .filter(field -> field != idField)
                .filter(field -> !AnnotationUtils.isTransient(field))
                .filter(field -> !field.isSynthetic())
                .filter(field -> ReflectionUtils.isPrimitive(field.getType()) || ReflectionUtils.isString(field.getType()))
                .map(ExceptionUtils.rethrowFunction(field -> ReflectionUtils.getFieldValue(field, object)))
                .collect(Collectors.toList());
        arguments.add(ReflectionUtils.getFieldValue(idField, object));
        return arguments.toArray();
    }

}