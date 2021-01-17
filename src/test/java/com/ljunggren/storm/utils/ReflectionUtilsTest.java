package com.ljunggren.storm.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.utils.ReflectionUtils;

public class ReflectionUtilsTest {
    
    List<String> stringList;
    Set<Integer> integerSet;
    Collection<Boolean> booleanCollection;
    Map<Integer, String> integerStringMap;
    String[] stringArray;
    boolean primitiveBoolean;
    Boolean objectBoolean;
    String string;
    Set<Integer> intergerSet;
    int primitiveInteger;
    Integer objectInteger;
    TestUser testUser;
    
    @Test
    public void constructorTest() {
        ReflectionUtils reflectionUtils = new ReflectionUtils();
        assertTrue(reflectionUtils instanceof ReflectionUtils);
    }
    
    @Test
    public void getObjectFieldsTest() {
        List<Field> fields = ReflectionUtils.getObjectFields(this.getClass());
        boolean hasStringList = fields.stream().anyMatch(field -> field.getName().equals("stringList"));
        assertTrue(hasStringList);
    }

    @Test
    public void getOwnerTypeNullTest() {
        Type type = ReflectionUtils.getOwnerType(null);
        assertEquals(null, type);
    }

    @Test
    public void getOwnerTypeStringTest() {
        Type type = ReflectionUtils.getOwnerType(String.class);
        assertEquals(null, type);
    }

    @Test
    public void getOwnerTypeListTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = field.getGenericType();
        Type type = ReflectionUtils.getOwnerType(fieldType);
        assertEquals(List.class, type);
    }

    @Test
    public void getOwnerTypeSetTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("integerSet");
        Type fieldType = field.getGenericType();
        Type type = ReflectionUtils.getOwnerType(fieldType);
        assertEquals(Set.class, type);
    }

    @Test
    public void getOwnerTypeBooleanTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("booleanCollection");
        Type fieldType = field.getGenericType();
        Type type = ReflectionUtils.getOwnerType(fieldType);
        assertEquals(Collection.class, type);
    }
    
    @Test
    public void getParameterizedTypesNullTest() {
        Type[] types = ReflectionUtils.getParameterizedTypes(null);
        assertEquals(0, types.length);
    }
    
    @Test
    public void getParameterizedTypesMapTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("integerStringMap");
        Type fieldType = field.getGenericType();
        Type[] types = ReflectionUtils.getParameterizedTypes(fieldType);
        assertEquals(2, types.length);
        assertEquals(Integer.class, types[0]);
        assertEquals(String.class, types[1]);
    }
    
    @Test
    public void getParameterizedTypeNullTest() {
        Type type = ReflectionUtils.getParameterizedType(null);
        assertNull(type);
    }
    
    @Test
    public void getParameterizedTypeNonParameterizedTest() {
        Type type = ReflectionUtils.getOwnerType(String.class);
        assertEquals(null, type);
    }

    @Test
    public void getParameterizedTypeStringTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = field.getGenericType();
        Type type = ReflectionUtils.getParameterizedType(fieldType);
        assertEquals(String.class, type);
    }
    
    @Test
    public void getArrayComponentTypeTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringArray");
        Type fieldType = field.getGenericType();
        Type type = ReflectionUtils.getArrayComponentType(fieldType);
        assertEquals(String.class, type);
    }
    
    @Test
    public void getArrayComponentTypeFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("string");
        Type fieldType = field.getGenericType();
        Type type = ReflectionUtils.getArrayComponentType(fieldType);
        assertEquals(null, type);
    }
    
    @Test
    public void isPrimitiveTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("primitiveBoolean");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isPrimitive(fieldType));
    }
    
    @Test
    public void isIntegerPrimitiveTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("primitiveInteger");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isInteger(fieldType));
    }
    
    @Test
    public void isIntegerObjectTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("objectInteger");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isInteger(fieldType));
    }
    
    @Test
    public void isIntegerFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("string");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isInteger(fieldType));
    }
    
    @Test
    public void isPrimitiveFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isPrimitive(fieldType));
    }
    
    @Test
    public void isPrimitiveWrappedTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("objectBoolean");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isPrimitive(fieldType));
    }
    
    @Test
    public void isParameterizedTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isParameterized(fieldType));
    }
    
    @Test
    public void isParameterizedFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("primitiveBoolean");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isParameterized(fieldType));
    }
    
    @Test
    public void isListTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isList(fieldType));
    }
    
    @Test
    public void isListFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("string");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isList(fieldType));
    }
    
    @Test
    public void isListParamaterizedFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("integerSet");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isList(fieldType));
    }
    
    @Test
    public void isArrayTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringArray");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isArray(fieldType));
    }
    
    @Test
    public void isArrayFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isArray(fieldType));
    }
    
    @Test
    public void isStringTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("string");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isString(fieldType));
    }
    
    @Test
    public void isStringFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("objectBoolean");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isString(fieldType));
    }
    
    @Test
    public void isStringParameterizedTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isString(fieldType));
    }
    
    @Test
    public void isSetTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("integerSet");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.isSet(fieldType));
    }
    
    @Test
    public void isSetParameterizedFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isSet(fieldType));
    }
    
    @Test
    public void isSetFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("string");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.isSet(fieldType));
    }
    
    @Test
    public void hasNoArgsConstructorTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("testUser");
        Type fieldType = field.getGenericType();
        assertTrue(ReflectionUtils.hasNoArgsContrustor((Class<?>) fieldType));
    }
    
    @Test
    public void hasNoArgsConstructorFalseTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("string");
        Type fieldType = field.getGenericType();
        assertFalse(ReflectionUtils.hasNoArgsContrustor((Class<?>) fieldType));
    }
    
    @Test
    public void isCollectionTest() throws NoSuchFieldException, SecurityException {
        Field field = ReflectionUtilsTest.class.getDeclaredField("stringList");
        Type fieldType = ((ParameterizedType) field.getGenericType()).getRawType();
        Class<?> clazz = ((Class<?>) fieldType);
        assertTrue(ReflectionUtils.isCollection(clazz));
    }
    
    @Test
    public void getFieldValueTest() throws NoSuchFieldException, SecurityException, IllegalAccessException {
        string = "test";
        Field field = ReflectionUtilsTest.class.getDeclaredField("string");
        String value = (String) ReflectionUtils.getFieldValue(field, this);
        assertEquals("test", value);
    }
    
}
