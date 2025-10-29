package com.darpan.communication.configuration.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "smpp")
public class SmppConfig {
    private DefaultConfig defaultConfig = new DefaultConfig();
    private Connections connections = new Connections();

    @Getter @Setter
    public static class DefaultConfig {
        private int maxTry;
        private long rebindTime;
        private String systemType;
    }

    @Getter @Setter
    public static class Connections {
        private MainSmsc mainSmsc = new MainSmsc();
    }

    @Getter @Setter
    public static class MainSmsc {
        private Credentials credentials = new Credentials();
    }

    @Getter @Setter
    public static class Credentials {
        private String host;
        private int port;
        private String username;
        private String password;
    }
}

