package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.ljunggren.storm.annotation.Insert;
import com.ljunggren.storm.context.Context;

public class InsertQuery extends QueryChain {
    
    @Override
    public Object execute(Annotation annotation, Context context, Object[] args, Type returnType) {
        if (annotation.annotationType() == Insert.class) {
            String sql = ((Insert) annotation).sql();
            return executeQuery(sql, context, args, returnType);
        }
        return nextChain.execute(annotation, context, args, returnType);
    }
    
    private Object executeQuery(String sql, Context context, Object[] args, Type returnType) {
        Connection connection = null;
        try {
            connection = context.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, args);
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeConnection(connection);
        }
    }
    
}
