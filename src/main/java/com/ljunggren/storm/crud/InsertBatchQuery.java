package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ljunggren.storm.annotation.crud.InsertBatch;
import com.ljunggren.storm.builders.InsertBatchQueryBuilder;
import com.ljunggren.storm.builders.QueryBuilder;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.utils.ExceptionUtils;

public class InsertBatchQuery extends QueryChain {

    public InsertBatchQuery(Consumer<String> peek) {
        super(peek);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        if (annotation.annotationType() == InsertBatch.class) {
            return executeNonNativeQuery(context, arguments, returnType);
        }
        return nextChain.execute(annotation, context, parameters, arguments, returnType);
    }

    private int executeNonNativeQuery(Context context, Object[] args, Type returnType) {
        return Arrays.stream(args).map(ExceptionUtils.rethrowFunction(arg -> executeNonNativeQuery(context, arg, returnType)))
                .collect(Collectors.summingInt(Integer::intValue));
    }
    
    private int executeNonNativeQuery(Context context, Object arg, Type returnType) throws Exception {
        QueryBuilder queryBuilder = new InsertBatchQueryBuilder((Object[]) arg);
        String sql = queryBuilder.buildSQL();
        Object[] generatedArgs = queryBuilder.getArgs();
        return executeBatch(sql, context, generatedArgs, returnType);
    }
    
}
