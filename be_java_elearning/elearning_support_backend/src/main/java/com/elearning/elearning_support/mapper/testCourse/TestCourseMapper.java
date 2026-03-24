package com.elearning.elearning_support.mapper.testCourse;

import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.repositories.chapter.ChapterRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseRepository;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class TestCourseMapper {
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    OnlineCourseRepository onlineCourseRepository;
    public TestCourseEntity toEntity(TestCourseDto testCourseDto){
        TestCourseEntity testCourseEntity=new TestCourseEntity();
        System.out.println(testCourseDto.getChapterId());


        Chapter chapter = chapterRepository.findById(testCourseDto.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        testCourseEntity.setChapter(chapter);
        testCourseEntity.setOnlineCourse(onlineCourseRepository.findById(testCourseDto.getOnlineCourseId()).get());
        testCourseEntity.setCreatedBy(Math.toIntExact(AuthUtils.getCurrentUserId()));
        testCourseEntity.setDuration(testCourseDto.getDuration());
        testCourseEntity.setSequence(testCourseDto.getSequence());
        testCourseEntity.setName(testCourseDto.getName());
        testCourseEntity.setEasyQuestion(testCourseDto.getEasyQuestion());
        testCourseEntity.setHardQuestion(testCourseDto.getHardQuestion());
        testCourseEntity.setMediumQuestion(testCourseDto.getMediumQuestion());
        testCourseEntity.setTotalPoint(testCourseDto.getTotalPoint());
        testCourseEntity.setTestCourseWeightl(testCourseDto.getTestCourseWeight());
        testCourseEntity.setQuestionQuantity(testCourseDto.getQuestionId().size());
        return  testCourseEntity;
    }
    public TestCourseDto toDto(TestCourseEntity testCourseEntity){
        TestCourseDto testCourseDto=new TestCourseDto();

        testCourseDto.setChapterId(testCourseEntity.getChapter().getId());
        testCourseDto.setName(testCourseEntity.getName());
        testCourseDto.setId(testCourseEntity.getId());
        testCourseDto.setMediumQuestion(testCourseEntity.getMediumQuestion());
        testCourseDto.setHardQuestion(testCourseEntity.getHardQuestion());
        testCourseDto.setEasyQuestion(testCourseEntity.getEasyQuestion());
        testCourseDto.setDuration(testCourseEntity.getDuration());
        testCourseDto.setTotalPoint(testCourseEntity.getTotalPoint());
        testCourseDto.setQuestionQuantity(testCourseEntity.getQuestionQuantity());
        testCourseDto.setOnlineCourseId(testCourseEntity.getOnlineCourse().getId());
        testCourseDto.setSequence(testCourseEntity.getSequence());
        testCourseDto.setTestCourseWeight(testCourseEntity.getTestCourseWeightl());
        return  testCourseDto;
    }
}
