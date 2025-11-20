package com.communication.configuration.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "messaging.mail")
@Getter
@Setter
public class MailConfig {
    private boolean enabled = true;
    private String host;
    private int port = 587;
    private String username;
    private String password;
    private String defaultFrom;
    private int connectionTimeout = 5000;
    private int timeout = 30000;
    private int writeTimeout = 30000;
    private boolean auth = true;
    private boolean starttls = true;
    private boolean debug = false;
    private String protocol = "smtp";
    private String defaultEncoding = "UTF-8";
    private Map<String, String> additionalProperties = new HashMap<>();

}