package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ljunggren.reflectionUtils.ReflectionUtils;
import com.ljunggren.storm.annotation.crud.Delete;
import com.ljunggren.storm.builders.DeleteQueryBuilder;
import com.ljunggren.storm.builders.QueryBuilder;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.utils.ExceptionUtils;

public class DeleteQuery extends QueryChain {
    
    public DeleteQuery(Consumer<String> peek) {
        super(peek);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        if (annotation.annotationType() == Delete.class) {
            String sql = ((Delete) annotation).sql();
            return sql.isEmpty() ? executeNonNativeQuery(context, arguments, returnType) : executeUpdate(sql, context, parameters, arguments, returnType);
        }
        return nextChain.execute(annotation, context, parameters, arguments, returnType);
    }
    
    private int executeNonNativeQuery(Context context, Object[] arguments, Type returnType) {
        return Arrays.stream(arguments).map(ExceptionUtils.rethrowFunction(arg -> executeNonNativeQuery(context, arg, returnType)))
                .collect(Collectors.summingInt(Integer::intValue));
    }
    
    private int executeNonNativeQuery(Context context, Object arg, Type returnType) throws Exception {
        if (ReflectionUtils.isArray(arg.getClass())) {
            return executeNonNativeQuery(context, (Object[]) arg, returnType);
        }
        QueryBuilder queryBuilder = new DeleteQueryBuilder(arg);
        String sql = queryBuilder.buildSQL();
        Object[] generatedArgs = queryBuilder.getArgs();
        return executeUpdate(sql, context, generatedArgs, returnType);
    }

}
