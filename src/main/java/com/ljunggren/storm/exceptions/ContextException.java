package com.ljunggren.storm.exceptions;

public class ContextException extends RuntimeException {

    private static final long serialVersionUID = 3884552155337373601L;

    public ContextException(String message) {
        super(message);
    }
    
    public ContextException(Throwable cause) {
        super(cause);
    }
    
    public ContextException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
