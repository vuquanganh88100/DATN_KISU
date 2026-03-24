package com.elearning.elearning_support.services.onlineCourse;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.onlineCourse.ChapterCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseChapterDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourseChapter;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public interface OnlineCourseChapterService {

    void add(OnlineCourseChapterDto onlineCourseChapterDto);
    public List<ChapterCourseDetailDto> getChapterCourseDetail(long courseId);
}
