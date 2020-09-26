package com.ljunggren.storm;

import java.sql.Connection;
import java.sql.DriverManager;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Context {

    private String name;
    private String url; 
    private String host;
    private int port;
    private String sid;
    private String username;
    private String password;
    
    @Builder.Default
    private String driver = "oracle.jdbc.driver.OracleDriver";
    
    public Connection getConnection() throws Exception {
        Class.forName(driver);
        String url = this.url == null ? String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, sid) : this.url;
        return username == null ? DriverManager.getConnection(url) : DriverManager.getConnection(url, username, password);
    }
    
}
