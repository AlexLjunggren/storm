package com.ljunggren.storm.utils;

import java.util.function.Function;

public class ExceptionUtils {

    @FunctionalInterface
    public interface FunctionWithException<T, R, E extends Exception> {
        R apply(T t) throws E;
    }
    
    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(FunctionWithException<T, R, E> fe) {
        return arg -> {
            try {
                return fe.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }    

}
