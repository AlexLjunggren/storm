package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ljunggren.reflectionUtils.ReflectionUtils;
import com.ljunggren.storm.annotation.crud.Update;
import com.ljunggren.storm.builders.QueryBuilder;
import com.ljunggren.storm.builders.UpdateQueryBuilder;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.util.ExceptionUtils;

public class UpdateQuery extends QueryChain {
    
    public UpdateQuery(Consumer<String> peek) {
        super(peek);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        if (annotation.annotationType() == Update.class) {
            String sql = ((Update) annotation).sql();
            return sql.isEmpty() ? executeNonNativeQuery(context, arguments, returnType) : 
                    executeUpdate(sql, context, parameters, arguments, returnType);
        }
        return nextChain.execute(annotation, context, parameters, arguments, returnType);
    }
    
    private int executeNonNativeQuery(Context context, Object[] arguments, Type returnType) {
        return Arrays.stream(arguments).map(ExceptionUtils.rethrowFunction(argument -> executeNonNativeQuery(context, argument, returnType)))
                .collect(Collectors.summingInt(Integer::intValue));
    }
    
    private int executeNonNativeQuery(Context context, Object argument, Type returnType) throws Exception {
        if (ReflectionUtils.isArray(argument.getClass())) {
            return executeNonNativeQuery(context, (Object[]) argument, returnType);
        }
        QueryBuilder queryBuilder = new UpdateQueryBuilder(argument);
        String sql = queryBuilder.buildSQL();
        Object[] generatedArgs = queryBuilder.getArgs();
        return executeUpdate(sql, context, generatedArgs, returnType);
    }
    
}
