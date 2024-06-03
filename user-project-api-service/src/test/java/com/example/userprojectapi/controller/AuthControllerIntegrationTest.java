package com.example.userprojectapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.userprojectapi.JsonUtil;
import com.example.userprojectapi.UserProjectApiApplication;
import com.example.userprojectapi.data.user.UserRepository;
import com.example.userprojectapi.model.login.LoginReq;
import com.example.userprojectapi.model.login.LoginRes;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UserProjectApiApplication.class)
@AutoConfigureMockMvc()
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@AutoConfigureTestDatabase
@ActiveProfiles("development")
public class AuthControllerIntegrationTest {

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
    public void whenValidInputButNoValidToken_thenThrow403() throws Exception {
        User bob = new User();
        bob.setEmail("admin1@example.com");
        bob.setPassword("12345");
        bob.setName("Admin1");
        mvc.perform(post("/api/v0/users").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(bob)))
                .andExpect(status().is(403));

        try {
            repository.findUserByEmail(bob.getEmail());
        } catch (Exception exception) {
            assertThat(exception).isInstanceOf(UsernameNotFoundException.class);
        }
    }

    @Test
    public void whenGivenValidEmailAndPasswordThenLoginAndReturnValidToken() throws Exception {
        LoginReq loginReq = new LoginReq();
        loginReq.setEmail("admin@example.com");
        loginReq.setPassword("password123");

        MvcResult mvcResult = mvc.perform(post("/api/v0/auth/login").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(loginReq.getEmail())))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();

        LoginRes loginRes = JsonUtil.toDTO(mvcResult.getResponse().getContentAsString(), LoginRes.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + loginRes.getToken());

        User user = createTestUser("admin1@example.com", "password123", "admin1");
        mvc.perform(get("/api/v0/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.password", is("*****")));
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