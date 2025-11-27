# Communication Module

A reusable Spring Boot starter library for Email and SMS with async support and multiple providers (Twilio, AWS SNS, MessageBird, SMPP).

## Overview
- **Artifact:** `com.darpan:communication:1.0.1`
- **Java:** 17, **Spring Boot:** 3.5.7, **Packaging:** jar
- Exposes `EmailService` and `MessageService` with sync/async methods

## Features
- **Email**: HTML, attachments (classpath, filesystem, MultipartFile), async
- **SMS**: Twilio, AWS SNS, MessageBird, SMPP (Camel), sync/async
- **Infra**: AWS SNS client pooling (Apache HTTP), custom async executor

## Install
```xml
<dependency>
  <groupId>com.darpan</groupId>
  <artifactId>communication</artifactId>
  <version>1.0.1</version>
</dependency>
```

## Configuration

### Email (default enabled)
Backed by `MailAutoConfiguration` + `MailConfig` (`messaging.mail.*`).
```properties
messaging.mail.enabled=true
messaging.mail.host=smtp.gmail.com
messaging.mail.port=587
messaging.mail.username=your-email@gmail.com
messaging.mail.password=your-app-password
messaging.mail.auth=true
messaging.mail.starttls=true
# Optional
messaging.mail.connection-timeout=5000
messaging.mail.timeout=30000
messaging.mail.write-timeout=30000
messaging.mail.protocol=smtp
messaging.mail.default-encoding=UTF-8
messaging.mail.debug=false
# Additional JavaMail properties (passed directly to JavaMailSender)
messaging.mail.additional-properties.mail.smtp.ssl.trust=*
messaging.mail.additional-properties.mail.smtp.connectiontimeout=5000
```
Note: If you define your own `JavaMailSender` bean, the starter backs off.

### Twilio
```properties
messaging.twilio.enabled=true
messaging.twilio.sid=your-account-sid
messaging.twilio.token=your-auth-token
messaging.twilio.from=+1234567890
```

### AWS SNS
```properties
messaging.sns.enabled=true
messaging.sns.access-key=your-access-key
messaging.sns.secret-key=your-secret-key
messaging.sns.region=us-east-1
```

### MessageBird
```properties
messaging.messagebird.enabled=true
messaging.messagebird.api-key=your-api-key
messaging.messagebird.sender-id=YourSenderID
```

### Azure Communication Services (SMS)
```properties
messaging.microsoft.enabled=true
azure.communication.connection-string=endpoint=https://<your-resource>.communication.azure.com/;accesskey=<your-access-key>
azure.communication.sms.from-phone-number=+1234567890
```

## Services and Models
- `EmailService`
  - `sendEmail(...)`, `sendEmailWithClasspathFiles(...)`, `sendEmailWithMultipartFile(...)`, `sendEmailWithMultipleFiles(...)`, `sendEmailAsync(...)`
- `MessageService`
  - `sendMessage(SmsRequest)`, `sendMessageAsync(SmsRequest)`
- Models
  - `SmsRequest { to, message, from }`
  - `SmsResponse { success, provider, messageId, error }`
  - `EmailRequest { to, subject, body, files? }`

Implementations:
- Email: `service.impl.EmailServiceImpl`
- SMS: `TwilioMessageService`, `AwsSnsMessageService`, `MessageBirdService`, `SmppMessageService`

## Async
- Enabled via `@EnableAsync` in `configuration.AsyncConfig`
- Executor bean: `communicationTaskExecutor`
- Async methods use `CompletableFuture.supplyAsync/runAsync`

- In parent project add
  ```java
  @ComponentScan(basePackages = {"com.darpan.communication"})

## Troubleshooting
- Ensure component-scan the base package is included
- Verify `*.enabled=true` flags and credentials
- Twilio initializes in `TwilioConfig` at startup; without creds it may fail if dependency is present
- SMPP requires reachable SMSC and active Camel context
