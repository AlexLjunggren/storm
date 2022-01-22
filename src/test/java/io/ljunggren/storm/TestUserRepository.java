package io.ljunggren.storm;

import java.util.List;

import io.ljunggren.storm.annotation.Database;
import io.ljunggren.storm.annotation.crud.Select;

@Database("H2")
public interface TestUserRepository {
    
    @Select(sql = "select * from table")
    public List<TestUser> fetch();
    
}
