package com.darpan.communication.configuration.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "messaging.messagebird")
public class MessageBirdConfig {
    private String apiKey;
    private String senderId;
}
