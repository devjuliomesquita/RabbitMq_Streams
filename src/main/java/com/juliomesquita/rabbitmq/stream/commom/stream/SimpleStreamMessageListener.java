package com.juliomesquita.rabbitmq.stream.commom.stream;

import com.juliomesquita.rabbitmq.stream.commom.jackson.interfaces.ObjectMapperSupplier;
import com.juliomesquita.rabbitmq.stream.commom.stream.interfaces.StreamListener;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;
import org.springframework.rabbit.stream.listener.StreamMessageListener;

import java.util.Objects;

public class SimpleStreamMessageListener<T> implements StreamMessageListener {
    private final StreamListener<T> streamListener;
    private final ObjectMapperSupplier<T> objectMapperSupplier;

    public SimpleStreamMessageListener(
            final StreamListener<T> streamListener,
            final ObjectMapperSupplier<T> objectMapperSupplier
    ) {
        this.streamListener = Objects.requireNonNull(streamListener);
        this.objectMapperSupplier = Objects.requireNonNull(objectMapperSupplier);
    }

    @Override
    public void onStreamMessage(Message message, MessageHandler.Context context) {
        streamListener.onMessage(
                this.objectMapperSupplier.sneakyThrows(
                        ob -> ob.readValue(message.getBodyAsBinary(), this.streamListener)),
                context,
                message);
    }

    @Override
    public void onMessage(org.springframework.amqp.core.Message message) {
        throw new UnsupportedOperationException();
    }
}
