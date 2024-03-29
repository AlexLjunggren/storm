package io.ljunggren.storm.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;

import io.ljunggren.reflectionUtils.ReflectionUtils;
import io.ljunggren.storm.TestUser;
import io.ljunggren.storm.annotation.Database;
import io.ljunggren.storm.annotation.entity.Id;
import io.ljunggren.storm.annotation.entity.Table;
import io.ljunggren.storm.annotation.entity.Transient;
import io.ljunggren.storm.context.Context;
import io.ljunggren.storm.entity.Generated;
import lombok.Data;

public class AnnotationUtilsTest {
    
    @Data
    private class TestId {
        
        @Id(generated = Generated.NONE)
        private int id;
        
    }
    
    @Test
    public void instantiationTest() {
        AnnotationUtils annotationUtils = new AnnotationUtils();
        assertTrue(annotationUtils instanceof AnnotationUtils);
    }

    @Test
    public void findFieldByAnnotationTest() {
        List<Field> fields = ReflectionUtils.getObjectFields(TestUser.class);
        Field field = AnnotationUtils.findFieldByAnnotation(Id.class, fields);
        assertTrue(field != null);
    }
    
    @Test
    public void findFieldByAnnotationNoFieldTest() {
        List<Field> fields = ReflectionUtils.getObjectFields(Context.class);
        Field field = AnnotationUtils.findFieldByAnnotation(Id.class, fields);
        assertTrue(field == null);
    }
    
    @Test
    public void getAnnotationFromFieldTest() {
        List<Field> fields = ReflectionUtils.getObjectFields(TestUser.class);
        Field field = AnnotationUtils.findFieldByAnnotation(Id.class, fields);
        Annotation annotation = AnnotationUtils.getAnnotationFromField(Id.class, field);
        assertTrue(annotation instanceof Id);
    }
    
    @Test
    public void getAnnotationFromFieldNoAnnotationTest() {
        List<Field> fields = ReflectionUtils.getObjectFields(TestUser.class);
        Field field = AnnotationUtils.findFieldByAnnotation(Id.class, fields);
        Annotation annotation = AnnotationUtils.getAnnotationFromField(Database.class, field);
        assertTrue(annotation == null);
    }
    
    @Test
    public void getAnnotationFromClassTest() {
        Annotation annotation = AnnotationUtils.getAnnotationFromClass(Table.class, TestUser.class);
        assertTrue(annotation instanceof Table);
    }
    
    @Test
    public void getAnnotationFromClassNoAnnotationTest() {
        Annotation annotation = AnnotationUtils.getAnnotationFromClass(Transient.class, TestUser.class);
        assertTrue(annotation == null);
    }
    
    @Test
    public void isAutoGeneratedIdTest() {
        List<Field> fields = ReflectionUtils.getObjectFields(TestUser.class);
        Field field = AnnotationUtils.findFieldByAnnotation(Id.class, fields);
        boolean isAutogenerated = AnnotationUtils.isAutoGeneratedId(field);
        assertTrue(isAutogenerated);
    }
    
    @Test
    public void isAutoGeneratedIdNotGeneratedTest() {
        List<Field> fields = ReflectionUtils.getObjectFields(TestId.class);
        Field field = AnnotationUtils.findFieldByAnnotation(Id.class, fields);
        boolean isAutogenerated = AnnotationUtils.isAutoGeneratedId(field);
        assertFalse(isAutogenerated);
    }
    
    @Test
    public void isAutoGeneratedIdNullTest() {
        boolean isAutogenerated = AnnotationUtils.isAutoGeneratedId(null);
        assertFalse(isAutogenerated);
    }
    
}
