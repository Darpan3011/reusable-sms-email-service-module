package com.communication.service.impl;

import com.azure.communication.sms.SmsClient;
import com.azure.communication.sms.models.SmsSendResult;
import com.communication.model.SmsRequest;
import com.communication.model.SmsResponse;
import com.communication.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "messaging.microsoft", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class MicrosoftSmsService implements MessageService {

    @Value("${azure.communication.sms.from-phone-number}")
    private String fromPhoneNumber;

    private final SmsClient smsClient;


    @Override
    public SmsResponse sendMessage(SmsRequest request) {
        try {
            SmsSendResult sendResult = smsClient.send(fromPhoneNumber, request.getTo(), request.getMessage());
            return SmsResponse.builder()
                    .messageId(sendResult.getMessageId())
                    .success(sendResult.isSuccessful())
                    .build();
        } catch (Exception e) {
            log.error("Microsoft send sms failed {}", e.getMessage(), e);
            throw new RuntimeException("Microsoft send sms failed", e);
        }

    }

    @Override
    public CompletableFuture<SmsResponse> sendMessageAsync(SmsRequest request) {
        return CompletableFuture.supplyAsync(() -> sendMessage(request));
    }
}
