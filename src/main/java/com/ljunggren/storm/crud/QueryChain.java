package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.mapper.ResultSetMapper;

public abstract class QueryChain {
    
    private Consumer<String> peek;

    protected QueryChain(Consumer<String> peek) {
        this.peek = peek;
    }

    protected QueryChain nextChain;
    
    public QueryChain nextChain(QueryChain nextChain) {
        this.nextChain = nextChain;
        return this;
    }
    
    public abstract Object execute(Annotation annotation, Context context, Object[] args, Type returnType) throws Exception;
    
    protected Object executeQuery(String sql, Context context, Object[] args, Type returnType) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = context.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, args);
            ResultSet resultSet = preparedStatement.executeQuery();
            return new ResultSetMapper(resultSet, returnType).map();
        } finally {
            if (peek != null) {
                peek.accept(preparedStatement.toString());
            }
            closeConnection(connection);
        }
    }
    
    protected int executeUpdate(String sql, Context context, Object[] args, Type returnType) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = context.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, args);
            return preparedStatement.executeUpdate();
        } finally {
            if (peek != null) {
                peek.accept(preparedStatement.toString());
            }
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
    
    private void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
    
}
