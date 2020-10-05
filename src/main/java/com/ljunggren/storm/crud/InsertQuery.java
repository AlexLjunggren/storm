package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ljunggren.storm.annotation.crud.Insert;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.utils.ExceptionUtils;
import com.ljunggren.storm.utils.QueryBuilder;
import com.ljunggren.storm.utils.ReflectionUtils;

public class InsertQuery extends QueryChain {
    
    public InsertQuery(Consumer<String> peek) {
        super(peek);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Object[] args, Type returnType) throws Exception {
        if (annotation.annotationType() == Insert.class) {
            String sql = ((Insert) annotation).sql();
            return sql.isEmpty() ? executeNonNativeQuery(context, args, returnType) : executeUpdate(sql, context, args, returnType);
        }
        return nextChain.execute(annotation, context, args, returnType);
    }
    
    private int executeNonNativeQuery(Context context, Object[] args, Type returnType) {
        return Arrays.stream(args).map(ExceptionUtils.rethrowFunction(arg -> executeNonNativeQuery(context, arg, returnType)))
                .collect(Collectors.summingInt(Integer::intValue));
    }
    
    private int executeNonNativeQuery(Context context, Object arg, Type returnType) throws SQLException {
        if (ReflectionUtils.isArray(arg.getClass())) {
            return executeNonNativeQuery(context, (Object[]) arg, returnType);
        }
        QueryBuilder queryBuilder = new QueryBuilder(arg);
        String sql = queryBuilder.buildInsertSQL();
        Object[] generatedArgs = queryBuilder.getInsertArgs();
        return executeUpdate(sql, context, generatedArgs, returnType);
    }
    
}
