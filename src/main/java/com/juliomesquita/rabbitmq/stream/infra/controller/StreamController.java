package com.juliomesquita.rabbitmq.stream.infra.controller;

import com.juliomesquita.rabbitmq.stream.infra.services.StreamService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(value = "/stream")
public class StreamController {
    private final StreamService streamService;

    public StreamController(final StreamService streamService) {
        this.streamService = Objects.requireNonNull(streamService);
    }
}
