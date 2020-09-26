package com.ljunggren.storm;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;

import com.ljunggren.storm.annotation.Connection;
import com.ljunggren.storm.crud.QueryChain;
import com.ljunggren.storm.crud.SelectQuery;

public class StormRepository implements InvocationHandler {
    
    private Context context;
    
    private StormRepository(Class<?> clazz) {
        this.context = getContextFromClass(clazz);
    }

    public static <T> T newInstance(Class<T> clazz) {
        return clazz.cast(Proxy.newProxyInstance(
                clazz.getClassLoader(), 
                new Class[] { clazz },
                new StormRepository(clazz)));
    }
    
    private Context getContextFromClass(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        Connection connection = (Connection) Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType() == Connection.class)
                .findFirst()
                .orElse(null);
        return createContext(connection);
    }
    
    private Context createContext(Connection connection) {
        if (connection == null) {
            return null;
        }
        String name = connection.context();
        return Context.builder()
                .name(name)
                .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .driver("org.h2.Driver")
                .build();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Annotation[] methodAnnotations = method.getAnnotations();
        Type returnType = method.getGenericReturnType();
        QueryChain queryChain = getQueryChain();
        return queryChain.execute(methodAnnotations[0], context, args, returnType);
    }
    
    private QueryChain getQueryChain() {
        return new SelectQuery();
    }
    
}
