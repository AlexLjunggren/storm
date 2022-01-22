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
import io.ljunggren.storm.annotation.crud.Delete;
import io.ljunggren.storm.annotation.crud.Select;
import io.ljunggren.storm.context.Context;
import io.ljunggren.storm.context.ContextFactory;
import io.ljunggren.storm.exception.StormException;

public class DeleteQueryTest {

    @Database("H2")
    private interface UserRepository extends Peek<UserRepository> {
        
        @Delete(sql = "delete from users where id = #{id}")
        public int deleteById(int id);
        
        @Delete
        public int delete(TestUser user);
        
        @Delete
        public int deleteAll(TestUser... users);
        
        @Delete(sql = "nonsense")
        public int nonsense();
        
        @Select(sql = "select count(*) from users")
        public long count();
        
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
        String sql = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("io/ljunggren/storm/CREATE_DB.sql"), "UTF-8");
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
    
    @Test
    public void deleteAllTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        long beforeCount = repository.count();
        TestUser user1 = repository.findById(1);
        TestUser user2 = repository.findById(2);
        int deletes = repository.deleteAll(user1, user2);
        long afterCount = repository.count();
        assertEquals(2, deletes);
        assertTrue(beforeCount > afterCount);
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
        repository.deleteById(1);
        assertEquals(generatedSQL, "delete from users where id = ? : [1]");
    }

}
