package com.juliomesquita.rabbitmq.stream.commom.stream.interfaces;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;

public abstract class StreamListener<T> extends TypeReference<T> {
    public void onMessage(T payload) {
        throw new UnsupportedOperationException("Method must be override.");
    }

    public void onMessage(T payload, MessageHandler.Context context) {
        this.onMessage(payload);
    }

    public void onMessage(T payload, MessageHandler.Context context, Message message) {
        this.onMessage(payload, context);
    }
}
