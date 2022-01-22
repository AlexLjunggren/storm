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

public class InsertBatchQueryBuilderTest {

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

    private Bank createFirstBank() {
        Bank bank = new Bank();
        bank.setId(1);
        bank.setName("FirstBank");
        bank.setBranchId(101);
        bank.setHeadquarters(new Bank());
        return bank;
    }
    
    private Bank createUnitedBank() {
        Bank bank = new Bank();
        bank.setId(2);
        bank.setName("UnitedBank");
        bank.setBranchId(102);
        bank.setHeadquarters(new Bank());
        return bank;
    }
    
    @Test
    public void buildSQLTest() {
        Bank firstBank = createFirstBank();
        Bank unitedBank = createUnitedBank();
        Bank[] banks = new Bank[] {firstBank, unitedBank};
        QueryBuilder queryBuilder = new InsertBatchQueryBuilder(banks);
        String sql = queryBuilder.buildSQL();
        String expected = String.format(InsertQueryBuilder.INSERT, table, "name, branch_id", "?, ?");
        assertEquals(expected, sql);
    }
    
    @Test
    public void getArgsTest() throws IllegalAccessException {
        Bank firstBank = createFirstBank();
        Bank unitedBank = createUnitedBank();
        Bank[] banks = new Bank[] {firstBank, unitedBank};
        QueryBuilder queryBuilder = new InsertBatchQueryBuilder(banks);
        Object[] args = queryBuilder.getArgs();
        assertArrayEquals(new Object[] { "FirstBank", 101 }, (Object[]) args[0]);
        assertArrayEquals(new Object[] { "UnitedBank", 102 }, (Object[]) args[1]);
    }

}