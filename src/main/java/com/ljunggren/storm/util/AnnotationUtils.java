package com.ljunggren.storm.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.ljunggren.storm.annotation.entity.Id;
import com.ljunggren.storm.annotation.entity.Transient;
import com.ljunggren.storm.entity.Generated;

public class AnnotationUtils {

    @SuppressWarnings("unchecked")
    public static <T> T findFieldByAnnotation(Class<?> annotationClazz, List<Field> fields) {
        return (T) fields.stream()
                .filter(field -> AnnotationUtils.getAnnotationFromField(Id.class, field) != null)
                .findFirst()
                .orElse(null);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationFromField(Class<T> annotationClazz, Field field) {
        return (T) Arrays.stream(field.getAnnotations())
                .filter(annotation -> annotation.annotationType() == annotationClazz)
                .findFirst()
                .orElse(null);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationFromClass(Class<T> annotationClazz, Class<?> clazz) {
        return (T) Arrays.stream(clazz.getAnnotations())
                .filter(annotation -> annotation.annotationType() == annotationClazz)
                .findFirst()
                .orElse(null);
    }
    
    public static boolean isAutoGeneratedId(Field field) {
        if (field == null) {
            return false;
        }
        Id id = getAnnotationFromField(Id.class, field);
        if (id == null) {
            return false;
        }
        return id.generated() == Generated.AUTO;
    }
    
    public static boolean isTransient(Field field) {
        return getAnnotationFromField(Transient.class, field) != null;
    }
    
}