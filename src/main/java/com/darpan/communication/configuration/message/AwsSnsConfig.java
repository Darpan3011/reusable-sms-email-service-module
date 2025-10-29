package com.darpan.communication.configuration.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "messaging.sns")
public class AwsSnsConfig {
    private boolean enabled;
    private String accessKey;
    private String secretKey;
    private String region;
}

