package com.communication.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EmailService {

    void sendEmail(String to, String subject, String body, String from, List<Resource> attachments);

    void sendEmailWithClasspathFiles(String to, String subject, String body, List<String> classpathFile);

//    void sendEmailWithInputStream(String to, String subject, String body, ByteArrayInputStream stream, String fileName);

    void sendEmailWithMultipartFile(String to, String subject, String body, MultipartFile multipartFile);

    void sendEmailWithMultipleFiles(String to, String subject, String body, List<String> files);

    CompletableFuture<Void> sendEmailAsync(String to, String subject, String body, String from, List<Resource> attachments);
}

