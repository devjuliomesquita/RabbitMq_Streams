package com.juliomesquita.rabbitmq.stream.infra.consumer;

import com.juliomesquita.rabbitmq.stream.commom.stream.interfaces.StreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class StreamConsumer extends StreamListener<Object> {
    private final Logger logger = LoggerFactory.getLogger(StreamConsumer.class);

    @Retryable(interceptor = "streamRetryOperationsInterceptorFactoryBean")
    @Override
    public void onMessage(Object payload) {
        logger.info("Consumer message: {}", payload);
    }
}
