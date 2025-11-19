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
    /**
     * Name of the default provider to use when no provider is specified.
     * e.g. "gmail"
     */
    private String defaultProvider = "gmail";

    /**
     * Map of provider name -> provider properties (gmail, outlook, etc.)
     */
    private Map<String, ProviderProperties> providers = new HashMap<>();

    @Getter
    @Setter
    public static class ProviderProperties {
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
}