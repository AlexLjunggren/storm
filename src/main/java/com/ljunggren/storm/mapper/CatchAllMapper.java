package com.ljunggren.storm.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CatchAllMapper extends MapperChain {

    @Override
    public Object map(ResultSet resultSet, Type returnType) throws SQLException {
        return null;
    }

}
