package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.ljunggren.storm.annotation.Delete;
import com.ljunggren.storm.context.Context;

public class DeleteQuery extends QueryChain {
    
    @Override
    public Object execute(Annotation annotation, Context context, Object[] args, Type returnType) {
        if (annotation.annotationType() == Delete.class) {
            String sql = ((Delete) annotation).sql();
            return executeQuery(sql, context, args, returnType);
        }
        return nextChain.execute(annotation, context, args, returnType);
    }
    
    private int executeQuery(String sql, Context context, Object[] args, Type returnType) {
        Connection connection = null;
        try {
            connection = context.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, args);
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            closeConnection(connection);
        }
    }

}
