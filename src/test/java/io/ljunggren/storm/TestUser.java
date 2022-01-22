package io.ljunggren.storm;

import io.ljunggren.storm.annotation.entity.ColumnProperty;
import io.ljunggren.storm.annotation.entity.Id;
import io.ljunggren.storm.annotation.entity.Table;
import io.ljunggren.storm.entity.Generated;
import lombok.Data;

@Data
@Table("users")
public class TestUser {

    @Id(generated = Generated.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    
    @ColumnProperty(name = "employee_id")
    private int employeeID;
    
}
