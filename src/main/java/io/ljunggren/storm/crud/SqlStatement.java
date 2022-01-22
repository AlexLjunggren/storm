package io.ljunggren.storm.crud;

import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.ljunggren.storm.exception.StormException;
import io.ljunggren.storm.util.ParameterUtils;

public class SqlStatement {

    private Connection connection;
    private String sql;
    private Parameter[] parameters;
    private Object[] arguments;
    
    private String preparedSql;
    private List<String> preparedArguments = new ArrayList<>();
    
    public SqlStatement(Connection connection, String sql, Object[] arguments) {
        this.connection = connection;
        this.sql = sql;
        this.arguments = arguments;
        this.preparedSql = prepareSql(sql);
    }
    
    public SqlStatement(Connection connection, String sql, Parameter[] parameters, Object[] arguments) {
        this.connection = connection;
        this.sql = sql;
        this.parameters = parameters;
        this.arguments = arguments;
        this.preparedSql = prepareSql(sql);
    }
    
    private String prepareSql(String sql) {
        return ParameterUtils.replaceParamaterIdsWithQuestionMarks(sql);
    }
    
    public PreparedStatement generatePreparedStatement() throws SQLException {
        return parameters == null ? generatePreparedStatementWithArguments() :
            generatePreparedStatementWithParameters();
    }
    
    private PreparedStatement generatePreparedStatementWithArguments() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(preparedSql);
        if (arguments == null) {
            return preparedStatement;
        }
        for (int i = 0; i < arguments.length; i++) {
            preparedStatement.setObject(i + 1, arguments[i]);
        }
        return preparedStatement;
    }
    
    private PreparedStatement generatePreparedStatementWithParameters() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(preparedSql);
        Map<String, Object> parameterArgumentMap = ParameterUtils.mapArgumentsToParameterNames(parameters, arguments);
        if (parameterArgumentMap.isEmpty() && parameters.length == 1) {
            preparedStatement.setObject(1, arguments[0]);
            preparedArguments.add(arguments[0].toString());
            return preparedStatement;
        }
        if (parameterArgumentMap.isEmpty() && parameters.length > 1) {
            throw new StormException("Multiple Storm parameters must be annotated with @Param");
        }
        List<String> parameterIds = ParameterUtils.findParameterIds(sql);
        for (int i = 0; i < parameterIds.size(); i++) {
            Object argument = parameterArgumentMap.get(parameterIds.get(i));
            preparedStatement.setObject(i + 1, argument);
            preparedArguments.add(argument.toString());
        }
        return preparedStatement;
    }
    
    @Override
    public String toString() {
        return preparedArguments.isEmpty() ? preparedSql :
            String.format("%s : [%s]", preparedSql, String.join(", ", preparedArguments));
    }
    
}
