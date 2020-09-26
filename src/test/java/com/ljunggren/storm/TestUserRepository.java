package com.ljunggren.storm;

import java.util.List;

import com.ljunggren.storm.annotation.Connection;
import com.ljunggren.storm.annotation.Select;

@Connection(context = "H2")
public interface TestUserRepository {
    
    @Select(sql = "select * from table")
    public List<TestUser> fetch();
    
}
