package com.example.userprojectapi.model.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginReq {
    @Schema(type = "string", example = "admin@example.com")
    @NotBlank(message = "Email must not be blank")
    private String email;
    @Schema(type = "string", example = "password123")
    @NotBlank(message = "Password must not be blank")
    private String password;
}
