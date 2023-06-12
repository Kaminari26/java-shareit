package ru.practicum.shareit.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class ErrorResponse {
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
