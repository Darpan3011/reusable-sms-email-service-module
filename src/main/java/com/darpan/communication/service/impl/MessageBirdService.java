package com.darpan.communication.service.impl;

import com.darpan.communication.model.SmsRequest;
import com.darpan.communication.model.SmsResponse;
import com.darpan.communication.service.MessageService;
import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.MessageResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "messaging.messagebird", name = "enabled", havingValue = "true")
public class MessageBirdService implements MessageService {

    private MessageBirdClient client;

    @Value("${messaging.messagebird.api-key}")
    private String apiKey;

    @Value("${messaging.messagebird.sender-id}")
    private String sendId;

    @PostConstruct
    public void init(){
        this.client = new MessageBirdClient(new MessageBirdServiceImpl(apiKey));
    }

    @Override
    public SmsResponse sendMessage(SmsRequest request) {
        try {
            // sendMessage(originator, body, recipients)
            BigInteger recipient = new BigInteger(request.getTo().replaceAll("[^0-9]", ""));
            MessageResponse response = client.sendMessage(sendId, request.getMessage(), Collections.singletonList(recipient));

            // MessageResponse contains an id and other details
            String messageId = response != null ? response.getId() : null;

            return SmsResponse.builder().success(true).provider("MESSAGEBIRD").messageId(messageId).build();

        } catch (UnauthorizedException e) {
            log.error("MessageBird unauthorized: {}", e.getMessage());
            return SmsResponse.builder().success(false).provider("MESSAGEBIRD").error("Unauthorized: invalid API key").build();

        } catch (GeneralException e) {
            log.error("MessageBird general exception: {}", e.getErrors());
            return SmsResponse.builder().success(false).provider("MESSAGEBIRD").error(e.getErrors() != null ? e.getErrors().toString() : e.getMessage()).build();

        } catch (Exception e) {
            log.error("MessageBird unexpected error: {}", e.getMessage(), e);
            return SmsResponse.builder().success(false).provider("MESSAGEBIRD").error(e.getMessage()).build();
        }
    }

    @Override
    public CompletableFuture<SmsResponse> sendMessageAsync(SmsRequest request) {
        return CompletableFuture.supplyAsync(() -> sendMessage(request));
    }
}