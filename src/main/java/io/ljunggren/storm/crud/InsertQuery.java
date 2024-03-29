package io.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.ljunggren.reflectionUtils.ReflectionUtils;
import io.ljunggren.storm.annotation.crud.Insert;
import io.ljunggren.storm.builders.InsertQueryBuilder;
import io.ljunggren.storm.builders.QueryBuilder;
import io.ljunggren.storm.context.Context;
import io.ljunggren.storm.util.ExceptionUtils;

public class InsertQuery extends QueryChain {
    
    public InsertQuery(Consumer<String> peek) {
        super(peek);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        if (annotation.annotationType() == Insert.class) {
            String sql = ((Insert) annotation).sql();
            return sql.isEmpty() ? executeNonNativeQuery(context, arguments, returnType) : executeUpdate(sql, context, parameters, arguments, returnType);
        }
        return nextChain.execute(annotation, context, parameters, arguments, returnType);
    }
    
    private int executeNonNativeQuery(Context context, Object[] args, Type returnType) {
        return Arrays.stream(args).map(ExceptionUtils.rethrowFunction(arg -> executeNonNativeQuery(context, arg, returnType)))
                .collect(Collectors.summingInt(Integer::intValue));
    }
    
    private int executeNonNativeQuery(Context context, Object arguments, Type returnType) throws Exception {
        if (ReflectionUtils.isArray(arguments.getClass())) {
            return executeNonNativeQuery(context, (Object[]) arguments, returnType);
        }
        QueryBuilder queryBuilder = new InsertQueryBuilder(arguments);
        String sql = queryBuilder.buildSQL();
        Object[] generatedArgs = queryBuilder.getArgs();
        return executeUpdate(sql, context, generatedArgs, returnType);
    }
    
}
