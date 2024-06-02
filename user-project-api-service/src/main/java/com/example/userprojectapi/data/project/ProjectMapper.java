package com.example.userprojectapi.data.project;

import com.example.userprojectapi.model.project.UserProject;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Mapper
public interface ProjectMapper {

    @Options(useGeneratedKeys = true, keyProperty = "up.id")
    @Insert("INSERT into tb_user_external_project " +
            "VALUES(#{userId}, #{up.name})")
    void insertProjectToUser(Long userId, @Param("up") UserProject userProject);

    @Select("SELECT COALESCE((SELECT 1 FROM tb_user_external_project " +
            "WHERE userId=#{userId} and name=#{up.name}), 0)")
    boolean checkUserProjectExists(Long userId, @Param("up") UserProject userProject);

    @Select("SELECT id, userId, name FROM tb_user_external_project " +
            "WHERE userId=#{userId}")
    List<UserProject> getProjectsForUser(Long userId);

}
