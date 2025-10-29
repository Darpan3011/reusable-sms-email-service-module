package com.darpan.communication.configuration;

import com.darpan.communication.configuration.message.AwsSnsConfig;
import com.darpan.communication.configuration.message.MessageBirdConfig;
import com.darpan.communication.configuration.message.SmppConfig;
import com.darpan.communication.configuration.message.TwilioConfig;
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