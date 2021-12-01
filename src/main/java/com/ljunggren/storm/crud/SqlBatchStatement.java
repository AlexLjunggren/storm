package com.ljunggren.storm.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ljunggren.storm.util.ParameterUtils;

public class SqlBatchStatement {

    private Connection connection;

    private String preparedSql;
    private PreparedStatement preparedStatement;
    private List<String> preparedArguments = new ArrayList<>();

    public SqlBatchStatement(Connection connection, String sql) {
        this.connection = connection;
        this.preparedSql = prepareSql(sql);
    }

    private String prepareSql(String sql) {
        return ParameterUtils.replaceParamaterIdsWithQuestionMarks(sql);
    }

    public void addBatch(Object[] arguments) throws SQLException {
        if (preparedStatement == null) {
            preparedStatement = connection.prepareStatement(preparedSql);
        }
        setParameters(preparedStatement, arguments);
    }

    private void setParameters(PreparedStatement preparedStatement, Object[] arguments) throws SQLException {
        preparedArguments = new ArrayList<>();
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                preparedStatement.setObject(i + 1, arguments[i]);
                preparedArguments.add(arguments[i].toString());
            }
            preparedStatement.addBatch();
        }
    }

    public PreparedStatement generatePreparedStatement() throws SQLException {
        if (preparedStatement == null) {
            preparedStatement = connection.prepareStatement(preparedSql);
        }
        return preparedStatement;
    }
    
    @Override
    public String toString() {
        return preparedArguments.isEmpty() ? preparedSql :
            String.format("%s : [%s]", preparedSql, String.join(", ", preparedArguments));
    }

}
