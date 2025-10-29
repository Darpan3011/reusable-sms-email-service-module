package com.darpan.communication.service.impl;

import com.darpan.communication.model.SmsRequest;
import com.darpan.communication.model.SmsResponse;
import com.darpan.communication.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "messaging.smpp", name = "enabled", havingValue = "true")
public class SmppMessageService implements MessageService {

    private final ProducerTemplate producerTemplate;
    private final String smppEndpointUri;

    @Autowired
    public SmppMessageService(ProducerTemplate producerTemplate, @Qualifier("smppEndpointUri") String smppEndpointUri) {
        this.producerTemplate = producerTemplate;
        this.smppEndpointUri = smppEndpointUri;
    }

    public SmsResponse sendMessage(SmsRequest request) {
        try {
            Exchange exchange = ExchangeBuilder.anExchange(producerTemplate.getCamelContext())
                    .withHeader("CamelSmppDestAddr", List.of(request.getTo()))
                    .withHeader("CamelSmppSourceAddr", request.getFrom())
                    .withPattern(ExchangePattern.InOnly)
                    .withBody(request.getMessage())
                    .build();

            // Send the exchange using the injected ProducerTemplate
            Exchange responseExchange = producerTemplate.send(smppEndpointUri, exchange);

            // Extract the message ID from the response exchange
            String messageId = responseExchange.getIn().getHeader("CamelSmppId", String.class);

            if (responseExchange.isFailed()) {
                log.error("Failed to send SMS to {}, error: {}", request.getTo(), responseExchange.getException().getMessage());
                return SmsResponse.builder().success(false).provider("SMPP").error(responseExchange.getException().getMessage()).build();
            }

            log.info("Successfully sent SMS with ID {} to {}", messageId, request.getTo());
            return SmsResponse.builder().success(true).provider("SMPP").messageId(messageId).build();
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending SMS: {}", e.getMessage(), e);
            return SmsResponse.builder().success(false).provider("SMPP").error(e.getMessage()).build();
        }
    }

    @Override
    public CompletableFuture<SmsResponse> sendMessageAsync(SmsRequest request) {
        return CompletableFuture.supplyAsync(() -> sendMessage(request));
    }
}