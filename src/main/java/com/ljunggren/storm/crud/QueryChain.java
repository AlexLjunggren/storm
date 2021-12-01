package com.ljunggren.storm.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.stream.IntStream;

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
    
    
    public abstract Object execute(Annotation annotation, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception;
    
    protected Object executeQuery(String sql, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws Exception {
        Connection connection = null;
        SqlStatement sqlStatement = null;
        try {
            connection = context.getConnection();
            sqlStatement = new SqlStatement(connection, sql, parameters, arguments);
            ResultSet resultSet = sqlStatement.generatePreparedStatement().executeQuery();
            return new ResultSetMapper(resultSet, returnType).map();
        } finally {
            if (peek != null) {
                peek.accept(sqlStatement.toString());
            }
            closeConnection(connection);
        }
    }
    
    protected int executeUpdate(String sql, Context context, Object[] arguments, Type returnType) throws SQLException {
        return executeUpdate(sql, context, null, arguments, returnType);
    }
    
    protected int executeUpdate(String sql, Context context, Parameter[] parameters, Object[] arguments, Type returnType) throws SQLException {
        Connection connection = null;
        SqlStatement sqlStatement = null;
        try {
            connection = context.getConnection();
            sqlStatement = new SqlStatement(connection, sql, parameters, arguments);
            return sqlStatement.generatePreparedStatement().executeUpdate();
        } finally {
            if (peek != null) {
                peek.accept(sqlStatement.toString());
            }
            closeConnection(connection);
        }
    }
    
    protected int executeBatch(String sql, Context context, Object[] argumentArray, Type returnType) throws SQLException {
        Connection connection = null;
        SqlBatchStatement sqlBatchStatement = null;
//        PreparedStatement preparedStatement = null;
        try {
            connection = context.getConnection();
            sqlBatchStatement = new SqlBatchStatement(connection, sql);
//            String preparedSql = ParameterUtils.replaceParamaterIdsWithQuestionMarks(sql);
//            preparedStatement = connection.prepareStatement(preparedSql);
            for (Object arguments: argumentArray) {
                sqlBatchStatement.addBatch((Object[]) arguments);
                // TODO: Refactor so args does not have to be cast to array
//                setParameters(preparedStatement, (Object[]) arguments);
//                preparedStatement.addBatch();
                if (peek != null) {
                    peek.accept(sqlBatchStatement.toString());
                }
            }
            int[] updates = sqlBatchStatement.generatePreparedStatement().executeBatch();
            return IntStream.of(updates).sum();
        } finally {
            closeConnection(connection);
        }
    }
    
//    private void setParameters(PreparedStatement preparedStatement, Object[] args) throws SQLException {
//        if (args != null) {
//            for (int i = 0; i < args.length; i++) {
//                preparedStatement.setObject(i + 1, args[i]);
//            }
//        }
//    }
//    
    private void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
    
}
