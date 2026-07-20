package com.jk1.exception;
public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(String message) { super(message); }
}
