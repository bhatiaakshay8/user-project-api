package com.example.userprojectapi.model.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProject {

    @Schema(hidden = true)
    private Long id;
    @Schema(hidden = true)
    private Long userId;
    @Schema(type = "string", example = "Example Project")
    @NotBlank(message = "Project Name must not be blank")
    @Size(max = 120, message = "Name size must be max 120")
    private String name;
}
