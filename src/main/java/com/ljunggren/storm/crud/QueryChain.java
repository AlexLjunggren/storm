package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.ljunggren.storm.Context;

public abstract class QueryChain {

    protected QueryChain nextChain;
    
    public QueryChain nextChain(QueryChain nextChain) {
        this.nextChain = nextChain;
        return this;
    }
    
    public abstract Object execute(Annotation annotation, Context context, Object[] args, Type returnType);
    
}
