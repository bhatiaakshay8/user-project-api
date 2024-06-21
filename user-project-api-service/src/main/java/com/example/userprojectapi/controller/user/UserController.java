package com.example.userprojectapi.controller.user;

import com.example.userprojectapi.data.project.UserProjectRepository;
import com.example.userprojectapi.data.user.UserRepository;
import com.example.userprojectapi.model.exception.NotAllowedException;
import com.example.userprojectapi.model.user.UpdateUser;
import com.example.userprojectapi.model.user.User;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api/v0/users")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;
    private final MeterRegistry meterRegistry;

    public UserController(UserRepository userRepository,
                          UserProjectRepository userProjectRepository,
                          MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;
        this.userProjectRepository = userProjectRepository;
    }

    @GetMapping("/{id}")
    @Operation(description = "Used for retrieving user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    headers = @Header(name = "Authorization", required = true,
                            description = "Bearer Token for accessing the endpoint")),
            @ApiResponse(responseCode = "403", description = "Not Authenticated"),
            @ApiResponse(responseCode = "404", description = "User Not Found")
    })
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("getting User id {}", id);

        Counter counter = Counter.builder("api_user_get")
                .tag("userId", String.valueOf(id))
                .description("a number of requests to /api/v0/users/{id} endpoint")
                .register(meterRegistry);
        counter.increment();

        User user = userRepository.getUser(id);
        user.setPassword("*****");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(description = "Used for creating new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created",
                    headers = @Header(name = "Authorization", required = true,
                            description = "Bearer Token for accessing the endpoint")),
            @ApiResponse(responseCode = "403", description = "Not Authenticated"),
            @ApiResponse(responseCode = "422", description = "User already exists")
    })
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("Adding User {}", user);
        userRepository.insertUser(user);
        log.info("User {} added", user);
        user.setPassword("*****");
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    @Operation(description = "Used for updating a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated",
                    headers = @Header(name = "Authorization", required = true,
                            description = "Bearer Token for accessing the endpoint")),
            @ApiResponse(responseCode = "403", description = "Not Authenticated"),
            @ApiResponse(responseCode = "404", description = "User Not Found")
    })
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUser updateUser) {
        log.info("Update User id {} with info: {}", id, updateUser);
        User user = userRepository.updateUser(id, updateUser);
        log.info("User id {} updated - {}", id, user);
        user.setPassword("*****");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Used for deleting a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted",
                    headers = @Header(name = "Authorization", required = true,
                            description = "Bearer Token for accessing the endpoint")),
            @ApiResponse(responseCode = "403", description = "Not Authenticated"),
            @ApiResponse(responseCode = "404", description = "User Not Found"),
            @ApiResponse(responseCode = "405", description = "User Cannot Delete Itself")
    })
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        log.info("Deleting User id {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.getUser(id);
        if (auth != null && user.getEmail().equals(auth.getPrincipal())) {
            throw new NotAllowedException("User Cannot delete itself");
        }
        //Deleting projects as there is a foreign key constraint
        //Another way is to throw an error since there are projects associated
        userProjectRepository.deleteAllProjectsForUser(id);
        userRepository.deleteUser(id);
        log.info("Deleted User id {}", id);
        return ResponseEntity.ok().build();
    }

}
