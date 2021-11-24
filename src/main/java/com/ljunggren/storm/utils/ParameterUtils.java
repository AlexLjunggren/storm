package com.ljunggren.storm.utils;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ParameterUtils {
    
    private final String parameterRegex = "#\\{[a-zA-Z0-9_]+\\}";
    
    public Map<String, Object> mapArgumentsToParameterNames(Parameter[] parameters, Object[] arguments) {
        return IntStream.range(0, parameters.length)
                .collect(HashMap::new, (m, i) -> m.put(parameters[i].getName(), arguments[i]), Map::putAll);
    }

    public List<String> findParameterIds(String sql) {
        List<String> parameterIds = new ArrayList<>();
        Pattern pattern = Pattern.compile(parameterRegex);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            parameterIds.add(matcher.group(0).replace("#{", "").replace("}", ""));
        }
        return parameterIds;
    }
    
    public String replaceParamaterIdsWithQuestionMarks(String sql) {
        return sql.replaceAll(parameterRegex, "?");
    }
    
}
