package com.juliomesquita.rabbitmq.stream.commom.jackson.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperSupplier<T>
        extends FunctionExcept<FunctionExcept<ObjectMapper, T>, T> {
}
