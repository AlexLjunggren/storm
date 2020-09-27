package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.Delete;
import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;

public class DeleteQueryTest {

    @Database(context = "H2")
    private interface UserRepository {
        
        @Delete(sql = "delete from users where id = ?")
        public int delete(int id);
        
        @Delete(sql = "delete from users")
        public int deleteAll();
        
        @Select(sql = "select count(*) from users")
        public long count();
        
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
        int deletedCount = repository.delete(1);
        long afterCount = repository.count();
        assertEquals(1, deletedCount);
        assertTrue(beforeCount > afterCount);
    }
    
    @Test
    public void deleteAllTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        repository.deleteAll();
        long count = repository.count();
        assertEquals(0, count);
    }

}
