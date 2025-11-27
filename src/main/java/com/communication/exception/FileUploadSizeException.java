package com.communication.exception;

public class FileUploadSizeException extends RuntimeException {
    public FileUploadSizeException(String message) {
        super(message);
    }
}
