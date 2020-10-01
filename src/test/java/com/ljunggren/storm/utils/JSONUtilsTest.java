package com.ljunggren.storm.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ljunggren.storm.TestUser;

public class JSONUtilsTest {

    private TestUser getUser() {
        TestUser user = new TestUser();
        user.setFirstName("Alex");
        user.setEmployeeID(100);
        return user;
    }
    
    @Test
    public void isValidJsonTest() throws JsonGenerationException, JsonMappingException, IOException {
        String json = JSONUtils.objectToJson(getUser());
        boolean actual = JSONUtils.isValidJSON(json);
        assertTrue(actual);
    }

    @Test
    public void isValidJsonFailTest() throws JsonGenerationException, JsonMappingException, IOException {
        String json = JSONUtils.objectToJson(getUser());
        String brokenJson = json.substring(0, json.length()/2);
        boolean actual = JSONUtils.isValidJSON(brokenJson);
        assertFalse(actual);
    }

    @Test
    public void isValidJsonNullValueTest() throws JsonParseException, IOException {
        boolean actual = JSONUtils.isValidJSON(null);
        assertFalse(actual);
    }
    
    @Test
    public void objectToJsonTest() throws JsonGenerationException, JsonMappingException, IOException {
        String json = JSONUtils.objectToJson(getUser());
        Boolean valid = JSONUtils.isValidJSON(json);
        assertTrue(valid);
    }

    @Test
    public void objectToJsonNullTest() throws JsonGenerationException, JsonMappingException, IOException {
        String json = JSONUtils.objectToJson(null);
        Boolean valid = JSONUtils.isValidJSON(json);
        assertTrue(valid);
    }
    
    @Test
    public void jsonToListObjectTest() throws JsonMappingException, JsonProcessingException {
        List<String> expected = Arrays.asList(new String[] {"Alex", "Christie", "Gage"});
        String json = JSONUtils.objectToJson(expected);
        List<String> actual = JSONUtils.jsonToListObject(json, String.class);
        assertEquals(expected, actual);
    }
    

}
