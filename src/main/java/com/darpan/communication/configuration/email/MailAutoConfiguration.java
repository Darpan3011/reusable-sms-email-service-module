package com.darpan.communication.configuration.email;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConditionalOnProperty(prefix = "messaging.mail", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MailConfig.class)
public class MailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender(MailConfig props) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(props.getHost());
        mailSender.setPort(props.getPort());

        if (props.getUsername() != null) {
            mailSender.setUsername(props.getUsername());
        }
        if (props.getPassword() != null) {
            mailSender.setPassword(props.getPassword());
        }

        mailSender.setDefaultEncoding(props.getDefaultEncoding());

        Properties mailProps = mailSender.getJavaMailProperties();
        mailProps.put("mail.transport.protocol", props.getProtocol());
        mailProps.put("mail.smtp.auth", Boolean.toString(props.isAuth()));
        mailProps.put("mail.smtp.starttls.enable", Boolean.toString(props.isStarttls()));
        mailProps.put("mail.smtp.connectiontimeout", Integer.toString(props.getConnectionTimeout()));
        mailProps.put("mail.smtp.timeout", Integer.toString(props.getTimeout()));
        mailProps.put("mail.smtp.writetimeout", Integer.toString(props.getWriteTimeout()));
        mailProps.put("mail.debug", Boolean.toString(props.isDebug()));
        mailProps.putAll(props.getAdditionalProperties());

        return mailSender;
    }
}