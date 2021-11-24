package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.Peek;
import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.Param;
import com.ljunggren.storm.annotation.crud.Select;
import com.ljunggren.storm.annotation.crud.Update;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;
import com.ljunggren.storm.exception.StormException;

public class UpdateQueryTest {

    @Database("H2")
    private interface UserRepository extends Peek<UserRepository> {
        
        @Update(sql = "update users set firstname = #{firstname} where id = #{id}")
        public int updateFirstName(@Param("firstname") String firstname, @Param("id") int id);
        
        @Update
        public int update(TestUser user);
        
        @Update
        public int updateAll(TestUser... users);
        
        @Update(sql = "nonsense")
        public int nonsense();
        
        @Select(sql = "select * from users where id = #{id}")
        public TestUser findById(@Param("id") int id);
        
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
    
    @Test
    public void updateAllTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser user1 = repository.findById(1);
        user1.setEmployeeID(888);
        TestUser user2 = repository.findById(2);
        user2.setEmployeeID(999);
        int updates = repository.updateAll(user1, user2);
        user1 = repository.findById(1);
        user2 = repository.findById(2);
        assertEquals(2, updates);
        assertEquals(888, user1.getEmployeeID());
        assertEquals(999, user2.getEmployeeID());
    }
    
    @Test(expected = StormException.class)
    public void nonsenseTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        repository.nonsense();
    }

    @Test
    public void peekTest() {
        Consumer<String> peek = e -> setGeneratedSQL(e);
        UserRepository repository = StormRepository.newInstance(UserRepository.class).peek(peek);
        repository.updateFirstName("Bob", 1);
        assertTrue(generatedSQL.contains("update users set firstname = ? where id = ?"));
    }

}
