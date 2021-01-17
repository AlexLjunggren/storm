package com.ljunggren.storm.builders;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ljunggren.storm.Paging;
import com.ljunggren.storm.builders.PagingQueryBuilder;
import com.ljunggren.storm.builders.QueryBuilder;

public class PagingQueryBuilderTest {

    @Test
    public void buildSQLTest() {
        String sql = "select * from banks";
        Paging paging = new Paging(2, 2);
        Object[] args = new Object[] { paging, "FirstBank" };
        QueryBuilder queryBuilder = new PagingQueryBuilder(sql, args, paging);
        String generatedSQL = queryBuilder.buildSQL();
        String expectedSQL = "select * from banks offset 2 rows fetch next 2 rows only";
        assertEquals(expectedSQL, generatedSQL);
    }

    @Test
    public void getArgsTest() throws IllegalAccessException {
        String sql = "select * from banks";
        Paging paging = new Paging(2, 2);
        Object[] args = new Object[] { paging, "FirstBank" };
        QueryBuilder queryBuilder = new PagingQueryBuilder(sql, args, paging);
        Object[] pagingArgs = queryBuilder.getArgs();
        assertEquals(1, pagingArgs.length);
        assertEquals("FirstBank", pagingArgs[0]);
    }

}