package com.elearning.elearning_support.services.onlineCourse;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.onlineCourse.ChapterCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseChapterDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
public interface OnlineCourseService {
     void addOnlineCourse(OnlineCourseDto onlineCourseDto);
     void configureChapterWeight(OnlineCourseChapterDto onlineCourseChapterDto);
    Page<OnlineCourseDto> getListCourse(Pageable pageable) ;
    OnlineCourseDto getCourseById(Long courseId,Pageable pageable);
    void updateOnlineCourse(Long onlineCourseId,OnlineCourseDto onlineCourseDto);
    void updatePublish(long onlineCourseId,boolean isPublish);
//     List<ChapterCourseDetailDto> getChapterCourseDetail(long courseId);
}
