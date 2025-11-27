package com.communication.configuration.message;

import com.azure.communication.sms.SmsClient;
import com.azure.communication.sms.SmsClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.time.Duration;

@Configuration
public class MessagingAutoConfiguration {

    @Value("${azure.communication.connection-string:}")
    private String connectionString;

    @Bean
    @ConditionalOnProperty(prefix = "messaging.sns", name = "enabled", havingValue = "true")
    public SnsClient snsClient(AwsSnsConfig awsSnsConfig) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsSnsConfig.getAccessKey(), awsSnsConfig.getSecretKey());
        return SnsClient.builder()
                .region(Region.of(awsSnsConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(5))
                        .socketTimeout(Duration.ofSeconds(30))
                        .maxConnections(50)
                        .connectionAcquisitionTimeout(Duration.ofSeconds(10)))
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "messaging.microsoft", name = "enabled", havingValue = "true")
    public SmsClient smsClient() {
        return new SmsClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }
}
