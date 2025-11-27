package com.communication.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EmailService {

    void sendEmail(String to, String subject, String body, String from, String title, List<Resource> attachments);

    void sendEmailWithClasspathFiles(String to, String subject, String body, String title, List<String> classpathFile);

//    void sendEmailWithInputStream(String to, String subject, String body, ByteArrayInputStream stream, String fileName);

    void sendEmailWithMultipartFile(String to, String subject, String body, String from, String title, MultipartFile multipartFile);

    void sendEmailWithMultipleFiles(String to, String subject, String body, String from, String title, List<String> files);

    CompletableFuture<Void> sendEmailAsync(String to, String subject, String body, String from, String title, List<Resource> attachments);

    void sendEmailWithMultipartFiles(String to, String subject, String body, String from, String title, List<MultipartFile> multipartFiles);
}

