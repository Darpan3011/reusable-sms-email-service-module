package com.darpan.communication.service;

import com.darpan.communication.model.SmsRequest;
import com.darpan.communication.model.SmsResponse;

import java.util.concurrent.CompletableFuture;

public interface MessageService {
    SmsResponse sendMessage(SmsRequest request);

    CompletableFuture<SmsResponse> sendMessageAsync(SmsRequest request);
}

