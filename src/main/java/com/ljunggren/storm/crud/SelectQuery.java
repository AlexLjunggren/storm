package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Consumer;

import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.context.Context;

public class SelectQuery extends QueryChain {
    
    public SelectQuery(Consumer<String> peek) {
        super(peek);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Object[] args, Type returnType) throws Exception {
        if (annotation.annotationType() == Select.class) {
            String sql = ((Select) annotation).sql();
            return executeQuery(sql, context, args, returnType);
        }
        return nextChain.execute(annotation, context, args, returnType);
    }
    
}
