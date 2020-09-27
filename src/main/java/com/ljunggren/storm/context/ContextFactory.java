package com.ljunggren.storm.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.ljunggren.storm.utils.JSONUtils;

public class ContextFactory {

    public Context getContext(String name) {
        List<Context> contexts = loadContexts();
        return contexts.stream().filter(context -> context.getName().equals(name))
                .findFirst()
                .orElse(new Context());
    }
    
    private List<Context> loadContexts() {
        try {
            String json = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("context.json"), "UTF-8");
            return JSONUtils.jsonToListObject(json, Context.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<Context>();
    }
}
