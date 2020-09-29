package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;

import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.annotation.Update;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;

public class UpdateQueryTest {

    @Database(context = "H2")
    private interface UserRepository {
        
        @Update(sql = "update users set firstname = ? where id = ?")
        public int updateFirstName(String name, int id);
        
        @Update
        public int update(TestUser user);
        
        @Select(sql = "select * from users where id = ?")
        public TestUser findById(int id);
        
    }
        
    @Before
    public void setup() throws Exception {
        Context context = new ContextFactory().getContext("H2");
        Statement stat = context.getConnection().createStatement();
        String sql = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("com/ljunggren/storm/CREATE_DB.sql"), "UTF-8");
        stat.execute(sql);
    }

    @Test
    public void updateFirstNameTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        int updates = repository.updateFirstName("Bob", 1);
        TestUser user = repository.findById(1);
        assertEquals(1, updates);
        assertEquals("Bob", user.getFirstName());
    }
    
    @Test
    public void updateTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser user = repository.findById(1);
        assertEquals("Alex", user.getFirstName());
        user.setFirstName("Alexander");
        int updates = repository.update(user);
        user = repository.findById(1);
        assertEquals("Alexander", user.getFirstName());
        assertEquals(1, updates);
    }

}
