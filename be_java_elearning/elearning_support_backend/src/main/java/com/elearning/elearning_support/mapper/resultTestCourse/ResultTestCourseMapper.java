package com.elearning.elearning_support.mapper.resultTestCourse;

import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDto;
import com.elearning.elearning_support.entities.resultTestCourse.ResultTestCourseEntity;
import com.elearning.elearning_support.repositories.testCourse.TestCourseRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class ResultTestCourseMapper {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestCourseRepository testCourseRepository;
    public ResultTestCourseEntity toEntity(ResultTestCourseDto dto){
        ResultTestCourseEntity entity=new ResultTestCourseEntity();
        entity.setTestCourseEntity(testCourseRepository.findById(dto.getTestCourseId()).get());
        entity.setUser(userRepository.findById(AuthUtils.getCurrentUserId()).get());
//        entity.setResultPoint(10);
        entity.setSubmitTime(new Date());
        entity.setTotalTimeToDo(dto.getTotalTimeTodo());
        return entity;
    }
    public ResultTestCourseDto toDto(ResultTestCourseEntity entity){
        ResultTestCourseDto dto=new ResultTestCourseDto();
        dto.setTestCourseId(entity.getTestCourseEntity().getId());
        dto.setUserId(entity.getUser().getId());
        dto.setSubmitTime(entity.getSubmitTime());
        return dto;
    }
}
