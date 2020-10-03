package com.ljunggren.storm;

import java.util.function.Consumer;

public interface StormPeek<T extends StormPeek<T>> {

    T peek(Consumer<String> peek);
    
}
