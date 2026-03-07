package com.habbashx.ecommerce.exception.handler;

import com.habbashx.ecommerce.exception.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserNotFound(@NotNull final UserNotFoundException exception) {

        return ResponseEntity.ok(ErrorResponse.builder()
                        .status(BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .time(now())
                .build());
    }

}
