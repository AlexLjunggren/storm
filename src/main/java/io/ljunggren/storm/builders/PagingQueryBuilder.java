package io.ljunggren.storm.builders;

import java.util.Arrays;

import io.ljunggren.storm.Paging;

public class PagingQueryBuilder extends QueryBuilder {
    
    private String sql;
    private Object[] args;
    private Paging paging;

    public static final String PAGING = "%s offset %d rows fetch next %d rows only";

    
    public PagingQueryBuilder(String sql, Object[] args, Paging paging) {
        this.sql = sql;
        this.args = args;
        this.paging = paging;
    }

    @Override
    public String buildSQL() {
        int page = paging.getPage() - 1;
        int rows = paging.getRows();
        int offset = page * rows;
        return String.format(PAGING, sql, offset, rows);
    }

    @Override
    public Object[] getArgs() throws IllegalAccessException {
        return Arrays.stream(args).filter(arg -> arg.getClass() != Paging.class).toArray();
    }

}