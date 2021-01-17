package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.Peek;
import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.crud.InsertBatch;
import com.ljunggren.storm.annotation.crud.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;

public class InsertBatchQueryTest {

    @Database(context = "H2")
    private interface UserRepository extends Peek<UserRepository> {
        
        @InsertBatch
        public int insert(TestUser... users);
        
        @InsertBatch
        public int insert(List<TestUser> users);
        
        @Select(sql = "select count(*) from users")
        public long count();
        
    }
    
    private List<String> generatedSQLs;

    private void addGeneratedSQL(String sql) {
        generatedSQLs.add(sql);
    }
    
    private TestUser getJane() {
        TestUser jane = new TestUser();
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        jane.setEmployeeID(104);
        return jane;
    }
    
    private TestUser getGreg() {
        TestUser greg = new TestUser();
        greg.setFirstName("Greg");
        greg.setLastName("Smith");
        greg.setEmployeeID(105);
        return greg;
    }

     @Before
    public void setup() throws Exception {
        generatedSQLs = new ArrayList<String>();
        Context context = new ContextFactory().getContext("H2");
        Statement stat = context.getConnection().createStatement();
        String sql = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("com/ljunggren/storm/CREATE_DB.sql"), "UTF-8");
        stat.execute(sql);
    }

    @Test
    public void insertBatchNativeEllipseTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        int inserts = repository.insert(getJane(), getGreg());
        assertEquals(2, inserts);
    }
    
    @Test
    public void insertBatchListTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        List<TestUser> users = Arrays.asList(new TestUser[] { getJane(), getGreg() });
        int inserts = repository.insert(users);
        assertEquals(2, inserts);
    }

    @Test
    public void peekTest() {
        Consumer<String> peek = e -> addGeneratedSQL(e);
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        repository.peek(peek);
        List<TestUser> users = Arrays.asList(new TestUser[] { getJane(), getGreg() });
        repository.insert(users);
        String sql = "insert into users (firstName, lastName, employee_id) values (?, ?, ?)";
        assertTrue(generatedSQLs.get(0).contains(sql));
        assertTrue(generatedSQLs.get(1).contains(sql));
    }

}
