package com.communication.configuration.message;

import org.apache.camel.component.smpp.SmppConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Bean @Qualifier("smppEndpointUri")
    public String smppEndpointUri(SmppConfig smppConfig) {
        SmppConfig.Credentials creds = smppConfig.getConnections().getMainSmsc().getCredentials();
        return String.format("smpp://%s@%s:%d?password=%s",
                creds.getUsername(), creds.getHost(), creds.getPort(), creds.getPassword());
    }

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
    @ConditionalOnProperty(prefix = "messaging.smpp", name = "enabled", havingValue = "true")
    public SmppConfiguration smppConfiguration(SmppConfig smppConfig) {
        SmppConfiguration cfg = new SmppConfiguration();
        SmppConfig.Credentials creds = smppConfig.getConnections().getMainSmsc().getCredentials();

        cfg.setHost(creds.getHost());
        cfg.setPort(creds.getPort());
        cfg.setSystemId(creds.getUsername());
        cfg.setPassword(creds.getPassword());
        cfg.setSystemType(smppConfig.getDefaultConfig().getSystemType());
        cfg.setReconnectDelay(smppConfig.getDefaultConfig().getRebindTime());
        cfg.setMaxReconnect(5);
        cfg.setReconnectDelay(5000);

        return cfg;
    }
}
