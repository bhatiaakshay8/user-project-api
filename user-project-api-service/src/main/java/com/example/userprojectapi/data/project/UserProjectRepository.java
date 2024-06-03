package com.example.userprojectapi.data.project;

import com.example.userprojectapi.data.user.UserMapper;
import com.example.userprojectapi.model.exception.DuplicateResourceException;
import com.example.userprojectapi.model.exception.ResourceNotFoundException;
import com.example.userprojectapi.model.project.UserProject;
import com.example.userprojectapi.model.user.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class UserProjectRepository {

    private final ProjectMapper projectMapper;

    public UserProjectRepository(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public void insertProjectToUser(User user, UserProject userProject) {
        if (projectMapper.checkUserProjectExists(user.getId(), userProject)) {
            throw new DuplicateResourceException(String.format("Project %s for userId %s already exists", userProject.getName(), user.getId()));
        }
        projectMapper.insertProjectToUser(user.getId(), userProject);
    }

    public List<UserProject> getProjectsForUser(User user) {
        return projectMapper.getProjectsForUser(user.getId());
    }

    public void deleteAllProjects() {
        projectMapper.deleteProjects();
    }

    public void deleteAllProjectsForUser(Long userId) {
        projectMapper.deleteProjectsForUser(userId);
    }
}