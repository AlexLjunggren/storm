package io.ljunggren.storm;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

import io.ljunggren.reflectionUtils.ReflectionUtils;
import io.ljunggren.storm.annotation.Database;
import io.ljunggren.storm.context.Context;
import io.ljunggren.storm.context.ContextFactory;
import io.ljunggren.storm.crud.CatchAllQuery;
import io.ljunggren.storm.crud.DeleteQuery;
import io.ljunggren.storm.crud.InsertBatchQuery;
import io.ljunggren.storm.crud.InsertQuery;
import io.ljunggren.storm.crud.QueryChain;
import io.ljunggren.storm.crud.SelectQuery;
import io.ljunggren.storm.crud.UpdateQuery;
import io.ljunggren.storm.exception.StormException;
import io.ljunggren.storm.util.AnnotationUtils;

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
    
    private Context createContext(Database database) {
        if (database == null) {
            return null;
        }
        String name = database.value();
        return new ContextFactory().getContext(name);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Peek.class) {
            return peek(proxy, args);
        }
        Iterator<Annotation> annotations = Arrays.stream(method.getAnnotations()).iterator();
        Type returnType = method.getGenericReturnType();
        Object[] argsAsArrays = convertCollectionsToArray(args);
        Parameter[] parameters = method.getParameters();
        try {
            return execute(annotations, parameters, argsAsArrays, returnType);
        } catch (Exception e) {
            throw new StormException(e.getMessage());
        }
    }
    
    @SuppressWarnings("rawtypes")
    private Object[] convertCollectionsToArray(Object[] args) {
        if (args == null) {
            return null;
        }
        return Arrays.stream(args).map(arg -> {
            if (ReflectionUtils.isCollection(arg.getClass())) {
                return ((Collection) arg).toArray();
            }
            return arg;
        }).toArray();
    }
    
    private Object execute(Iterator<Annotation> annotations, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        if (annotations.hasNext()) {
            Object object = getQueryChain().execute(annotations.next(), context, parameters, arguments, returnType);
            return object == null ? execute(annotations, parameters, arguments, returnType) : object;
        }
        return null;
    }
    
    private QueryChain getQueryChain() {
        return new SelectQuery(peek).nextChain(
                new DeleteQuery(peek).nextChain(
                new UpdateQuery(peek).nextChain(
                new InsertQuery(peek).nextChain(
                new InsertBatchQuery(peek).nextChain(
                new CatchAllQuery()
                        )))));
    }
    
    @SuppressWarnings("unchecked")
    private Object peek(Object proxy, Object[] args) {
        this.peek = (Consumer<String>) args[0];
        return proxy;
    }
    
}
