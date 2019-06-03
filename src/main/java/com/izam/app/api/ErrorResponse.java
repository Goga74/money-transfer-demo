package com.izam.app.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ErrorResponse {
    int code;
    String message;
}
