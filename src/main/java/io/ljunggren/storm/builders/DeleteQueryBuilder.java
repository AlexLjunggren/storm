package io.ljunggren.storm.builders;

import java.lang.reflect.Field;
import java.util.List;

import io.ljunggren.reflectionUtils.ReflectionUtils;
import io.ljunggren.storm.annotation.entity.Id;
import io.ljunggren.storm.util.AnnotationUtils;

public class DeleteQueryBuilder extends QueryBuilder {

    private Object object;
    private List<Field> fields;
    private Field idField;

    public static final String DELETE = "delete from %s where %s = ?";

    public DeleteQueryBuilder(Object object) {
        this.object = object;
        this.fields = ReflectionUtils.getObjectFields(object.getClass());
        this.idField = AnnotationUtils.findFieldByAnnotation(Id.class, fields);
    }
    
    @Override
    public String buildSQL() {
        String table = getTableName(object.getClass());
        String idName = idField.getName();
        return String.format(DELETE, table, idName);
    }

    @Override
    public Object[] getArgs() throws IllegalAccessException {
        return new Object[] {ReflectionUtils.getFieldValue(idField, object)};
    }

}