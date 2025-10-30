package com.darpan.communication.service.impl;

import com.darpan.communication.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Core reusable method for sending dynamic emails with optional attachments.
     */
    @Override
    @Async("communicationTaskExecutor")
    public void sendEmail(String to, String subject, String body, String from, List<Resource> attachments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // HTML enabled

            if (attachments != null && !attachments.isEmpty()) {
                for (Resource attachment : attachments) {
                    helper.addAttachment(attachment.getFilename(), attachment);
                }
            }

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    // ----------------------------
    // Helper Methods for Attachments
    // ----------------------------

    public void sendEmailWithClasspathFiles(String to, String subject, String body, List<String> classpathFiles) {
        if (classpathFiles == null || classpathFiles.isEmpty()) {
            throw new IllegalArgumentException("No classpath files provided!");
        }
        List<Resource> resources = classpathFiles.stream().map(ClassPathResource::new).map(r -> (Resource) r).toList();
        sendEmailAsync(to, subject, body, null, resources);
    }
//
//    public void sendEmailWithInputStream(String to, String subject, String body, ByteArrayInputStream stream, String fileName) {
//        Resource resource = new ByteArrayResource(toByteArray(stream)) {
//            @Override
//            public String getFilename() {
//                return fileName;
//            }
//        };
//        sendEmailAsync(to, subject, body, null, List.of(resource));
//    }

    public void sendEmailWithMultipartFile(String to, String subject, String body, MultipartFile multipartFile) {
        Resource resource = new ByteArrayResource(toByteArray(multipartFile)) {
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        sendEmailAsync(to, subject, body, null, List.of(resource));
    }

    public void sendEmailWithMultipleFiles(String to, String subject, String body, List<String> files) {
        List<Resource> resources = files.stream().map(FileSystemResource::new).map(r -> (Resource) r).toList();
        sendEmailAsync(to, subject, body, null, resources);
    }

    @Override
    @Async("communicationTaskExecutor")
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String body, String from, List<Resource> attachments) {
        return CompletableFuture.runAsync(() -> sendEmail(to, subject, body, from, attachments));
    }

    // ----------------------------
    // Utility Converters
    // ----------------------------
    private byte[] toByteArray(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read MultipartFile: " + e.getMessage(), e);
        }
    }
//
//    private byte[] toByteArray(InputStream inputStream) {
//        try {
//            return inputStream.readAllBytes();
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read InputStream: " + e.getMessage(), e);
//        }
//    }
}


