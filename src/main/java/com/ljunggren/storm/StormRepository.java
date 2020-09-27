package com.ljunggren.storm;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;

import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;
import com.ljunggren.storm.crud.DeleteQuery;
import com.ljunggren.storm.crud.QueryChain;
import com.ljunggren.storm.crud.SelectQuery;
import com.ljunggren.storm.crud.UpdateQuery;

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
        Database connection = (Database) Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType() == Database.class)
                .findFirst()
                .orElse(null);
        return createContext(connection);
    }
    
    private Context createContext(Database connection) {
        if (connection == null) {
            return null;
        }
        String name = connection.context();
        return new ContextFactory().getContext(name);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Iterator<Annotation> annotations = Arrays.stream(method.getAnnotations()).iterator();
        Type returnType = method.getGenericReturnType();
        return execute(annotations, args, returnType);
    }
    
    private Object execute(Iterator<Annotation> annotations, Object[] args, Type returnType) {
        if (annotations.hasNext()) {
            Object object = getQueryChain().execute(annotations.next(), context, args, returnType);
            return object == null ? execute(annotations, args, returnType) : object;
        }
        return null;
    }
    
    private QueryChain getQueryChain() {
        return new SelectQuery().nextChain(
                new DeleteQuery().nextChain(
                new UpdateQuery()
                        ));
    }
    
}
