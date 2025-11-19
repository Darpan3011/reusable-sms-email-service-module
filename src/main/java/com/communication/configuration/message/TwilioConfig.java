package com.communication.configuration.message;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "messaging.twilio")
public class TwilioConfig {
    private boolean enabled;
    private String sid;
    private String token;
    private String from;

    @PostConstruct
    public void init() {
        Twilio.init(sid, token);
    }
}
