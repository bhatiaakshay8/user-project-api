package com.example.userprojectapi.model.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Data
public class ErrorRes {

    private HttpStatus httpStatus;
    private String message;
}
