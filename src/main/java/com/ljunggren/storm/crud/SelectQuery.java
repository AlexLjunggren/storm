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
    
    @Override
    public Object execute(Annotation annotation, Context context, Object[] args, Type returnType) throws SQLException {
        if (annotation.annotationType() == Select.class) {
            String sql = ((Select) annotation).sql();
            return executeQuery(sql, context, args, returnType);
        }
        return nextChain.execute(annotation, context, args, returnType);
    }
    
    private Object executeQuery(String sql, Context context, Object[] args, Type returnType) throws SQLException {
        Connection connection = null;
        try {
            connection = context.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, args);
            ResultSet resultSet = preparedStatement.executeQuery();
            return new ResultSetMapper(resultSet, returnType).map();
        } finally {
            closeConnection(connection);
        }
    }
    
}
