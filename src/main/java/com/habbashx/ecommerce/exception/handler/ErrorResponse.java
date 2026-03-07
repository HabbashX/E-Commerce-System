package com.habbashx.ecommerce.exception.handler;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime time;
}
