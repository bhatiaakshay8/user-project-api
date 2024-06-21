package com.example.userprojectapi.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUser {
    @Schema(type = "string", example = "password123")
    @Nullable
    @Size(min = 1, max = 15, message = "Password size must be b/w 1-15")
    @ToString.Exclude
    private String password;
    @Schema(type = "string", example = "admin")
    @Size(max = 120, message = "Name size must be max 120")
    private String name;
}
