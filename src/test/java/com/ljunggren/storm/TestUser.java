package com.ljunggren.storm;

import com.ljunggren.storm.annotation.entity.ColumnProperty;
import com.ljunggren.storm.annotation.entity.Id;
import com.ljunggren.storm.annotation.entity.Table;
import com.ljunggren.storm.entity.Generated;

import lombok.Data;

@Data
@Table(name = "users")
public class TestUser {

    @Id(generated = Generated.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    
    @ColumnProperty(name = "employee_id")
    private int employeeID;
    private TestUser boss;
}
