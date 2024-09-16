package com.juliomesquita.rabbitmq.stream.infra.services;

import com.juliomesquita.rabbitmq.stream.infra.dtos.StreamRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class StreamService {
    private final Logger logger = LoggerFactory.getLogger(StreamService.class);

    private final RabbitStreamTemplate rabbitStreamTemplate;

    public StreamService(final RabbitStreamTemplate rabbitStreamTemplate) {
        this.rabbitStreamTemplate = Objects.requireNonNull(rabbitStreamTemplate);
    }

    public void publisher(StreamRequest request) {
        logger.info("Publisher new request: {}", request);
        this.rabbitStreamTemplate.convertAndSend(request);
    }
}
