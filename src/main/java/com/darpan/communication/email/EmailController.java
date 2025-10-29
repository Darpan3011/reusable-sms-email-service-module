package com.darpan.communication.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // Simple email (no attachments)
    @PostMapping("/simple")
    public String sendSimpleEmail(@RequestBody EmailRequest request) {
        emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody(), "dummmy3011@gmail.com", null);
        return "Email sent successfully (no attachments)";
    }

    // Email with multiple files (file paths from local system)
    @PostMapping("/file-path")
    public String sendEmailWithFilePaths(@RequestBody EmailRequest request) {
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            return "No file paths provided!";
        }
        emailService.sendEmailWithMultipleFiles(request.getTo(), request.getSubject(), request.getBody(), request.getFiles());
        return "Email sent successfully with file(s) from file system";
    }

    // Email with classpath file(s)
    @PostMapping("/classpath")
    public String sendEmailWithClasspathFiles(@RequestBody EmailRequest request) {
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            return "No classpath files provided!";
        }
        emailService.sendEmailWithClasspathFiles(request.getTo(), request.getSubject(), request.getBody(), request.getFiles());
        return "Email sent successfully with classpath file(s)";
    }

    // Email with uploaded multipart file
    @PostMapping(value = "/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String sendEmailWithMultipart(@RequestPart("to") String to, @RequestPart("subject") String subject, @RequestPart("body") String body, @RequestPart("file") MultipartFile file) {
        emailService.sendEmailWithMultipartFile(to, subject, body, file);
        return "Email sent successfully with uploaded file";
    }

    // Email with InputStream (useful for generated files like PDFs)
    @PostMapping("/inputstream")
    public String sendEmailWithInputStream(@RequestBody EmailRequest request, @RequestParam String fileName) {
        String exampleContent = "Generated file content for: " + request.getSubject();
        ByteArrayInputStream stream = new ByteArrayInputStream(exampleContent.getBytes(StandardCharsets.UTF_8));

        emailService.sendEmailWithInputStream(request.getTo(), request.getSubject(), request.getBody(), stream, fileName);
        return "Email sent successfully with InputStream attachment";
    }

    @PostMapping("/inputstream/pdf")
    public String sendEmailWithInputStream2(@RequestBody EmailRequest request,
                                            @RequestParam String fileName) {
        try {
            // Create PDF as ByteArrayOutputStream
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, pdfOutputStream);

            document.open();
            document.add(new com.itextpdf.text.Paragraph("Subject: " + request.getSubject()));
            document.add(new com.itextpdf.text.Paragraph("Body: " + request.getBody()));
            document.add(new com.itextpdf.text.Paragraph("Generated automatically by the system."));
            document.close();


            // Convert to input stream
            ByteArrayInputStream stream = new ByteArrayInputStream(pdfOutputStream.toByteArray());

            // Send email with PDF attachment
            emailService.sendEmailWithInputStream(request.getTo(), request.getSubject(), request.getBody(), stream, fileName.endsWith(".pdf") ? fileName : fileName + ".pdf");

            return "Email sent successfully with PDF attachment";
        } catch (Exception e) {
            throw new RuntimeException("Failed to send PDF email: " + e.getMessage(), e);
        }
    }


}