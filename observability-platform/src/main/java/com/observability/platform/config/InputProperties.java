package com.observability.platform.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:input.properties")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InputProperties {
    @Value("${sendEmail}")
    Boolean sendEmail;

    @Value("${sendSms}")
    Boolean $sendSms;

    @Value("${sendFileSystem}")
    Boolean sendFileSystem;

}
