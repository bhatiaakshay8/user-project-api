package com.example.userprojectapi.controller.project;

import com.example.userprojectapi.data.project.UserProjectRepository;
import com.example.userprojectapi.data.user.UserRepository;
import com.example.userprojectapi.model.project.UserProject;
import com.example.userprojectapi.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/v0/users/{userId}/projects")
@SecurityRequirement(name = "Bearer Authentication")
public class UserProjectController {

    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

    public UserProjectController(UserRepository userRepository,
                                 UserProjectRepository userProjectRepository) {
        this.userRepository = userRepository;
        this.userProjectRepository = userProjectRepository;
    }

    @GetMapping("")
    @Operation(description = "Used for retrieving user projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    headers = @Header(name = "Authorization", required = true,
                            description = "Bearer Token for accessing the endpoint")),
            @ApiResponse(responseCode = "204", description = "No Projects"),
            @ApiResponse(responseCode = "404", description = "User Not Found")
    })
    public ResponseEntity<List<UserProject>> getProjectsForUser(@PathVariable Long userId) {
        log.info("Getting Projects for User id {}", userId);
        User user = userRepository.getUser(userId);
        List<UserProject> userProjects = userProjectRepository.getProjectsForUser(user);
        log.info("User {} has {} projects", userId, userProjects.size());
        if (userProjects.isEmpty()) {
            return new ResponseEntity<>(userProjects, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userProjects, HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(description = "Used for adding a project to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added",
                    headers = @Header(name = "Authorization", required = true,
                            description = "Bearer Token for accessing the endpoint")),
            @ApiResponse(responseCode = "422", description = "Project already exists for user")
    })
    public ResponseEntity<UserProject> addProjectToUser(@PathVariable Long userId, @Valid @RequestBody UserProject userProject) {
        log.info("Adding Project {} to User {}", userProject.getName(), userId);
        User user = userRepository.getUser(userId);
        userProject.setUserId(userId);
        userProjectRepository.insertProjectToUser(user, userProject);
        log.info("Project {} added to User {}", userProject.getName(), userId);
        return new ResponseEntity<>(userProject, HttpStatus.CREATED);
    }

}
