package com.juliomesquita.rabbitmq.stream.infra.config;

import com.juliomesquita.rabbitmq.stream.commom.stream.interfaces.RabbitStreamTemplateSimpleFactory;
import com.juliomesquita.rabbitmq.stream.commom.stream.interfaces.StreamListener;
import com.juliomesquita.rabbitmq.stream.commom.stream.interfaces.StreamListenerContainerSimpleFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.config.SuperStream;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;

@Configuration
public class BeansConfig {
    private static final String STREAM = "Super_Stream";
    private static final Integer PARTITION = 3;

    @Bean
    public SuperStream superStream() {
        return new SuperStream(STREAM, PARTITION);
    }

    @Bean
    public RabbitStreamTemplate rabbitStreamTemplate(RabbitStreamTemplateSimpleFactory factory) {
        return factory.apply(STREAM);
    }

    @Bean
    public <T> StreamListenerContainer streamListenerContainer(
            StreamListenerContainerSimpleFactory<T> factory,
            StreamListener<T> listener
    ) {
        return factory.apply(STREAM, listener);
    }
}
