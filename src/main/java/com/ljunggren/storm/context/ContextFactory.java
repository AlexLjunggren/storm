package com.ljunggren.storm.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ljunggren.jsonUtils.JsonUtils;
import com.ljunggren.storm.exception.ContextException;

public class ContextFactory {
    
    private String definition = "context.json";
    
    public ContextFactory() {}
    
    public ContextFactory(String definition) {
        this.definition = definition;
    }

    public Context getContext(String name) {
        List<Context> contexts = loadContexts();
        return contexts.stream().filter(context -> context.getName().equals(name)).findFirst()
                .orElseThrow(() -> new ContextException(String.format("Could not find context for %s", name)));
    }
    
    private List<Context> loadContexts() {
        String json = readContextFile();
        return parseContextFile(json);
    }
    
    private String readContextFile() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(definition);
        if (inputStream == null) {
            throw new ContextException(String.format("Unable to read %s", definition));
        }
        try {
            String context = IOUtils.toString(inputStream, "UTF-8");
            return context;
        } catch (IOException e) {
            throw new ContextException(String.format("Unable to read %s", definition));
        }
    }
    
    private List<Context> parseContextFile(String json) {
        try {
            return JsonUtils.jsonToList(json, Context.class);
        } catch (JsonProcessingException e) {
            throw new ContextException(String.format("Unable to parse context.json", definition));
        }
    }
    
}
