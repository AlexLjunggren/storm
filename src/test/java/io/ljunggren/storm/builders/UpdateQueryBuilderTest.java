package io.ljunggren.storm.builders;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.ljunggren.storm.annotation.entity.ColumnProperty;
import io.ljunggren.storm.annotation.entity.Id;
import io.ljunggren.storm.annotation.entity.Table;
import io.ljunggren.storm.annotation.entity.Transient;
import io.ljunggren.storm.entity.Generated;
import lombok.Data;

public class UpdateQueryBuilderTest {

    @Data
    @Table("banks")
    private class Bank {

        @Id(generated = Generated.AUTO)
        private int id;
        private String name;
        @ColumnProperty(name = "branch_id")
        private int branchId;
        @Transient
        private boolean closed;
        private Bank headquarters;
        
    }
    
    private String table = "banks";

    private Bank createBank() {
        Bank bank = new Bank();
        bank.setId(1);
        bank.setName("FirstBank");
        bank.setBranchId(101);
        bank.setHeadquarters(new Bank());
        return bank;
    }
    
    @Test
    public void buildSQLTest() {
        QueryBuilder queryBuilder = new UpdateQueryBuilder(createBank());
        String sql = queryBuilder.buildSQL();
        String expected = String.format(UpdateQueryBuilder.UPDATE, table, "name = ?, branch_id = ?", "id");
        assertEquals(expected, sql);
    }

    @Test
    public void getArgsTest() throws IllegalAccessException {
        QueryBuilder queryBuilder = new UpdateQueryBuilder(createBank());
        Object[] args = queryBuilder.getArgs();
        Object[] expecteds = new Object[] { "FirstBank", 101, 1 };
        assertArrayEquals(expecteds, args);
    }

}