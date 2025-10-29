package com.darpan.communication.service.impl;

import com.darpan.communication.model.SmsRequest;
import com.darpan.communication.model.SmsResponse;
import com.darpan.communication.service.MessageService;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "messaging.twilio", name = "enabled", havingValue = "true")
public class TwilioMessageService implements MessageService {

    @Value("${messaging.twilio.from}")
    private String fromNumber;

    @Value("${messaging.twilio.sid}")
    private String accountSid;

    @Value("${messaging.twilio.token}")
    private String authToken;

    @Override
    public SmsResponse sendMessage(SmsRequest request) {
        try {
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(request.getTo()),
                    new com.twilio.type.PhoneNumber(fromNumber),
                    request.getMessage()
            ).create();

            return SmsResponse.builder()
                    .success(true)
                    .provider("Twilio")
                    .messageId(message.getSid() + " " + message.getStatus().toString())
                    .build();

        } catch (Exception e) {
            log.error("Twilio send failed: {}", e.getMessage());
            return SmsResponse.builder()
                    .success(false)
                    .provider("Twilio")
                    .error(e.getMessage())
                    .build();
        }
    }

    @Override
    public CompletableFuture<SmsResponse> sendMessageAsync(SmsRequest request) {
        return CompletableFuture.supplyAsync(() -> sendMessage(request));
    }
}
