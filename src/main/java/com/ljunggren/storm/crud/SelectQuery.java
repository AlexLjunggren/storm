package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.mapper.ResultSetMapper;

public class SelectQuery extends QueryChain {
    
    private Class<?> annotationClass = Select.class;

    @Override
    public Object execute(Annotation annotation, Context context, Object[] args, Type returnType) {
        if (annotation.annotationType() == annotationClass) {
            return executeQuery(annotation, context, args, returnType);
        }
        return nextChain.execute(annotation, context, args, returnType);
    }
    
    private Object executeQuery(Annotation annotation, Context context, Object[] args, Type returnType) {
        Connection connection = null;
        String sql = ((Select) annotation).sql();
        try {
            connection = context.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, args);
            ResultSet resultSet = preparedStatement.executeQuery();
            return new ResultSetMapper(resultSet, returnType).map();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeConnection(connection);
        }
    }
    
    private void setParameters(PreparedStatement preparedStatement, Object[] args) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
        }
    }
    
    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}
