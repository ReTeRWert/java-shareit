package ru.practicum.shareit.exeption;

public class UserVerificationException extends RuntimeException {
    public UserVerificationException(String message) {
        super(message);
    }
}
