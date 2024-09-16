package com.juliomesquita.rabbitmq.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class StreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamApplication.class, args);
	}

}
