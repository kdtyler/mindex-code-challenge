package com.mindex.challenge.exceptionhandling;

public class CompensationNotFoundException extends RuntimeException {
    public CompensationNotFoundException(String message) {
      super(message);
    }
}
