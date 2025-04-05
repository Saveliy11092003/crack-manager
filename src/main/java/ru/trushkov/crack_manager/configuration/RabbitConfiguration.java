package ru.trushkov.crack_manager.configuration;

import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
public class RabbitConfiguration {

    @Value("${queue.request.worker1}")
    private String requestQueueName1;

    @Value("${queue.request.worker2}")
    private String requestQueueName2;

    @Value("${queue.request.worker3}")
    private String requestQueueName3;

    @Value("${queue.response}")
    private String responseQueueName;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${exchange.name}")
    private String exchangeName;

    @Bean("requestQueue1")
    public Queue requestQueue1() {
        return new Queue(requestQueueName1);
    }

    @Bean("requestQueue2")
    public Queue requestQueue2() {
        return new Queue(requestQueueName2);
    }

    @Bean("requestQueue3")
    public Queue requestQueue3() {
        return new Queue(requestQueueName3);
    }

    @Bean("responseQueue")
    public Queue responseQueue() {
        return new Queue(responseQueueName);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding binding1(Queue requestQueue1, DirectExchange exchange) {
        return BindingBuilder.bind(requestQueue1).to(exchange).with("task.worker1");
    }

    @Bean
    public Binding binding2(Queue requestQueue2, DirectExchange exchange) {
        return BindingBuilder.bind(requestQueue2).to(exchange).with("task.worker2");
    }

    @Bean
    public Binding binding3(Queue requestQueue3, DirectExchange exchange) {
        return BindingBuilder.bind(requestQueue3).to(exchange).with("task.worker3");
    }


    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        rabbitAdmin.declareExchange(exchange()); // Создаст exchange
        return rabbitAdmin;
    }


    @Bean
    public MessageConverter converter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        return converter;
    }


    @Bean
    public AmqpTemplate amqpTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}