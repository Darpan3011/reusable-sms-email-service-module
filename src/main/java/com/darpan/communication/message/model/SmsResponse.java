package com.darpan.communication.message.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponse {
    private boolean success;
    private String provider;
    private String messageId;
    private String error;
}