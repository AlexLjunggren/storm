package com.ljunggren.storm.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.ljunggren.storm.annotation.Param;

public class ParameterUtils {
    
    private final static String parameterRegex = "#\\{[a-zA-Z0-9_]+\\}";
    
    public static Map<String, Object> mapArgumentsToParameterNames(Parameter[] parameters, Object[] arguments) {
        String[] parameterNames = getParameterNames(parameters);
        return IntStream.range(0, parameterNames.length)
                .collect(HashMap::new, (m, i) -> m.put(parameterNames[i], arguments[i]), Map::putAll);
    }

    public static String[] getParameterNames(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(parameter -> getParameterName(parameter))
                .filter(name -> null != name)
                .toArray(String[]::new);
    }
    
    public static String getParameterName(Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();
        return Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType() == Param.class)
                .map(annotation -> ((Param) annotation).value())
                .findFirst()
                .orElse(null);
    }
    
    public static List<String> findParameterIds(String sql) {
        List<String> parameterIds = new ArrayList<>();
        Pattern pattern = Pattern.compile(parameterRegex);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            parameterIds.add(matcher.group(0).replace("#{", "").replace("}", ""));
        }
        return parameterIds;
    }
    
    public static String replaceParamaterIdsWithQuestionMarks(String sql) {
        return sql.replaceAll(parameterRegex, "?");
    }
    
}
