package com.ljunggren.storm.context;

import java.sql.Connection;
import java.sql.DriverManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Context {

    private String name;
    private String driver;
    private String url;
    private String username;
    private String password;
    
    public Connection getConnection() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
    
}
