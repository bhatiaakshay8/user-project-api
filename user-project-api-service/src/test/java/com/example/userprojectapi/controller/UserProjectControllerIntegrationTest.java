package com.example.userprojectapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.userprojectapi.JsonUtil;
import com.example.userprojectapi.UserProjectApiApplication;
import com.example.userprojectapi.data.project.UserProjectRepository;
import com.example.userprojectapi.data.user.UserRepository;
import com.example.userprojectapi.model.project.UserProject;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UserProjectApiApplication.class)
@AutoConfigureMockMvc()
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@AutoConfigureTestDatabase
@ActiveProfiles("testing")
public class UserProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProjectRepository userProjectRepository;

    @After
    public void resetDb() {
        userProjectRepository.deleteAllProjects();
        userRepository.deleteAllUsers();
        createTestUser("admin@example.com", "password123", "admin"); //Initial existing user
    }

    @Test
    public void whenValidInputAndGivenUser_thenAddProjectToUser() throws IOException, Exception {
        User bob = createTestUser("admintest@example.com", "password123", "admintest");

        UserProject userProject = new UserProject();
        userProject.setName("Example Project");

        mvc.perform(post("/api/v0/users/" + bob.getId() + "/projects").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(userProject)))
                .andExpect(status().isCreated());

        List<UserProject> found = userProjectRepository.getProjectsForUser(bob);
        assertThat(found)
                .isNotNull()
                .singleElement()
                .matches(project -> project.getName().equals(userProject.getName()));
    }

    @Test
    public void whenValidInput_AndUserDoesnExist_thenThrow404() throws Exception {
        UserProject userProject = new UserProject();
        userProject.setName("Example Project");

        mvc.perform(post("/api/v0/users/100/projects").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(userProject)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.error", is("User id 100 not found")));
    }

    @Test
    public void givenUserAndProjects_whenGetUserProjects_thenStatus200() throws Exception {
        User user = createTestUser("admin1@example.com", "password123", "admin1");
        addProjectToUser(user, "Example Project");

        mvc.perform(get("/api/v0/users/" + user.getId() + "/projects").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Example Project")))
                .andExpect(jsonPath("$[0].userId", is(user.getId().intValue())));
    }

    private User createTestUser(String email, String password, String name) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        userRepository.insertUser(user);
        return user;
    }

    private void addProjectToUser(User user, String projectName) {
        UserProject userProject = new UserProject();
        userProject.setName(projectName);
        userProjectRepository.insertProjectToUser(user, userProject);
    }

}