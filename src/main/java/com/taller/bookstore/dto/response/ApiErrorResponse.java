package com.taller.bookstore.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApiErrorResponse {

    private String status;
    private int code;
    private String message;
    private List<String> errors;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;

    public static ApiErrorResponse of(int code, String message, List<String> errors, String path) {
        return ApiErrorResponse.builder()
                .status("error")
                .code(code)
                .message(message)
                .errors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiErrorResponse of(int code, String message, String error, String path) {
        return of(code, message, List.of(error), path);
    }
}
