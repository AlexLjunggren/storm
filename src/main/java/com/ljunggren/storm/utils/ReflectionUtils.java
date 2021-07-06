package com.ljunggren.storm.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
        return FieldUtils.getAllFieldsList(clazz);
    }
    
    public static boolean isPrimitive(Type type) {
        if (isParameterized(type)) {
            return false;
        }
        return ClassUtils.isPrimitiveOrWrapper((Class<?>) type);
    }
    
    public static boolean isInteger(Type type) {
        if (isPrimitive(type)) {
            return type.getTypeName().equals(Integer.class.getName()) ||
                    type.getTypeName().equals(int.class.getName());
        }
        return false;
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
    
    public static boolean isSet(Type type) {
        if (isParameterized(type)) {
            return getOwnerType(type).getTypeName().equals(Set.class.getName());
        }
        return false;
    }
    
    public static boolean hasNoArgsContrustor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
                .allMatch(constructor -> constructor.getParameterCount() == 0);
    }
    
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    public static Object getFieldValue(Field field, Object target) throws IllegalAccessException {
        return FieldUtils.readField(field, target, true);
    }
    
}
