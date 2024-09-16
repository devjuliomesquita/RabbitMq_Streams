package com.juliomesquita.rabbitmq.stream.commom.stream;

import com.juliomesquita.rabbitmq.stream.commom.jackson.interfaces.ObjectMapperSupplier;
import com.juliomesquita.rabbitmq.stream.commom.stream.interfaces.RabbitStreamTemplateSimpleFactory;
import com.juliomesquita.rabbitmq.stream.commom.stream.interfaces.StreamListenerContainerSimpleFactory;
import com.juliomesquita.rabbitmq.stream.commom.stream.interfaces.StreamListenerMessageConverter;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import com.rabbitmq.stream.impl.StreamEnvironmentBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.retry.StreamRetryOperationsInterceptorFactoryBean;
import org.springframework.retry.support.RetryTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.delayedExecutor;

@Configuration
class StreamConfig {
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public Environment environment(RabbitProperties rabbitProperties) {
        return new StreamEnvironmentBuilder()
                .host(rabbitProperties.getHost())
                .port(rabbitProperties.getPort())
                .username(rabbitProperties.getUsername())
                .password(rabbitProperties.getPassword())
                .virtualHost(rabbitProperties.getVirtualHost())
                .build();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public <T> StreamListenerMessageConverter<T> streamListenerMessageConverter(ObjectMapperSupplier<T> objectMapperSupplier) {
        return streamListener -> new SimpleStreamMessageListener<>(streamListener, objectMapperSupplier);
    }

//    @Bean
//    public Exchange exchange() {
//        return ExchangeBuilder.directExchange(applicationName).build();
//    }
//
//    @Bean
//    public Queue queue() {
//        return QueueBuilder
//                .durable("partition-1")
//                .stream()
//                .withArgument("x-max-age", "70")
//                .withArgument("x-max-length-bytes", DataSize.ofGigabytes(10).toBytes())
//                .build();
//    }
//
//    @Bean
//    public Binding binding(Exchange exchange, Queue queue) {
//        return BindingBuilder
//                .bind(queue)
//                .to(exchange)
//                .with("")
//                .noargs();
//    }
//
//    @Bean
//    public RabbitStreamTemplate rabbitStreamTemplate(
//            Environment environment,
//            Exchange exchange,
//            Jackson2JsonMessageConverter jackson2JsonMessageConverter
//    ) {
//        RabbitStreamTemplate template = new RabbitStreamTemplate(environment, exchange.getName());
//        template.setMessageConverter(jackson2JsonMessageConverter);
//        template.setProducerCustomizer((s, builder) -> builder.routing(Objects::toString).producerBuilder());
//        return template;
//    }
//
//    @Bean
//    public RabbitListenerContainerFactory<StreamListenerContainer> streamContainerFactory(Environment environment) {
//        StreamRabbitListenerContainerFactory factory = new StreamRabbitListenerContainerFactory(environment);
//        factory.setNativeListener(true);
//        factory.setConsumerCustomizer((id, builder) ->
//                builder.name(applicationName).offset(OffsetSpecification.first()).manualTrackingStrategy());
//        return factory;
//    }

    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .infiniteRetry()
                .exponentialBackoff(
                        TimeUnit.SECONDS.toMillis(10), 1.5, TimeUnit.MINUTES.toMillis(5))
                .build();
    }

    @Bean
    public StreamRetryOperationsInterceptorFactoryBean streamRetryOperationsInterceptorFactoryBean(RetryTemplate retryTemplate) {
        StreamRetryOperationsInterceptorFactoryBean factoryBean = new StreamRetryOperationsInterceptorFactoryBean();
        factoryBean.setRetryOperations(retryTemplate);
        return factoryBean;
    }

    @Bean
    public RabbitStreamTemplateSimpleFactory rabbitStreamTemplateSimpleFactory(
            Environment environment,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter
    ) {
        return stream -> {
            RabbitStreamTemplate rabbitStreamTemplate = new RabbitStreamTemplate(environment, stream);
            rabbitStreamTemplate.setMessageConverter(jackson2JsonMessageConverter);
            rabbitStreamTemplate.setSuperStreamRouting(msg -> UUID.randomUUID().toString());
            return rabbitStreamTemplate;
        };
    }

    @Bean
    public <T> StreamListenerContainerSimpleFactory<T> streamListenerContainerSimpleFactory(
            Environment environment,
            StreamListenerMessageConverter<T> streamListenerMessageConverter
    ) {
        return (stream, streamListener) -> {
            StreamListenerContainer streamListenerContainer = new StreamListenerContainer(environment);
            streamListenerContainer.setAutoStartup(false);
            streamListenerContainer.superStream(stream, applicationName);
            streamListenerContainer.setupMessageListener(streamListenerMessageConverter.apply(streamListener));
            streamListenerContainer.setConsumerCustomizer(
                    (id, builder) -> builder.offset(OffsetSpecification.first()).manualTrackingStrategy());
            delayedExecutor(5, TimeUnit.SECONDS).execute(streamListenerContainer::start);
            return streamListenerContainer;
        };

    }
}
