package com.elearning.elearning_support.mapper;

import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.entities.onlineCourse.UserOnlineCourse;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.apache.catalina.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring", imports = AuthUtils.class)
public interface OnlineCourseMapper {
    @Mapping(target = "subject.id", source = "subjectId")
    @Mapping(target = "createdBy", expression = "java(AuthUtils.getCurrentUserId())")
    @Mapping(target = "courseUrlImg", source = "courseUrlImg")  // Sử dụng URL ảnh đã upload
    @Mapping(target = "publish",source = "publish")
    OnlineCourse toEntity(OnlineCourseDto onlineCourseDto);

    @Mapping(target = "subjectId", source = "subject.id")
    OnlineCourseDto toDto(OnlineCourse onlineCourse);

}
