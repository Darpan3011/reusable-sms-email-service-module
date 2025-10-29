package com.darpan.communication.service.impl;

import com.darpan.communication.model.SmsRequest;
import com.darpan.communication.model.SmsResponse;
import com.darpan.communication.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "messaging.sns", name = "enabled", havingValue = "true")
public class AwsSnsMessageService implements MessageService {

    private final SnsClient snsClient;

    public AwsSnsMessageService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    @Override
    public SmsResponse sendMessage(SmsRequest request) {
        try {
            PublishRequest publishRequest = PublishRequest.builder()
                    .message(request.getMessage())
                    .phoneNumber(request.getTo())
                    .build();

            PublishResponse result = snsClient.publish(publishRequest);

            return SmsResponse.builder()
                    .success(true)
                    .provider("AWS_SNS")
                    .messageId(result.messageId())
                    .build();

        } catch (Exception e) {
            log.error("AWS SNS send failed: {}", e.getMessage(), e);
            return SmsResponse.builder()
                    .success(false)
                    .provider("AWS_SNS")
                    .error(e.getMessage())
                    .build();
        }
    }

    @Override
    public CompletableFuture<SmsResponse> sendMessageAsync(SmsRequest request) {
        return CompletableFuture.supplyAsync(() -> sendMessage(request));
    }
}