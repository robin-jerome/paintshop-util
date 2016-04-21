package com.paintshop.file;

public class InvalidInputDataException extends RuntimeException {

    public InvalidInputDataException(String message) {
        super(message);
    }

    public InvalidInputDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
