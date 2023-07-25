package com.observability.platform.config;

// Importing required classes
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

// Annotations
@EnableKafka
@Configuration

// Class
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory(String consumerGroup)
    {

        // Creating a Map of string-object pairs
        Map<String, Object> config = new HashMap<>();

        // Adding the Configuration
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "127.0.0.1:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                consumerGroup);
        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    // Creating a Listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory
    logKafkaListenerContainerFactory()
    {
        ConcurrentKafkaListenerContainerFactory<
                String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory("group_id"));
        return factory;
    }


    // ConcurrentKafkaListenerContainerFactory for 'appmetrics' topic
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> appMetricsKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory("app_metrics_group_id"));
        return factory;
    }

    // ConcurrentKafkaListenerContainerFactory for 'dbmetrics' topic
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> dbMetricsKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory("db_metrics_group_id"));
        return factory;
    }
}