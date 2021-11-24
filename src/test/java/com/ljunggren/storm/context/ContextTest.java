package com.ljunggren.storm.context;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.ljunggren.storm.exception.ContextException;

public class ContextTest {

    @Test
    public void getConnectionTest() throws SQLException {
        Context context = Context.builder()
                .driver("org.h2.Driver")
                .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .build();
        Connection connection = context.getConnection();
        assertTrue(connection != null);
        connection.close();
    }
    
    @Test(expected = ContextException.class)
    public void getConnectionInvalidDriver() {
        Context context = Context.builder()
                .driver("not.real.Driver")
                .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .build();
        context.getConnection();
    }

    @Test(expected = ContextException.class)
    public void getConnectionInvalidURL() {
        Context context = Context.builder()
                .driver("org.h2.Driver")
                .url("cbdj:h2:mem:test;DB_CLOSE_DELAY=-1")
                .build();
        context.getConnection();
    }

}
