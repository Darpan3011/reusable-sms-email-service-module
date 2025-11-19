package com.communication.configuration;

import com.communication.configuration.message.AwsSnsConfig;
import com.communication.configuration.message.MessageBirdConfig;
import com.communication.configuration.message.SmppConfig;
import com.communication.configuration.message.TwilioConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
    AwsSnsConfig.class,
    TwilioConfig.class,
    MessageBirdConfig.class,
    SmppConfig.class
})
@AutoConfiguration
public class CommunicationAutoConfiguration {

}