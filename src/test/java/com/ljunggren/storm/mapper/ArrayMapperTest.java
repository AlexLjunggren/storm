package com.ljunggren.storm.mapper;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.junit.Test;

import com.ljunggren.storm.TestUser;
import com.mockrunner.mock.jdbc.MockResultSet;

public class ArrayMapperTest {

    TestUser[] users;
    String[] names;
    
    private MockResultSet resultSet;
    private MapperChain mapper = new ArrayMapper();

    @Test
    public void mapTest() throws Exception {
        resultSet = new MockResultSet("mock");
        resultSet.addColumn("ID", new Integer[] {1, 2});
        resultSet.addColumn("FIRSTNAME", new String[] {"Alex", "Christie"});
        resultSet.addColumn("LASTNAME", new String[] {"Ljunggren", "Ljunggren"});
        resultSet.addColumn("EMPLOYEEID", new Integer[] {100, 101});
 
        Field field = this.getClass().getDeclaredField("users");
        Type returnType = field.getGenericType();
        TestUser[] users = (TestUser[]) mapper.map(resultSet, returnType);
        TestUser alex = users[0];
        TestUser christie = users[1];
        
        assertEquals(1, alex.getId());
        assertEquals("Alex", alex.getFirstName());
        assertEquals("Ljunggren", alex.getLastName());
        assertEquals(100, alex.getEmployeeID());
        assertEquals(2, christie.getId());
        assertEquals("Christie", christie.getFirstName());
        assertEquals("Ljunggren", christie.getLastName());
        assertEquals(101, christie.getEmployeeID());
    }
    
    @Test
    public void mapPrimitiveTest() throws Exception {
        resultSet = new MockResultSet("mock");
        resultSet.addColumn("FIRSTNAME", new String[] {"Alex", "Christie"});
        
        Field field = this.getClass().getDeclaredField("names");
        Type returnType = field.getGenericType();
        String[] names = (String[]) mapper.map(resultSet, returnType);
        
        assertEquals("Alex", names[0]);
        assertEquals("Christie", names[1]);
    }
    
}
