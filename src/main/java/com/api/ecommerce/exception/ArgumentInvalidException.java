package com.api.ecommerce.exception;

public class ArgumentInvalidException extends RuntimeException {
    public ArgumentInvalidException(String message) {
        super(message);
    }
}