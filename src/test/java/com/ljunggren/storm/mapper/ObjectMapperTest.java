package com.ljunggren.storm.mapper;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.TestUser;
import com.mockrunner.mock.jdbc.MockResultSet;

public class ObjectMapperTest {

    TestUser user;
    
    private MockResultSet resultSet;
    private MapperChain mapper = new ObjectMapper();
    
    @Before
    public void setup() throws SQLException {
        resultSet = new MockResultSet("mock");
        resultSet.addColumn("ID", new Integer[] {1});
        resultSet.addColumn("FIRSTNAME", new String[] {"Alex"});
        resultSet.addColumn("LASTNAME", new String[] {"Ljunggren"});
        resultSet.addColumn("EMPLOYEEID", new Integer[] {100});
    }
    
    @Test
    public void mapTest() throws NoSuchFieldException, SecurityException, SQLException {
        Field field = this.getClass().getDeclaredField("user");
        Type returnType = field.getGenericType();
        TestUser user = (TestUser) mapper.map(resultSet, returnType);
        
        assertEquals(1, user.getId());
        assertEquals("Alex", user.getFirstName());
        assertEquals("Ljunggren", user.getLastName());
        assertEquals(100, user.getEmployeeID());
    }
    
}
