package com.ljunggren.storm.mapper;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ljunggren.storm.TestUser;
import com.mockrunner.mock.jdbc.MockResultSet;

public class SetMapperTest {

    Set<TestUser> users;
    Set<String> names;
    
    private MockResultSet resultSet;
    private MapperChain mapper = new SetMapper();

    @Test
    @SuppressWarnings("unchecked")
    public void mapTest() throws Exception {
        resultSet = new MockResultSet("mock");
        resultSet.addColumn("ID", new Integer[] {1, 2});
        resultSet.addColumn("FIRSTNAME", new String[] {"Alex", "Christie"});
        resultSet.addColumn("LASTNAME", new String[] {"Ljunggren", "Ljunggren"});
        resultSet.addColumn("EMPLOYEEID", new Integer[] {100, 101});
        
        Field field = this.getClass().getDeclaredField("users");
        Type returnType = field.getGenericType();
        Set<TestUser> set = (Set<TestUser>) mapper.map(resultSet, returnType);
        assertEquals(2, set.size());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void mapPrimitiveTest() throws Exception {
        resultSet = new MockResultSet("mock");
        resultSet.addColumn("FIRSTNAME", new String[] {"Alex", "Christie"});
        
        Field field = this.getClass().getDeclaredField("names");
        Type returnType = field.getGenericType();
        Set<String> set = (Set<String>) mapper.map(resultSet, returnType);
        List<String> names = new ArrayList<>(set);
        
        assertEquals("Alex", names.get(0));
        assertEquals("Christie", names.get(1));
    }
    
}
