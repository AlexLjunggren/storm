package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.Insert;
import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;
import com.ljunggren.storm.exceptions.StormException;

public class InsertQueryTest {

    @Database(context = "H2")
    private interface UserRepository {
        
        @Insert(sql = "insert into users (firstname, lastname, employee_id) values (?, ?, ?)")
        public int insert(String firstName, String lastName, int employeeID);

        @Insert
        public int insert(TestUser user);
        
        @Insert
        public int insertAll(TestUser... users);
        
        @Insert(sql = "nonsense")
        public int nonsense();
        
        @Select(sql = "select count(*) from users")
        public long count();
        
    }
    
    private String generatedSQL;

    public void setGeneratedSQL(String generatedSQL) {
        this.generatedSQL = generatedSQL;
    }

    @Before
    public void setup() throws Exception {
        Context context = new ContextFactory().getContext("H2");
        Statement stat = context.getConnection().createStatement();
        String sql = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("com/ljunggren/storm/CREATE_DB.sql"), "UTF-8");
        stat.execute(sql);
    }

    @Test
    public void insertNativeTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        long beforeCount = repository.count();
        int inserts = repository.insert("Jane", "Doe", 104);
        long afterCount = repository.count();
        assertEquals(1, inserts);
        assertTrue(beforeCount < afterCount);
    }
    
    @Test
    public void insertTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser jane = new TestUser();
        jane.setFirstName("Jane");
        long beforeCount = repository.count();
        int inserts = repository.insert(jane);
        long afterCount = repository.count();
        assertEquals(1, inserts);
        assertTrue(beforeCount < afterCount);
    }
    
    @Test
    public void insertAllTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser jane = new TestUser();
        jane.setFirstName("Jane");
        TestUser greg = new TestUser();
        greg.setFirstName("Greg");
        long beforeCount = repository.count();
        int inserts = repository.insertAll(jane, greg);
        long afterCount = repository.count();
        assertEquals(2, inserts);
        assertTrue(beforeCount < afterCount);
    }
    
    @Test(expected = StormException.class)
    public void nonsenseTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        repository.nonsense();
    }
    
    @Test
    public void peekTest() {
        Consumer<String> peek = e -> setGeneratedSQL(e);
        UserRepository repository = StormRepository.newInstance(UserRepository.class, peek);
        repository.insert("Jane", "Doe", 104);
        assertTrue(generatedSQL.contains("insert into users (firstname, lastname, employee_id) values (?, ?, ?)"));
    }

}
