package com.elearning.elearning_support.mapper.testCourse;

import com.elearning.elearning_support.dtos.testCourse.TestCourseQuestionDto;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.entities.testCourse.TestCourseQuestion;
import com.elearning.elearning_support.repositories.chapter.ChapterRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseRepository;
import com.elearning.elearning_support.repositories.question.QuestionRepository;
import com.elearning.elearning_support.repositories.testCourse.TestCourseQuestionRepository;
import com.elearning.elearning_support.repositories.testCourse.TestCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Column;

@Component
public class TestCourseQuestionMapper {
    @Autowired
    TestCourseRepository testCourseRepository;
    @Autowired
    QuestionRepository questionRepository;
    public TestCourseQuestion toEntity(TestCourseQuestionDto dto){
        TestCourseQuestion testCourseQuestion=new TestCourseQuestion();
        testCourseQuestion.setQuestion(questionRepository.findById(dto.getQuestionId()).get());
        testCourseQuestion.setTestCourseEntity(testCourseRepository.findById(dto.getTestCourseId()).get());
    return  testCourseQuestion;
    }
    public TestCourseQuestionDto toDto(TestCourseQuestion entity){
        TestCourseQuestionDto dto=new TestCourseQuestionDto();
        dto.setQuestionId(entity.getQuestion().getId());
        dto.setTestCourseId(entity.getTestCourseEntity().getId());
        return dto;
    }
}
