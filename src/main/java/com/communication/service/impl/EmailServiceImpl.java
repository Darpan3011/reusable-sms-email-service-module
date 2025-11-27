package com.communication.service.impl;

import com.communication.exception.FileUploadSizeException;
import com.communication.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxSize;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Core reusable method for sending dynamic emails with optional attachments.
     */
    @Override
    @Async("communicationTaskExecutor")
    public void sendEmail(String to, String subject, String body, String from, String title,List<Resource> attachments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // HTML enabled

            InternetAddress internetAddress = new InternetAddress(from, title);
            helper.setFrom(internetAddress);

            if (attachments != null && !attachments.isEmpty()) {
                long maxSizeBytes = org.springframework.util.unit.DataSize.parse(maxSize).toBytes();
                for (Resource attachment : attachments) {
                    long size = 0;
                    try {
                        size = attachment.contentLength();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read attachment size", e);
                    }

                    if (size > maxSizeBytes) {
                        throw new FileUploadSizeException("File upload size exceeding for "+ attachment.getFilename() + ". Maximum size is "  + maxSizeBytes);
                    }
                    helper.addAttachment(attachment.getFilename(), attachment);
                }
            }

            mailSender.send(message);

        } catch (FileUploadSizeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    // ----------------------------
    // Helper Methods for Attachments
    // ----------------------------

    public void sendEmailWithClasspathFiles(String to, String subject, String body, String title, List<String> classpathFiles) {
        if (classpathFiles == null || classpathFiles.isEmpty()) {
            throw new IllegalArgumentException("No classpath files provided!");
        }
        List<Resource> resources = classpathFiles.stream().map(ClassPathResource::new).map(r -> (Resource) r).toList();
        sendEmailAsync(to, subject, body, null, title, resources);
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

    public void sendEmailWithMultipartFile(String to, String subject, String body, String from, String title, MultipartFile multipartFile) {
        Resource resource = null;
        if(multipartFile != null){
            resource = new ByteArrayResource(toByteArray(multipartFile)) {
                @Override
                public String getFilename() {
                    return multipartFile.getOriginalFilename();
                }
            };
        }
        sendEmailAsync(to, subject, body, from, title, resource != null ? List.of(resource) : null);
    }

    public void sendEmailWithMultipartFiles(String to, String subject, String body, String from, String title, List<MultipartFile> multipartFiles) {

        List<Resource> resources = new ArrayList<>();

        if (multipartFiles != null) {
            for (MultipartFile mf : multipartFiles) {
                if (mf != null && !mf.isEmpty()) {
                    Resource r = new ByteArrayResource(toByteArray(mf)) {
                        @Override
                        public String getFilename() {
                            return mf.getOriginalFilename();
                        }
                    };
                    resources.add(r);
                }
            }
        }

        sendEmailAsync(to, subject, body, from, title, resources.isEmpty() ? null : resources);
    }


    public void sendEmailWithMultipleFiles(String to, String subject, String body, String from, String title, List<String> files) {
        List<Resource> resources = files.stream().map(FileSystemResource::new).map(r -> (Resource) r).toList();
        sendEmailAsync(to, subject, body, from, title, resources);
    }

    @Override
    @Async("communicationTaskExecutor")
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String body, String from, String title, List<Resource> attachments) {
        return CompletableFuture.runAsync(() -> sendEmail(to, subject, body, from, title, attachments));
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


