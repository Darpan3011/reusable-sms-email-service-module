package com.communication.configuration.email;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(prefix = "messaging.mail", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MailConfig.class)
public class MailAutoConfiguration {

    @Bean
    public MailSenderFactory mailSenderFactory(MailConfig props) {
        Map<String, JavaMailSender> map = new HashMap<>();

        // create sender for each provider configured
        props.getProviders().forEach((name, provider) -> {
            if (!provider.isEnabled()) return;

            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(provider.getHost());
            mailSender.setPort(provider.getPort());
            if (provider.getUsername() != null) mailSender.setUsername(provider.getUsername());
            if (provider.getPassword() != null) mailSender.setPassword(provider.getPassword());
            mailSender.setDefaultEncoding(provider.getDefaultEncoding());

            Properties mailProps = mailSender.getJavaMailProperties();
            mailProps.put("mail.transport.protocol", provider.getProtocol());
            mailProps.put("mail.smtp.auth", Boolean.toString(provider.isAuth()));
            mailProps.put("mail.smtp.starttls.enable", Boolean.toString(provider.isStarttls()));
            mailProps.put("mail.smtp.connectiontimeout", Integer.toString(provider.getConnectionTimeout()));
            mailProps.put("mail.smtp.timeout", Integer.toString(provider.getTimeout()));
            mailProps.put("mail.smtp.writetimeout", Integer.toString(provider.getWriteTimeout()));
            mailProps.put("mail.debug", Boolean.toString(provider.isDebug()));
            mailProps.putAll(provider.getAdditionalProperties());

            map.put(name.toLowerCase(), mailSender);
        });

        return new MailSenderFactory(map, props.getDefaultProvider());
    }

    /**
     * Backwards-compatible primary JavaMailSender bean (the default provider).
     * This lets existing modules autowire JavaMailSender and keep working.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender(MailSenderFactory factory) {
        return factory.getDefaultSender();
    }
}