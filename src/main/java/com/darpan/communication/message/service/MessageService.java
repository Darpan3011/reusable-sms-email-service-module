package com.darpan.communication.message.service;

import com.darpan.communication.message.model.SmsRequest;
import com.darpan.communication.message.model.SmsResponse;

public interface MessageService {
    SmsResponse sendMessage(SmsRequest request);
}

