package com.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.ljunggren.storm.StormPeek;
import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;
import com.ljunggren.storm.exceptions.StormException;

public class SelectQueryTest {

    @Database(context = "H2")
    private interface UserRepository extends StormPeek<UserRepository> {
        
        @Select(sql = "select * from users order by id")
        public TestUser[] fetchAllOrdered();
        
        @Select(sql = "select * from users where id = ?")
        public TestUser findById(int id);
        
        @Select(sql = "select * from users where firstname = ? and lastname = ?")
        public TestUser findByFirstAndLastName(String firstName, String lastName);
        
        @Select(sql = "select count(*) from users")
        public long count();
        
        @Select(sql = "select firstname || ' ' || lastname from users")
        public List<String> fullNames();

        @Select(sql = "select * from users")
        public Set<TestUser> fetchAll();

        @Select(sql = "nonsense")
        public int nonsense();
        
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
    public void fetchAllOrderedTest() throws Exception {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser[] users = repository.fetchAllOrdered();
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
    public void findByIdNoResultTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser user = repository.findById(999);
        assertNull(user);
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

    @Test
    public void fetchAllTest() throws Exception {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        Set<TestUser> users = repository.fetchAll();
        assertTrue(users.size() > 0);
    }

    @Test(expected = StormException.class)
    public void nonsenseTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        repository.nonsense();
    }

    @Test
    public void peekTest() {
        Consumer<String> peek = e -> setGeneratedSQL(e);
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        repository.peek(peek).fetchAllOrdered();
        assertTrue(generatedSQL.contains("select * from users order by id"));
    }

}
