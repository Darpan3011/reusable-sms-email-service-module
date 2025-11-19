package com.communication.service;

import com.communication.model.SmsRequest;
import com.communication.model.SmsResponse;

import java.util.concurrent.CompletableFuture;

public interface MessageService {
    SmsResponse sendMessage(SmsRequest request);

    CompletableFuture<SmsResponse> sendMessageAsync(SmsRequest request);
}

