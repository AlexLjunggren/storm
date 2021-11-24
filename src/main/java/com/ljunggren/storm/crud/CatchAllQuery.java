package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import com.ljunggren.storm.context.Context;

public class CatchAllQuery extends QueryChain {

    public CatchAllQuery() {
        super(null);
    }

    @Override
    public Object execute(Annotation annotation, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        return null;
    }

}
