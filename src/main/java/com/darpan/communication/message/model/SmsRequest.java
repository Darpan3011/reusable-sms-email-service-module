package com.darpan.communication.message.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {
    private String to;
    private String message;
    private String from;
}


