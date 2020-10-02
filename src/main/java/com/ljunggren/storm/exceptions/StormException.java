package com.ljunggren.storm.exceptions;

public class StormException extends RuntimeException {

    private static final long serialVersionUID = 1478572986254603169L;
    
    public StormException(String message) {
        super(message);
    }

}
