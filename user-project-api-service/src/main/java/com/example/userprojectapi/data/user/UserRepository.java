package com.example.userprojectapi.data.user;

import com.example.userprojectapi.model.user.UpdateUser;
import com.example.userprojectapi.model.user.User;
import com.example.userprojectapi.model.exception.DuplicateResourceException;
import com.example.userprojectapi.model.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class UserRepository {

    private final UserMapper userMapper;

    public UserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User findUserByEmail(String email) {
        User user = userMapper.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User email %s not found", email));
        }
        user.setRoles(Collections.singletonList("ADMIN"));
        return user;
    }

    public void insertUser(User user) {
        String email = user.getEmail();
        if (userMapper.checkUserExists(email)) {
            throw new DuplicateResourceException(String.format("User email %s already exists", email));
        }
        userMapper.insertUser(user);
    }

    public User updateUser(Long id, UpdateUser updateUser) {
        User user = getUser(id);
        userMapper.updateUser(id, updateUser);
        user.setName(updateUser.getName());
        user.setPassword(updateUser.getPassword());
        return user;
    }

    public User getUser(Long id) {
        User user = userMapper.getUser(id);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User id %s not found", id));
        }
        return user;
    }

    public void deleteUser(Long id) {
        userMapper.deleteUser(id);
    }

    //Visible for testing
    public void deleteAllUsers() {
        userMapper.deleteAllUsers();
    }
}