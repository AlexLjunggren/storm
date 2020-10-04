package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;

import com.ljunggren.storm.Paging;
import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.utils.QueryBuilder;

public class SelectQuery extends QueryChain {
    
    public SelectQuery(Consumer<String> peek) {
        super(peek);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Object[] args, Type returnType) throws Exception {
        if (annotation.annotationType() == Select.class) {
            String sql = ((Select) annotation).sql();
            Paging paging = findPaging(args);
            return paging == null ? executeQuery(sql, context, args, returnType) : executePagedQuery(sql, context, args, returnType, paging);
        }
        return nextChain.execute(annotation, context, args, returnType);
    }
    
    private Paging findPaging(Object[] args) {
        if (args == null) {
            return null;
        }
        return (Paging) Arrays.stream(args).filter(arg -> arg.getClass() == Paging.class)
                .findFirst()
                .orElse(null);
    }
    
    private Object executePagedQuery(String sql, Context context, Object[] args, Type returnType, Paging paging) throws Exception {
        QueryBuilder queryBuilder = new QueryBuilder();
        String pagingSQL = queryBuilder.buildPagingSQL(sql, paging);
        Object[] generatedArgs = queryBuilder.getPagingArgs(args);
        return executeQuery(pagingSQL, context, generatedArgs, returnType);
    }
    
}
