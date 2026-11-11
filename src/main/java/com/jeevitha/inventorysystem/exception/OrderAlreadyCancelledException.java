package com.jeevitha.inventorysystem.exception;

public class OrderAlreadyCancelledException extends RuntimeException{
    public OrderAlreadyCancelledException(String message) {
        super(message);
    }
}
