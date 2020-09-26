package com.ljunggren.storm.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectionUtils {

    public static Type getOwnerType(Type type) {
        return type instanceof ParameterizedType ? ((ParameterizedType) type).getRawType() : null;
    }
    
    public static Type[] getParameterizedTypes(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }
        return new Type[] {};
    }
    
    public static Type getParameterizedType(Type type) {
        Type[] types = getParameterizedTypes(type);
        return types.length == 1 ? types[0] : null;
    }
    
    public static Type getArrayComponentType(Type type) {
        return isArray(type) ? ((Class<?>) type).getComponentType() : null;
    }
    
    public static List<Field> getObjectFields(Class<?> clazz) {
        Field[] fields = FieldUtils.getAllFields(clazz);
        return Arrays.asList(fields);
    }
    
    public static boolean isPrimitive(Type type) {
        if (isParameterized(type)) {
            return false;
        }
        return ClassUtils.isPrimitiveOrWrapper((Class<?>) type);
    }
    
    public static boolean isParameterized(Type type) {
        return type instanceof ParameterizedType;
    }
    
    public static boolean isString(Type type) {
        if (isParameterized(type)) {
            return false;
        }
        return type.getTypeName().equals(String.class.getName());
    }
    
    public static boolean isList(Type type) {
        if (isParameterized(type)) {
            return getOwnerType(type).getTypeName().equals(List.class.getName());
        }
        return false;
    }
    
    public static boolean isArray(Type type) {
        if (isParameterized(type)) {
            return false;
        }
        return ((Class<?>) type).isArray();
    }
    
}