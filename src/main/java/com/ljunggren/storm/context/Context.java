package com.ljunggren.storm.context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.ljunggren.storm.exceptions.ContextException;

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
    
    public Connection getConnection() {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e1) {
            throw new ContextException(String.format("Counld not load driver %s", driver));
        } catch (SQLException e) {
            throw new ContextException(String.format("Counld not connect to %s", url));
        }
     }
    
}
