package com.communication.model;

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


