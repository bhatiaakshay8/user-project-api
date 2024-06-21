package com.example.userprojectapi.data.user;

import com.example.userprojectapi.model.user.UpdateUser;
import com.example.userprojectapi.model.user.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Mapper
public interface UserMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT into tb_user(email, password, name) " +
            "VALUES(#{user.email}, #{user.password}, #{user.name})")
    void insertUser(@Param("user") User user);

    @Update("UPDATE tb_user " +
            "SET password=#{user.password}, " +
            "name=#{user.name} " +
            "WHERE id=#{id}")
    void updateUser(Long id, @Param("user") UpdateUser updateUser);

    @Select("SELECT COALESCE((SELECT 1 FROM tb_user WHERE email=#{email}), 0)")
    boolean checkUserExists(String email);

    @Select("SELECT id, email, password, name FROM tb_user where email=#{email}")
    User getUserByEmail(String email);

    @Select("SELECT id, email, password, name FROM tb_user where id=#{id}")
    User getUser(Long id);

    @Delete("Delete from tb_user where id=#{id}")
    void deleteUser(Long id);

    @Delete("Delete from tb_user")
    void deleteAllUsers();
}
