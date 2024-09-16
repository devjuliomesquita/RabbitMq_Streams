package com.juliomesquita.rabbitmq.stream.commom.jackson.interfaces;

@FunctionalInterface
public interface FunctionExcept<T, R> {
    R apply(T t) throws Exception;

    default R sneakyThrows(T t){
        try {
            return apply(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
