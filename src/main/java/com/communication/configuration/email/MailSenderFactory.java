package com.communication.configuration.email;

import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class MailSenderFactory {
    private final Map<String, JavaMailSender> senders;
    private final String defaultProvider;

    public MailSenderFactory(Map<String, JavaMailSender> senders, String defaultProvider) {
        this.senders = Collections.unmodifiableMap(Objects.requireNonNull(senders));
        this.defaultProvider = defaultProvider;
    }

    /**
     * Return sender for the given provider name. If providerName is null or not found,
     * return the default provider sender.
     */
    public JavaMailSender getSender(String providerName) {
        if (providerName == null || providerName.isBlank()) {
            return getDefaultSender();
        }
        JavaMailSender sender = senders.get(providerName.toLowerCase());
        return sender == null ? getDefaultSender() : sender;
    }

    public JavaMailSender getDefaultSender() {
        JavaMailSender s = senders.get(defaultProvider);
        if (s == null) {
            throw new IllegalStateException("Default mail sender '" + defaultProvider + "' not configured");
        }
        return s;
    }

    public Map<String, JavaMailSender> getAllSenders() {
        return senders;
    }

    public String getDefaultProvider() {
        return defaultProvider;
    }
}
