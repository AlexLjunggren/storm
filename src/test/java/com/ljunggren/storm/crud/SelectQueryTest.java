package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.Context;
import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.annotation.Connection;
import com.ljunggren.storm.annotation.Select;

public class SelectQueryTest {

    @Connection(context = "H2")
    private interface UserRepository {
        
        @Select(sql = "select * from users order by id")
        public TestUser[] fetchAll();
        
        @Select(sql = "select * from users where id = ?")
        public TestUser findById(int id);
        
        @Select(sql = "select * from users where firstname = ? and lastname = ?")
        public TestUser findByFirstAndLastName(String firstName, String lastName);
        
        @Select(sql = "select count(*) from users")
        public long count();
        
        @Select(sql = "select firstname || ' ' || lastname from users")
        public List<String> fullNames();

    }

    @Before
    public void setup() throws Exception {
        Context context = Context.builder()
                .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .driver("org.h2.Driver")
                .build();
        Statement stat = context.getConnection().createStatement();
        String sql = IOUtils.toString(this.getClass().getResourceAsStream("CREATE_DB.sql"), "UTF-8");
        stat.execute(sql);
    }

    @Test
    public void fetchAllTest() throws Exception {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser[] users = repository.fetchAll();
        assertTrue(users.length > 0);
    }

    @Test
    public void findByIdTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser user = repository.findById(2);
        assertEquals("Christie", user.getFirstName());
        assertEquals("Ljunggren", user.getLastName());
        assertEquals(101, user.getEmployeeID());
    }
    
    @Test
    public void findByFirstAndLastNameTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser user = repository.findByFirstAndLastName("Gage", "Ljunggren");
        assertEquals("Gage", user.getFirstName());
        assertEquals("Ljunggren", user.getLastName());
        assertEquals(102, user.getEmployeeID());
    }

    @Test
    public void findByFirstAndLastNameNoResultTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser user = repository.findByFirstAndLastName("Gage", "Smith");
        assertNull(user);
    }
    
    @Test
    public void countTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        long count = repository.count();
        assertTrue(count > 0);
    }
    
    @Test
    public void fullNamesTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        List<String> fullNames = repository.fullNames();
        assertEquals("Alex Ljunggren", fullNames.get(0));
        assertEquals("Christie Ljunggren", fullNames.get(1));
    }

}
