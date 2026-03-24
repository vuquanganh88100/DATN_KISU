package com.elearning.elearning_support.services.onlineCourse.impl;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.onlineCourse.ChapterCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseChapterDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.entities.lecture.Lecture;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourseChapter;
import com.elearning.elearning_support.mapper.OnlineCourseChapterMapper;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseChapterRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseRepository;
import com.elearning.elearning_support.services.lecture.LectureService;
import com.elearning.elearning_support.services.onlineCourse.OnlineCourseChapterService;
import com.elearning.elearning_support.services.testCourse.TestCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OnlineCourChapterServiceImpl implements OnlineCourseChapterService {

    @Autowired
    OnlineCourseChapterMapper onlineCourseChapterMapper;
    @Autowired
    OnlineCourseChapterRepository onlineCourseChapterRepository;
    @Autowired
    LectureService lectureService;
    @Autowired
    TestCourseService testCourseService;
    @Override
    public void add(OnlineCourseChapterDto onlineCourseChapterDto) {
        OnlineCourseChapter onlineCourseChapter=new OnlineCourseChapter();
        onlineCourseChapter= onlineCourseChapterMapper.toEntity(onlineCourseChapterDto);
    }

    @Override
    public List<ChapterCourseDetailDto> getChapterCourseDetail(long courseId){
        List<Object[]> rawResults = onlineCourseChapterRepository.findChapterDetailByOnlineCourse(courseId);

        List<ChapterCourseDetailDto> chapterCourseDetailDtos = rawResults.stream()
                .map(row -> {
                    ChapterCourseDetailDto dto = new ChapterCourseDetailDto();
                    dto.setChapterId(((BigInteger) row[2]).longValue());
                    dto.setCourseId(((BigInteger) row[1]).longValue());
                    dto.setChapterWeight((Integer) row[3]);
                    dto.setChapterSequence((Integer) row[5]);
                    dto.setChapterName((String) row[4]);

                    // Lấy và set lecture cho chapter
                    List<LectureDto> lectureDtos = lectureService.getAllLectureOfCourse(dto.getChapterId(), dto.getCourseId());
                    List<TestCourseDto> testCourseDtos=testCourseService.getAllTestOfChapterCourse(dto.getChapterId(),dto.getCourseId());
                    dto.setLectureList(lectureDtos);
                    dto.setTestCourseDtos(testCourseDtos);
                    dto.setLectureCount(lectureDtos.size());

                    return dto;
                }).sorted(Comparator.comparingInt(ChapterCourseDetailDto::getChapterSequence))
                .collect(Collectors.toList());
        return chapterCourseDetailDtos;
    }
}
