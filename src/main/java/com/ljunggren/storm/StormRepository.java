package com.ljunggren.storm;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;
import com.ljunggren.storm.crud.DeleteQuery;
import com.ljunggren.storm.crud.InsertQuery;
import com.ljunggren.storm.crud.QueryChain;
import com.ljunggren.storm.crud.SelectQuery;
import com.ljunggren.storm.crud.UpdateQuery;
import com.ljunggren.storm.exceptions.StormException;
import com.ljunggren.storm.utils.AnnotationUtils;

public class StormRepository implements InvocationHandler {
    
    private Context context;
    private Consumer<String> peek;
    
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
        Database database = AnnotationUtils.getAnnotationFromClass(Database.class, clazz);
        return createContext(database);
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
        if (method.getDeclaringClass() == Peek.class) {
            return peek(proxy, args);
        }
        Iterator<Annotation> annotations = Arrays.stream(method.getAnnotations()).iterator();
        Type returnType = method.getGenericReturnType();
        try {
            return execute(annotations, args, returnType);
        } catch (Exception e) {
            throw new StormException(e.getMessage());
        }
    }
    
    private Object execute(Iterator<Annotation> annotations, Object[] args, Type returnType) throws Exception {
        if (annotations.hasNext()) {
            Object object = getQueryChain().execute(annotations.next(), context, args, returnType);
            return object == null ? execute(annotations, args, returnType) : object;
        }
        return null;
    }
    
    private QueryChain getQueryChain() {
        return new SelectQuery(peek).nextChain(
                new DeleteQuery(peek).nextChain(
                new UpdateQuery(peek).nextChain(
                new InsertQuery(peek)
                        )));
    }
    
    @SuppressWarnings("unchecked")
    private Object peek(Object proxy, Object[] args) {
        this.peek = (Consumer<String>) args[0];
        return proxy;
    }
    
}
