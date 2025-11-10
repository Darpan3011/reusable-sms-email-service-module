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

## Auto-configuration
This module provides:
- `com.darpan.communication.configuration.CommunicationAutoConfiguration`
- `com.darpan.communication.configuration.email.MailAutoConfiguration`
- `com.darpan.communication.configuration.message.MessagingAutoConfiguration`

Ensure the imports file lists them one-per-line:
`src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

If not using auto-config, include `com.darpan.communication` in your component scan.

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

### SMPP (Apache Camel)
```properties
messaging.smpp.enabled=true
smpp.default-config.max-try=3
smpp.default-config.rebind-time=5000
smpp.default-config.system-type=SMPP
smpp.connections.main-smsc.credentials.host=smpp-provider.com
smpp.connections.main-smsc.credentials.port=2775
smpp.connections.main-smsc.credentials.username=your-username
smpp.connections.main-smsc.credentials.password=your-password
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
- SMS: `service.impl.TwilioMessageService`, `AwsSnsMessageService`, `MessageBirdService`, `SmppMessageService`

## Async
- Enabled via `@EnableAsync` in `configuration.AsyncConfig`
- Executor bean: `communicationTaskExecutor`
- Async methods use `CompletableFuture.supplyAsync/runAsync`

## Dependencies (highlights)
- Spring Boot: web, mail
- Twilio SDK 11.0.1
- AWS SDK v2 (SNS + Apache HTTP client)
- MessageBird API 3.2.0
- Apache Camel (spring-boot, smpp, bean)
- Lombok
- iText 5.5.13.3 (present but not used in services)
- In parent project add
  ```java
  @ComponentScan(basePackages = {"com.darpan.communication"})

## Troubleshooting
- Ensure AutoConfiguration imports file is populated or component-scan base package is included
- Verify `*.enabled=true` flags and credentials
- Twilio initializes in `TwilioConfig` at startup; without creds it may fail if dependency is present
- SMPP requires reachable SMSC and active Camel context

## Known caveats
- `EmailServiceImpl.sendEmail(...)` does not set a `from` address; mail server default is used
- `MessageBirdService` reads kebab-case props via `@Value` (`api-key`, `sender-id`); use those names
- `META-INF/spring/...AutoConfiguration.imports` exists but may be empty; populate with the three classes above
