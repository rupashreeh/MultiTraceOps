package com.sample.microservice;


import com.sample.microservice.observability.config.CustomDataSource;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

  private DataSource dataSource;

  @Primary
  @Bean
  public DataSource customDataSource() {
    DataSource dataSource = DataSourceBuilder.create()
            // Configure your data source properties here (e.g., URL, username, password)
            .url("jdbc:mysql://localhost:3306/test")
            .username("root")
            .password("password")
            .build();

    return new CustomDataSource(dataSource);
  }

  
  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    // Add any other Kafka producer configuration properties as needed

    return new DefaultKafkaProducerFactory<>(configProps);
  }
}

