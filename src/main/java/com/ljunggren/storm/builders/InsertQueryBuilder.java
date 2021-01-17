package com.ljunggren.storm.builders;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.ljunggren.storm.utils.AnnotationUtils;
import com.ljunggren.storm.utils.ExceptionUtils;
import com.ljunggren.storm.utils.ReflectionUtils;

public class InsertQueryBuilder extends QueryBuilder {

    private Object object;
    private List<Field> fields;
    
    public static final String INSERT = "insert into %s (%s) values (%s)";

    public InsertQueryBuilder(Object object) {
        this.object = object;
        this.fields = ReflectionUtils.getObjectFields(object.getClass());
    }

    @Override
    public String buildSQL() {
        String table = getTableName(object.getClass());
        List<String> columns = getColumnNames(fields);
        List<String> questionMarks = generateQuestionMarks(columns);
        return String.format(INSERT, table, StringUtils.join(columns, ", "), StringUtils.join(questionMarks, ", "));
    }

    @Override
    public Object[] getArgs() {
        List<Object> arguments = fields.stream()
                .filter(field -> !AnnotationUtils.isAutoGeneratedId(field))
                .filter(field -> !AnnotationUtils.isTransient(field))
                .filter(field -> !field.isSynthetic())
                .filter(field -> ReflectionUtils.isPrimitive(field.getType()) || ReflectionUtils.isString(field.getType()))
                .map(ExceptionUtils.rethrowFunction(field -> ReflectionUtils.getFieldValue(field, object)))
                .collect(Collectors.toList());
        return arguments.toArray();
    }
    
}