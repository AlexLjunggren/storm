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

import com.ljunggren.storm.Paging;
import com.ljunggren.storm.Peek;
import com.ljunggren.storm.StormRepository;
import com.ljunggren.storm.TestUser;
import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.Param;
import com.ljunggren.storm.annotation.crud.Select;
import com.ljunggren.storm.context.Context;
import com.ljunggren.storm.context.ContextFactory;
import com.ljunggren.storm.exceptions.StormException;

public class SelectQueryTest {

    @Database("H2")
    private interface UserRepository extends Peek<UserRepository> {
        
        @Select(sql = "select * from users order by id")
        public TestUser[] fetchAllOrdered();
        
        @Select(sql = "select * from users order by id")
        public TestUser[] fetchAllOrdered(Paging paging);
        
        @Select(sql = "select * from users where lastname = #{lastname}")
        public List<TestUser> findByLastName(@Param("lastname") String lastname, Paging paging);
        
        @Select(sql = "select * from users where lastname = #{lastname}")
        public List<TestUser> findByLastName(Paging paging, @Param("lastname") String lastname);
        
        @Select(sql = "select * from users where id = #{id}")
        public TestUser findById(@Param("id") int id);
        
        @Select(sql = "select * from users where firstname = #{firstname} and lastname = #{lastname}")
        public TestUser findByFirstAndLastName(@Param("firstname") String firstname, @Param("lastname") String lastname);
        
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
    public void fetchAllOrderedPagingTest() throws Exception {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        TestUser[] users = repository.fetchAllOrdered(new Paging(2, 2));
        assertEquals(102, users[0].getEmployeeID());
        assertEquals(103, users[1].getEmployeeID());
    }
    
    @Test
    public void findByLastNamePagingSecondTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        List<TestUser> users = repository.findByLastName("Ljunggren", new Paging(2, 2));
        assertEquals(1, users.size());
        assertEquals(102, users.get(0).getEmployeeID());
    }

    @Test
    public void findByLastNamePagingFirstTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        List<TestUser> users = repository.findByLastName(new Paging(2, 2), "Ljunggren");
        assertEquals(1, users.size());
        assertEquals(102, users.get(0).getEmployeeID());
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
        UserRepository repository = StormRepository.newInstance(UserRepository.class).peek(peek);
        repository.fetchAllOrdered();
        assertTrue(generatedSQL.contains("select * from users order by id"));
    }

}
