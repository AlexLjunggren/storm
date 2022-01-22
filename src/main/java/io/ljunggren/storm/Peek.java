package io.ljunggren.storm;

import java.util.function.Consumer;

public interface Peek<T extends Peek<T>> {

    T peek(Consumer<String> peek);
    
}
