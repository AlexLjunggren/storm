package io.ljunggren.storm.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;

public class ResultSetMapper {

    private ResultSet resultSet;
    private Type returnType;
    
    public ResultSetMapper(ResultSet resultSet, Type returnType) {
        this.resultSet = resultSet;
        this.returnType = returnType;
    }
    
    public Object map() throws Exception {
        return getChain().map(resultSet, returnType);
    }
    
    private MapperChain getChain() {
        return new PrimitiveMapper().nextChain(
                new ObjectMapper().nextChain(
                new ArrayMapper().nextChain(
                new ListMapper().nextChain(
                new SetMapper().nextChain(
                new CatchAllMapper()
                        )))));
    }
    
}
