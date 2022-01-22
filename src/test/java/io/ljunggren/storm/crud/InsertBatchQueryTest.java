package io.ljunggren.storm.crud;

import static org.junit.Assert.assertEquals;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import io.ljunggren.storm.Peek;
import io.ljunggren.storm.StormRepository;
import io.ljunggren.storm.TestUser;
import io.ljunggren.storm.annotation.Database;
import io.ljunggren.storm.annotation.crud.InsertBatch;
import io.ljunggren.storm.annotation.crud.Select;
import io.ljunggren.storm.context.Context;
import io.ljunggren.storm.context.ContextFactory;

public class InsertBatchQueryTest {

    @Database("H2")
    private interface UserRepository extends Peek<UserRepository> {
        
        @InsertBatch
        public int insert(TestUser... users);
        
        @InsertBatch
        public int insert(List<TestUser> users);
        
        @Select(sql = "select count(*) from users")
        public long count();
        
    }
    
    private List<String> generatedSQLs;

    private void addGeneratedSQL(String sql) {
        generatedSQLs.add(sql);
    }
    
    private TestUser getJane() {
        TestUser jane = new TestUser();
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        jane.setEmployeeID(104);
        return jane;
    }
    
    private TestUser getGreg() {
        TestUser greg = new TestUser();
        greg.setFirstName("Greg");
        greg.setLastName("Smith");
        greg.setEmployeeID(105);
        return greg;
    }

     @Before
    public void setup() throws Exception {
        generatedSQLs = new ArrayList<String>();
        Context context = new ContextFactory().getContext("H2");
        Statement stat = context.getConnection().createStatement();
        String sql = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("io/ljunggren/storm/CREATE_DB.sql"), "UTF-8");
        stat.execute(sql);
    }

    @Test
    public void insertBatchNativeEllipseTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        int inserts = repository.insert(getJane(), getGreg());
        assertEquals(2, inserts);
    }
    
    @Test
    public void insertBatchListTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        List<TestUser> users = Arrays.asList(new TestUser[] { getJane(), getGreg() });
        int inserts = repository.insert(users);
        assertEquals(2, inserts);
    }

    @Test
    public void peekTest() {
        Consumer<String> peek = e -> addGeneratedSQL(e);
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        repository.peek(peek);
        List<TestUser> users = Arrays.asList(new TestUser[] { getJane(), getGreg() });
        repository.insert(users);
        assertEquals(generatedSQLs.get(0), "insert into users (firstName, lastName, employee_id) values (?, ?, ?) : [Jane, Doe, 104]");
        assertEquals(generatedSQLs.get(1), "insert into users (firstName, lastName, employee_id) values (?, ?, ?) : [Greg, Smith, 105]");
    }

}
