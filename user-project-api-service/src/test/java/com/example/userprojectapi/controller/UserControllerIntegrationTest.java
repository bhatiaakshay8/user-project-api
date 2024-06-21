package com.example.userprojectapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.userprojectapi.JsonUtil;
import com.example.userprojectapi.UserProjectApiApplication;
import com.example.userprojectapi.data.user.UserRepository;
import com.example.userprojectapi.model.exception.ResourceNotFoundException;
import com.example.userprojectapi.model.user.UpdateUser;
import com.example.userprojectapi.model.user.User;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UserProjectApiApplication.class)
@AutoConfigureMockMvc()
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@AutoConfigureTestDatabase
@ActiveProfiles("testing")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository repository;

    @After
    public void resetDb() {
        repository.deleteAllUsers();
        createTestUser("admin@example.com", "password123", "admin"); //Initial existing user
    }

    @Test
    public void whenValidInput_thenCreateUser() throws Exception {
        User bob = new User();
        bob.setEmail("admin1@example.com");
        bob.setPassword("12345");
        bob.setName("Admin1");
        mvc.perform(post("/api/v0/users").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(bob)))
                .andExpect(status().isCreated());

        User found = repository.findUserByEmail(bob.getEmail());
        assertThat(found)
                .isNotNull()
                .matches(user -> user.getName().equals(bob.getName()) && user.getPassword().equals(bob.getPassword()));
    }

    @Test
    public void whenUserEmailExists_thenStatus422() throws Exception {
        User bob = new User();
        bob.setEmail("admin@example.com");
        bob.setPassword("password123");
        bob.setName("admin");

        User existingUser = repository.findUserByEmail(bob.getEmail());
        assertThat(existingUser)
                .isNotNull()
                .matches(user -> user.getName().equals(bob.getName()) && user.getPassword().equals(bob.getPassword()));

        mvc.perform(post("/api/v0/users").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(bob)))
                .andDo(print())
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
                .andExpect(jsonPath("$.error", is("User email admin@example.com already exists")));
    }

    @Test
    public void givenUser_whenGetUser_thenStatus200() throws Exception {
        User user = createTestUser("admin1@example.com", "password123", "admin1");
        mvc.perform(get("/api/v0/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.password", is("*****")));
    }

    @Test
    public void whenGetUserNotExistingUser_thenStatus404() throws Exception {
        mvc.perform(get("/api/v0/users/" + 100).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.error", is("User id 100 not found")));
    }

    @Test
    public void givenUser_whenUpdateUser_thenStatus200() throws Exception {
        User user = createTestUser("admin1@example.com", "password123", "admin1");
        UpdateUser updateUser = new UpdateUser("password1234", "admin12");

        mvc.perform(post("/api/v0/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(updateUser)))
                .andDo(print())
                .andExpect(status().isOk());

        User found = repository.findUserByEmail(user.getEmail());
        assertThat(found)
                .isNotNull()
                .matches(u -> u.getName().equals(updateUser.getName()) && u.getPassword().equals(updateUser.getPassword()));

    }

    @Test
    public void givenUser_whenUpdateUserWithOnlyNameProvided_thenUpdateJustNameWithStatus200() throws Exception {
        User user = createTestUser("admin1@example.com", "password123", "admin1");
        UpdateUser updateUser = new UpdateUser(null, "admin12");

        mvc.perform(post("/api/v0/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(updateUser)))
                .andDo(print())
                .andExpect(status().isOk());

        User found = repository.findUserByEmail(user.getEmail());
        assertThat(found)
                .isNotNull()
                .matches(u -> u.getName().equals(updateUser.getName()) && u.getPassword().equals(user.getPassword()));

    }

    @Test
    public void givenUser_whenUpdateUserWithOnlyPasswordProvided_thenUpdateJustPasswordWithStatus200() throws Exception {
        User user = createTestUser("admin1@example.com", "password123", "admin1");
        UpdateUser updateUser = new UpdateUser("password1234", null);

        mvc.perform(post("/api/v0/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(updateUser)))
                .andDo(print())
                .andExpect(status().isOk());

        User found = repository.findUserByEmail(user.getEmail());
        assertThat(found)
                .isNotNull()
                .matches(u -> u.getName().equals(user.getName()) && u.getPassword().equals(updateUser.getPassword()));

    }

    @Test
    public void givenUser_whenDeleteUser_thenStatus200() throws Exception {
        User user = createTestUser("admin1@example.com", "password123", "admin1");

        mvc.perform(delete("/api/v0/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        try {
            repository.findUserByEmail(user.getEmail());
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UsernameNotFoundException.class);
        }

        try {
            repository.getUser(user.getId());
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResourceNotFoundException.class);
        }
    }

    private User createTestUser(String email, String password, String name) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        repository.insertUser(user);
        return user;
    }

}