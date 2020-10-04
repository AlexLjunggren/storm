package com.ljunggren.storm.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ljunggren.storm.Paging;
import com.ljunggren.storm.annotation.entity.ColumnProperty;
import com.ljunggren.storm.annotation.entity.Id;
import com.ljunggren.storm.annotation.entity.Table;
import com.ljunggren.storm.entity.Generated;

import lombok.Data;

public class QueryBuilderTest {
    
    @Data
    @Table(name = "banks")
    private class Bank {

        @Id(generated = Generated.AUTO)
        private int id;
        private String name;
        @ColumnProperty(name = "branch_id")
        private int branchId;
        
    }
    
    private String table = "banks";

    private Bank createBank() {
        Bank bank = new Bank();
        bank.setId(1);
        bank.setName("FirstBank");
        bank.setBranchId(101);
        return bank;
    }
    
    @Test
    public void buildPagingSQLTest() {
        QueryBuilder queryBuilder = new QueryBuilder();
        String sql = "select * from banks";
        Paging paging = new Paging(2, 2);
        String generatedSQL = queryBuilder.buildPagingSQL(sql, paging);
        String expectedSQL = "select * from banks offset 2 rows fetch next 2 rows only";
        assertEquals(expectedSQL, generatedSQL);
    }

    @Test
    public void getPagingArgsTest() {
        QueryBuilder queryBuilder = new QueryBuilder();
        Paging paging = new Paging(2, 2);
        Object[] args = new Object[] {paging, "FirstBank"};
        Object[] pagingArgs = queryBuilder.getPagingArgs(args);
        assertEquals(1, pagingArgs.length);
        assertEquals("FirstBank", pagingArgs[0]);
    }
    
    @Test
    public void buildInsertSQLTest() {
        QueryBuilder queryBuilder = new QueryBuilder(createBank());
        String sql = queryBuilder.buildInsertSQL();
        String expected = String.format(QueryBuilder.INSERT, table, "name, branch_id", "?, ?");
        assertEquals(expected, sql);
    }
    
    @Test
    public void getInsertArgsTest() {
        QueryBuilder queryBuilder = new QueryBuilder(createBank());
        Object[] args = queryBuilder.getInsertArgs();
        Object[] expecteds = new Object[] {"FirstBank", 101};
        assertArrayEquals(expecteds, args);
    }
    
    @Test
    public void buildUpdateSQLTest() {
        QueryBuilder queryBuilder = new QueryBuilder(createBank());
        String sql = queryBuilder.buildUpdateSQL();
        String expected = String.format(QueryBuilder.UPDATE, table, "name = ?, branch_id = ?", "id");
        assertEquals(expected, sql);
    }

    @Test
    public void getUpdateArgsTest() throws IllegalAccessException {
        QueryBuilder queryBuilder = new QueryBuilder(createBank());
        Object[] args = queryBuilder.getUpdateArgs();
        Object[] expecteds = new Object[] {"FirstBank", 101, 1};
        assertArrayEquals(expecteds, args);
    }
    
    @Test
    public void buildDeleteSQLTest() {
        QueryBuilder queryBuilder = new QueryBuilder(createBank());
        String sql = queryBuilder.buildDeleteSQL();
        String expected = String.format(QueryBuilder.DELETE, table, "id");
        assertEquals(expected, sql);
    }
    
    @Test
    public void getDeleteArgsTest() throws IllegalAccessException {
        QueryBuilder queryBuilder = new QueryBuilder(createBank());
        Object[] args = queryBuilder.getDeleteArgs();
        Object[] expecteds = new Object[] {1};
        assertArrayEquals(expecteds, args);
        
    }
    
}
