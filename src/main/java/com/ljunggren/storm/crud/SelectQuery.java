package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;

import com.ljunggren.storm.Paging;
import com.ljunggren.storm.annotation.crud.Select;
import com.ljunggren.storm.builders.PagingQueryBuilder;
import com.ljunggren.storm.builders.QueryBuilder;
import com.ljunggren.storm.context.Context;

public class SelectQuery extends QueryChain {
    
    public SelectQuery(Consumer<String> peek) {
        super(peek);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        if (annotation.annotationType() == Select.class) {
            String sql = ((Select) annotation).sql();
            Paging paging = findPaging(arguments);
            return paging == null ? executeQuery(sql, context, parameters, arguments, returnType) : 
                    executePagedQuery(sql, context, parameters, arguments, returnType, paging);
        }
        return nextChain.execute(annotation, context, parameters, arguments, returnType);
    }
    
    private Paging findPaging(Object[] arguments) {
        if (arguments == null) {
            return null;
        }
        return (Paging) Arrays.stream(arguments).filter(argument -> argument.getClass() == Paging.class)
                .findFirst()
                .orElse(null);
    }
    
    private Object executePagedQuery(String sql, Context context, Parameter[] parameters, Object[] arguments, Type returnType, Paging paging) throws Exception {
        parameters = Arrays.stream(parameters).filter(parameter -> parameter.getType() != Paging.class).toArray(Parameter[]::new);
        arguments = Arrays.stream(arguments).filter(argument -> argument.getClass() != Paging.class).toArray(Object[]::new);
        QueryBuilder queryBuilder = new PagingQueryBuilder(sql, arguments, paging);
        String pagingSQL = queryBuilder.buildSQL();
        return executeQuery(pagingSQL, context, parameters, arguments, returnType);
    }
    
}
