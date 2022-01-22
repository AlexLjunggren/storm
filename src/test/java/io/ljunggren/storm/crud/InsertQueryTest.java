package io.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Statement;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import io.ljunggren.storm.Peek;
import io.ljunggren.storm.StormRepository;
import io.ljunggren.storm.TestUser;
import io.ljunggren.storm.annotation.Database;
import io.ljunggren.storm.annotation.Param;
import io.ljunggren.storm.annotation.crud.Insert;
import io.ljunggren.storm.annotation.crud.Select;
import io.ljunggren.storm.context.Context;
import io.ljunggren.storm.context.ContextFactory;
import io.ljunggren.storm.exception.StormException;

public class InsertQueryTest {

    @Database("H2")
    private interface UserRepository extends Peek<UserRepository> {
        
        @Insert(sql = "insert into users (firstname, lastname, employee_id) values (#{firstname}, #{lastname}, #{employeeID})")
        public int insert(@Param("firstname") String firstname, @Param("lastname") String lastname, @Param("employeeID") int employeeID);

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
        String sql = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("io/ljunggren/storm/CREATE_DB.sql"), "UTF-8");
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
        UserRepository repository = StormRepository.newInstance(UserRepository.class).peek(peek);
        repository.insert("Jane", "Doe", 104);
        assertEquals(generatedSQL, "insert into users (firstname, lastname, employee_id) values (?, ?, ?) : [Jane, Doe, 104]");
    }

}
