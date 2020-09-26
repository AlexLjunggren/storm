package com.ljunggren.storm;

import com.ljunggren.storm.annotation.ColumnProperty;

import lombok.Data;

@Data
public class TestUser {

    private int id;
    private String firstName;
    private String lastName;
    
    @ColumnProperty(name = "employee_id")
    private int employeeID;
    
}
