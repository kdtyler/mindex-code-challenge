package com.mindex.challenge.exceptionhandling;

public class CompensationAlreadyExistsException extends RuntimeException {
    public CompensationAlreadyExistsException(String message) {
        super(message);
    }
}
