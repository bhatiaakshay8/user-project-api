package com.example.userprojectapi.model.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginRes {
    @Schema(type = "string", example = "admin@example.com")
    private String email;
    @Schema(type = "string")
    private String token;
}
