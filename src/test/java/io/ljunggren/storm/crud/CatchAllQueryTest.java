package io.ljunggren.storm.crud;

import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Test;

import io.ljunggren.storm.StormRepository;
import io.ljunggren.storm.annotation.Database;

public class CatchAllQueryTest {
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface NonCrud {}

    @Database("H2")
    private interface UserRepository {
        
        @NonCrud
        public Boolean isHourly();
        
    }
        
    @Test
    public void hourlyTest() {
        UserRepository repository = StormRepository.newInstance(UserRepository.class);
        Boolean isHourly = repository.isHourly();
        assertTrue(isHourly == null);
    }

}
