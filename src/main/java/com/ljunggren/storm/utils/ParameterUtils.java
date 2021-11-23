package com.ljunggren.storm.utils;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParameterUtils {
    
    private final String parameterRegex = "#\\{[a-zA-Z0-9_]+\\}";
    
    public Map<String, Object> mapArgumentsToParameterNames(Parameter[] parameters, Object[] arguments) {
        return IntStream.range(0, parameters.length)
                .collect(HashMap::new, (m, i) -> m.put(parameters[i].getName(), arguments[i]), Map::putAll);
    }

    public List<String> findParameterIds(String sql) {
        return Stream.of(sql.split(" ")).filter(string -> string.matches(parameterRegex))
            .map(parameter -> parameter.replace("#{", "").replace("}", ""))
            .collect(Collectors.toList());
    }
    
    public String replaceParamaterIdsWithQuestionMarks(String sql) {
        return sql.replaceAll(parameterRegex, "?");
    }
    
}
