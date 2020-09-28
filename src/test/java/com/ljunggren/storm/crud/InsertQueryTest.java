package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.Insert;
import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;

public class InsertQueryTest {

    @Database(context = "H2")
    private interface UserRepository {
        
        @Insert(sql = "insert into users (firstname, lastname, employee_id) values (?, ?, ?)")
        public int add(String firstName, String lastName, int employeeID);

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
    public void test() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        long beforeCount = repository.count();
        int inserts = repository.add("Jane", "Doe", 104);
        long afterCount = repository.count();
        assertEquals(1, inserts);
        assertTrue(beforeCount < afterCount);
    }

}
