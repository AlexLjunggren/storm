package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.ljunggren.storm.annotation.Insert;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.utils.QueryBuilder;

public class InsertQuery extends QueryChain {
    
    @Override
    public Object execute(Annotation annotation, Context context, Object[] args, Type returnType) {
        if (annotation.annotationType() == Insert.class) {
            String sql = ((Insert) annotation).sql();
            return sql.isEmpty() ? executeNonNativeQuery(context, args, returnType) : executeQuery(sql, context, args, returnType);
        }
        return nextChain.execute(annotation, context, args, returnType);
    }
    
    private int executeNonNativeQuery(Context context, Object[] args, Type returnType) {
        // add check for multiple arguments
        QueryBuilder queryBuilder = new QueryBuilder(args[0]);
        String sql = queryBuilder.buildInsertSQL();
        Object[] generatedArgs = queryBuilder.getInsertArgs();
        return executeQuery(sql, context, generatedArgs, returnType);
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
