package com.darpan.communication.configuration.message;

import org.apache.camel.component.smpp.SmppConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

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

        return cfg;
    }
}
