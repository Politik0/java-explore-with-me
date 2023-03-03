package ru.practicum.ewmservice.exception;

public class DataExistException extends RuntimeException {
    public DataExistException(String message) {
        super(message);
    }
}
