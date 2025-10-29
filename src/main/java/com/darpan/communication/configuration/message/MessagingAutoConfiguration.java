package com.darpan.communication.configuration.message;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class MessagingAutoConfiguration {

    @Bean("smppEndpointUri")
    public String smppEndpointUri() {
        return "smpp://{{smpp.connections.main-smsc.credentials.username}}@"
                + "{{smpp.connections.main-smsc.credentials.host}}:{{smpp.connections.main-smsc.credentials.port}}"
                + "?password={{smpp.connections.main-smsc.credentials.password}}";
    }

    @Bean
    @ConditionalOnProperty(prefix = "messaging.sns", name = "enabled", havingValue = "true")
    public SnsClient snsClient(AwsSnsConfig awsSnsConfig) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsSnsConfig.getAccessKey(), awsSnsConfig.getSecretKey());
        return SnsClient.builder()
                .region(Region.of(awsSnsConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
