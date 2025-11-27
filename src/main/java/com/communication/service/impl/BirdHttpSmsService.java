package com.communication.service.impl;

import com.communication.model.SmsRequest;
import com.communication.model.SmsResponse;
import com.communication.service.MessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "messaging.bird", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class BirdHttpSmsService implements MessageService {

    private WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${messaging.bird.base-url}")
    private String baseUrl;

    @Value("${messaging.bird.api-key}")
    private String apiKey;

    @Value("${messaging.bird.workspace-id}")
    private String workspaceId;

    @Value("${messaging.bird.channel-id}")
    private String channelId;

    @PostConstruct
    public void init() {
        String key = apiKey != null ? apiKey.trim() : "";
        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, key)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        log.debug("BirdHttpSmsService initialized (keyMask={}, workspace={}, channel={})",
                maskKey(key), workspaceId, channelId);
    }

    private String maskKey(String k) {
        if (k == null) return "null";
        String compact = k.replaceAll("\\s+", "");
        if (compact.length() <= 10) return "****";
        return compact.substring(0,4) + "****" + compact.substring(compact.length()-4);
    }

    @Override
    public SmsResponse sendMessage(SmsRequest request) {
        // Build payload same as your curl
        Map<String, Object> payload = Map.of(
                "body", Map.of("type", "text", "text", Map.of("text", request.getMessage())),
                "receiver", Map.of("contacts", List.of(Map.of(
                        "identifierValue", request.getTo(),
                        "identifierKey", "phonenumber"
                )))
        );

        String uri = String.format("/workspaces/%s/channels/%s/messages", workspaceId, channelId);

        try {
            String respBody = webClient.post()
                    .uri(uri)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Bird API response: {}", respBody);

            // Defensive parse to extract message id if present
            String messageId = extractMessageId(respBody);

            return SmsResponse.builder()
                    .success(true)
                    .provider("BIRD")
                    .messageId(messageId)
                    .build();

        } catch (WebClientResponseException e) {
            log.error("Bird API returned status {} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            return SmsResponse.builder()
                    .success(false)
                    .provider("BIRD")
                    .error("HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error calling Bird API", e);
            return SmsResponse.builder()
                    .success(false)
                    .provider("BIRD")
                    .error(e.getMessage())
                    .build();
        }
    }

    @Override
    public CompletableFuture<SmsResponse> sendMessageAsync(SmsRequest request) {
        return CompletableFuture.supplyAsync(() -> sendMessage(request));
    }

    private String extractMessageId(String respBody) {
        if (respBody == null || respBody.isBlank()) return null;
        try {
            JsonNode root = objectMapper.readTree(respBody);

            // Common possible locations: "id", "messageId", or inside an array/object like "data[0].id"
            if (root.has("id")) return root.get("id").asText();
            if (root.has("messageId")) return root.get("messageId").asText();
            if (root.has("data") && root.get("data").isArray() && root.get("data").size() > 0) {
                JsonNode first = root.get("data").get(0);
                if (first.has("id")) return first.get("id").asText();
                if (first.has("messageId")) return first.get("messageId").asText();
            }
            // sometimes Bird returns created resources under "result" or nested; try a shallow search
            JsonNode idNode = root.findValue("id");
            if (idNode != null && idNode.isTextual()) return idNode.asText();

        } catch (Exception ex) {
            log.debug("Failed to parse Bird response for id extraction", ex);
        }
        return null;
    }
}
