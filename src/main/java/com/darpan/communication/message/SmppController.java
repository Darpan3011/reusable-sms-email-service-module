package com.darpan.communication.message;

import com.darpan.communication.message.model.SmsRequest;
import com.darpan.communication.message.model.SmsResponse;
import com.darpan.communication.message.service.AwsSnsMessageService;
import com.darpan.communication.message.service.MessageBirdService;
import com.darpan.communication.message.service.SmppMessageService;
import com.darpan.communication.message.service.TwilioMessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smpp")
public class SmppController {

    private final TwilioMessageService twilioMessageService;
    private final SmppMessageService smppMessageService;
    private final MessageBirdService messageBirdService;
    private final AwsSnsMessageService awsSnsMessageService;

    public SmppController(TwilioMessageService twilioMessageService, SmppMessageService smppMessageService, MessageBirdService messageBirdService, AwsSnsMessageService awsSnsMessageService) {
        this.twilioMessageService = twilioMessageService;
        this.smppMessageService = smppMessageService;
        this.messageBirdService = messageBirdService;
        this.awsSnsMessageService = awsSnsMessageService;
    }

    @PostMapping("/smpp")
    public SmsResponse sendMessage(@RequestBody SmsRequest request) {
        return smppMessageService.sendMessage(request);
    }

    @PostMapping("/twilio")
    public SmsResponse sendTwilioMessage(@RequestBody SmsRequest request) {
        return twilioMessageService.sendMessage(request);
    }

    @PostMapping("/aws")
    public SmsResponse sendAwsMessage(@RequestBody SmsRequest request) {
        return awsSnsMessageService.sendMessage(request);
    }

    @PostMapping("/messagebird")
    public SmsResponse sendMessageBirdMessage(@RequestBody SmsRequest request) {
        return messageBirdService.sendMessage(request);
    }
}
