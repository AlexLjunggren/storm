package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.Delete;
import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;

public class DeleteQueryTest {

    @Database(context = "H2")
    private interface UserRepository {
        
        @Delete(sql = "delete from users where id = ?")
        public int deleteById(int id);
        
        @Delete
        public int delete(TestUser user);
        
        @Select(sql = "select count(*) from users")
        public long count();
        
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
    public void deleteByIdTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        long beforeCount = repository.count();
        int deletedCount = repository.deleteById(1);
        long afterCount = repository.count();
        assertEquals(1, deletedCount);
        assertTrue(beforeCount > afterCount);
    }
    
    @Test
    public void deleteTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        long beforeCount = repository.count();
        TestUser user = repository.findById(1);
        int deletes = repository.delete(user);
        long afterCount = repository.count();
        assertEquals(1, deletes);
        assertTrue(beforeCount > afterCount);
    }

}