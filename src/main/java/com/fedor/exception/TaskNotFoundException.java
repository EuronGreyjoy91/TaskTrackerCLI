package com.fedor.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
