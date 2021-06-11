package com.ljunggren.storm;

import java.util.List;

import com.ljunggren.storm.annotation.Database;
import com.ljunggren.storm.annotation.crud.Select;

@Database("H2")
public interface TestUserRepository {
    
    @Select(sql = "select * from table")
    public List<TestUser> fetch();
    
}
