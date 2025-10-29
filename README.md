# Communication Module

A reusable Spring Boot starter library that provides email and SMS communication services with support for multiple providers.

## 🚀 Features

### 📧 Email Services
- **SMTP-based email sending** with Spring Mail
- **Multiple attachment support**:
  - Classpath resources
  - MultipartFile attachments
  - File system attachments
- **HTML email support**
- **Async processing** with `CompletableFuture`
- **Optimized connection pooling**

### 📱 SMS Services
Support for **4 different SMS providers**:

1. **Twilio** - Cloud communications platform
2. **AWS SNS** - Amazon Simple Notification Service
3. **MessageBird** - Global communications platform
4. **SMPP** - Short Message Peer-to-Peer protocol (via Apache Camel)

### ⚡ Performance Features
- **Async operations** for all services
- **Connection pooling** for external services
- **Memory-efficient streaming** for large attachments
- **Optimized SMTP configuration**

## 📦 Installation

Add this module as a dependency in your Spring Boot project:

```xml
<dependency>
    <groupId>com.darpan</groupId>
    <artifactId>communication</artifactId>
    <version>1.0.1</version>
</dependency>
```

## ⚙️ Configuration

### Email Configuration

Add the following properties to your `application.properties`:

```properties
# SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### SMS Provider Configuration

Configure one or more SMS providers based on your needs:

#### Twilio
```properties
# Enable Twilio SMS
messaging.twilio.enabled=true
messaging.twilio.sid=your-account-sid
messaging.twilio.token=your-auth-token
messaging.twilio.from=+1234567890
```

#### AWS SNS
```properties
# Enable AWS SNS SMS
messaging.sns.enabled=true
messaging.sns.access-key=your-access-key
messaging.sns.secret-key=your-secret-key
messaging.sns.region=us-east-1
```

#### MessageBird
```properties
# Enable MessageBird SMS
messaging.messagebird.enabled=true
messaging.messagebird.api-key=your-api-key
messaging.messagebird.sender-id=YourSenderID
```

#### SMPP
```properties
# Enable SMPP SMS
messaging.smpp.enabled=true
smpp.default-config.max-try=3
smpp.default-config.rebind-time=5000
smpp.default-config.system-type=SMPP
smpp.connections.main-smsc.credentials.host=smpp-provider.com
smpp.connections.main-smsc.credentials.port=2775
smpp.connections.main-smsc.credentials.username=your-username
smpp.connections.main-smsc.credentials.password=your-password
```

## 📖 Usage

### Email Service

```java
@Service
public class MyService {

    private final EmailService emailService;

    public MyService(EmailService emailService) {
        this.emailService = emailService;
    }

    // Send simple email
    public void sendSimpleEmail() {
        emailService.sendEmailAsync(
            "recipient@example.com",
            "Test Subject",
            "<h1>Hello World</h1>",
            "sender@example.com",
            null
        );
    }

    // Send email with attachments
    public void sendEmailWithAttachments() {
        List<Resource> attachments = List.of(
            new FileSystemResource("path/to/file.pdf"),
            new ClassPathResource("static/logo.png")
        );

        emailService.sendEmailAsync(
            "recipient@example.com",
            "Email with Attachments",
            "Please find the attached files.",
            "sender@example.com",
            attachments
        );
    }

    // Send email with MultipartFile
    public void sendEmailWithMultipartFile(MultipartFile file) {
        emailService.sendEmailWithMultipartFile(
            "recipient@example.com",
            "File Upload",
            "Here is your file",
            file
        );
    }
}
```

### SMS Service

```java
@Service
public class MySmsService {

    private final MessageService messageService;

    public MySmsService(MessageService messageService) {
        this.messageService = messageService;
    }

    // Send SMS (synchronous)
    public SmsResponse sendSms() {
        SmsRequest request = SmsRequest.builder()
            .to("+1234567890")
            .message("Hello from Communication Module!")
            .from("+0987654321")
            .build();

        return messageService.sendMessage(request);
    }

    // Send SMS (asynchronous)
    public CompletableFuture<SmsResponse> sendSmsAsync() {
        SmsRequest request = SmsRequest.builder()
            .to("+1234567890")
            .message("Async SMS message")
            .from("+0987654321")
            .build();

        return messageService.sendMessageAsync(request);
    }
}
```

### Using Multiple SMS Providers

The module automatically uses the enabled providers. You can inject specific implementations if needed:

```java
@Service
public class SmsService {

    private final TwilioMessageService twilioService;
    private final AwsSnsMessageService snsService;

    public SmsService(
        @Autowired(required = false) TwilioMessageService twilioService,
        @Autowired(required = false) AwsSnsMessageService snsService) {
        this.twilioService = twilioService;
        this.snsService = snsService;
    }

    public void sendViaTwilio(SmsRequest request) {
        if (twilioService != null) {
            twilioService.sendMessage(request);
        }
    }

    public void sendViaSns(SmsRequest request) {
        if (snsService != null) {
            snsService.sendMessage(request);
        }
    }
}
```

## 📊 Models

### SmsRequest
```java
SmsRequest request = SmsRequest.builder()
    .to("+1234567890")           // Recipient phone number
    .message("Your SMS content") // SMS content
    .from("+0987654321")         // Sender phone number (optional for some providers)
    .build();
```

### SmsResponse
```java
// Response contains:
// - success: boolean
// - provider: String (TWILIO, AWS_SNS, MESSAGEBIRD, SMPP)
// - messageId: String (provider-specific message ID)
// - error: String (error message if failed)
```

### EmailRequest
```java
EmailRequest request = new EmailRequest(
    "recipient@example.com",  // to
    "Subject",               // subject
    "Email body",            // body
    List.of("file1.pdf", "file2.jpg") // optional file paths
);
```

## ⚡ Performance Considerations

### Memory Management
- Large attachments are handled efficiently with streaming
- Connection pooling reduces memory overhead
- Async operations prevent thread blocking

### Connection Pooling
- **SMTP**: Configured with optimal timeouts and connection limits
- **AWS SNS**: Uses Apache HTTP client with connection pooling
- **Twilio**: Optimized HTTP client configuration

### Async Operations
- All email and SMS operations support async execution
- Uses `CompletableFuture` for non-blocking operations
- Configurable thread pools for optimal performance

## 🔧 Advanced Configuration

### Custom Thread Pool
```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "communicationTaskExecutor")
    public TaskExecutor communicationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("comm-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

### Custom SMTP Configuration
```java
@Configuration
public class CustomMailConfig {

    @Bean
    public JavaMailSender customMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Custom configuration
        return mailSender;
    }
}
```

## 🛠️ Dependencies

The module includes the following key dependencies:

- **Spring Boot Starter Web** - Web framework
- **Spring Boot Starter Mail** - Email functionality
- **Twilio SDK** - Twilio SMS integration
- **AWS SDK v2** - AWS SNS integration
- **MessageBird API** - MessageBird SMS integration
- **Apache Camel** - SMPP protocol support
- **Lombok** - Boilerplate code reduction

## 📝 Error Handling

This module focuses on core communication functionality. Error handling and retry mechanisms should be implemented in the parent application using:

- Circuit breakers (Resilience4j)
- Retry patterns
- Custom exception handling
- Monitoring and alerting

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For issues and questions:
1. Check the configuration examples above
2. Verify provider credentials and network connectivity
3. Review application logs for detailed error messages
4. Ensure proper Spring Boot auto-configuration is enabled

## 📈 Version History

- **v1.0.1** - Current version with async support and performance optimizations
- **v1.0.0** - Initial release with basic email and SMS functionality

---

**Note**: This module is designed as a reusable library. Error handling, retry mechanisms, and business logic should be implemented in the parent application that consumes this module.
