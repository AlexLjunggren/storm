package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.mapper.ResultSetMapper;
import com.ljunggren.storm.utils.ParameterUtils;

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
    
    private ParameterUtils parameterUtils = new ParameterUtils();
    
    public abstract Object execute(Annotation annotation, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception;
    
    protected Object executeQuery(String sql, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = context.getConnection();
            preparedStatement = generatePreparedStatement(connection, sql, parameters, arguments);
            ResultSet resultSet = preparedStatement.executeQuery();
            return new ResultSetMapper(resultSet, returnType).map();
        } finally {
            if (peek != null) {
                peek.accept(preparedStatement.toString());
            }
            closeConnection(connection);
        }
    }
    
    protected int executeUpdate(String sql, Context context, Object[] arguments, Type returnType) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = context.getConnection();
            preparedStatement = generatePreparedStatement(connection, sql, arguments);
            return preparedStatement.executeUpdate();
        } finally {
            if (peek != null) {
                peek.accept(preparedStatement.toString());
            }
            closeConnection(connection);
        }
    }
    
    protected int executeUpdate(String sql, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = context.getConnection();
            preparedStatement = generatePreparedStatement(connection, sql, parameters, arguments);
            return preparedStatement.executeUpdate();
        } finally {
            if (peek != null) {
                peek.accept(preparedStatement.toString());
            }
            closeConnection(connection);
        }
    }
    
    private PreparedStatement generatePreparedStatement(Connection connection, String sql, Object[] arguments) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        if (arguments == null) {
            return preparedStatement;
        }
        for (int i = 0; i < arguments.length; i++) {
            preparedStatement.setObject(i + 1, arguments[i]);
        }
        return preparedStatement;
    }
        
    private PreparedStatement generatePreparedStatement(Connection connection, String sql, Parameter[] parameters, Object[] arguments) throws SQLException {
        List<String> parameterIds = parameterUtils.findParameterIds(sql);
        String preparedSql = parameterUtils.replaceParamaterIdsWithQuestionMarks(sql);
        PreparedStatement preparedStatement = connection.prepareStatement(preparedSql);
        Map<String, Object> parameterArgumentMap = new ParameterUtils().mapArgumentsToParameterNames(parameters, arguments);
        for (int i = 0; i < parameterIds.size(); i++) {
            preparedStatement.setObject(i + 1, parameterArgumentMap.get(parameterIds.get(i)));
        }
        return preparedStatement;
    }
    
    
    protected int executeBatch(String sql, Context context, Object[] argsArray, Type returnType) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = context.getConnection();
            String preparedSql = parameterUtils.replaceParamaterIdsWithQuestionMarks(sql);
            preparedStatement = connection.prepareStatement(preparedSql);
            for (Object args: argsArray) {
                // TODO: Refactor so args does not have to be cast to array
                setParameters(preparedStatement, (Object[]) args);
                preparedStatement.addBatch();
                if (peek != null) {
                    peek.accept(preparedStatement.toString());
                }
            }
            int[] updates = preparedStatement.executeBatch();
            return IntStream.of(updates).sum();
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
    
    private void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
    
}
