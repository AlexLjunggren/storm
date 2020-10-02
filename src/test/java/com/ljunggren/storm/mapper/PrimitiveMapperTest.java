package com.ljunggren.storm.mapper;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.mockrunner.mock.jdbc.MockResultSet;

public class PrimitiveMapperTest {

    int count;
    
    private MockResultSet resultSet;
    private MapperChain mapper = new PrimitiveMapper();
    
    @Before
    public void setup() throws SQLException {
        resultSet = new MockResultSet("mock");
        resultSet.addColumn("count", new Integer[] {4});
    }
    
    @Test
    public void mapTest() throws Exception {
        Field field = this.getClass().getDeclaredField("count");
        Type returnType = field.getGenericType();
        int count = (int) mapper.map(resultSet, returnType);
        assertEquals(4, count);
    }
    
}
