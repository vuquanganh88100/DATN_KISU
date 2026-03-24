package com.elearning.elearning_support.mapper;

import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseChapterDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourseChapter;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface OnlineCourseChapterMapper {
    @Mapping(target = "chapter.id", source = "chapterId")
    @Mapping(target = "onlineCourse.id", source = "onlineCourseId")
    OnlineCourseChapter toEntity(OnlineCourseChapterDto onlineCourseChapterDto);

//    @Mapping(target = "chapterId", source = "chapter.id")
//    @Mapping(target = "onlineCourseId", source = "onlineCourse.id")
//    @Mapping(target = "chapterWeight", source = "chapterWeight")
    OnlineCourseChapter toDto(OnlineCourse onlineCourse);
}
