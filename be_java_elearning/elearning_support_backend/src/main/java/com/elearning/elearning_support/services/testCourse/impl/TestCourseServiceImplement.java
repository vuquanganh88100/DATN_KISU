package com.elearning.elearning_support.services.testCourse.impl;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.dtos.testCourse.TestCourseQuestionDto;
import com.elearning.elearning_support.entities.lecture.Lecture;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.entities.testCourse.TestCourseQuestion;
import com.elearning.elearning_support.mapper.testCourse.TestCourseMapper;
import com.elearning.elearning_support.mapper.testCourse.TestCourseQuestionMapper;
import com.elearning.elearning_support.repositories.question.QuestionRepository;
import com.elearning.elearning_support.repositories.testCourse.TestCourseQuestionRepository;
import com.elearning.elearning_support.repositories.testCourse.TestCourseRepository;
import com.elearning.elearning_support.services.question.QuestionService;
import com.elearning.elearning_support.services.testCourse.TestCourseService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
@Service
public class TestCourseServiceImplement implements TestCourseService {
    @Autowired
    private TestCourseRepository testCourseRepository;

    @Autowired
    private TestCourseQuestionRepository testCourseQuestionRepository;

    @Autowired
    private TestCourseMapper testCourseMapper;
    @Autowired
    private TestCourseQuestionMapper testCourseQuestionMapper;
    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionRepository questionRepository;

    @Override
    public void addTestCourse(TestCourseDto testCourseDto) {
        TestCourseEntity testCourseEntity = testCourseMapper.toEntity(testCourseDto);
        testCourseEntity = testCourseRepository.save(testCourseEntity);

        TestCourseQuestionDto testCourseQuestionDto = new TestCourseQuestionDto();
        testCourseQuestionDto.setTestCourseId(testCourseEntity.getId());
        List<Long> questionIds = testCourseDto.getQuestionId();
        for (Long questionId : questionIds) {
            testCourseQuestionDto.setQuestionId(questionId);
            testCourseQuestionRepository.save(testCourseQuestionMapper.toEntity(testCourseQuestionDto));
        }
    }

    @Override
    public List<TestCourseDto> getAllTestOfChapterCourse(long chapterId, long onlineCourseId) {
        List<TestCourseEntity> testCourseEntities = testCourseRepository.getTestCourseByChapterAndCourse(chapterId, onlineCourseId);
        List<TestCourseDto> testCourseDtos = new ArrayList<>();
        for (TestCourseEntity testCourseEntity : testCourseEntities) {
            long testCourseId=testCourseEntity.getId();
             TestCourseDto testCourseDto=getDetailTestCourse(testCourseId);
//            TestCourseDto testCourseDto = testCourseMapper.toDto(testCourseEntity);
//            List<Long>questionIds=new ArrayList<>();
//            List<TestCourseQuestion>testCourseQuestions=  testCourseQuestionRepository.findByTestCourseEntityId(testCourseEntity.getId());
//            for(TestCourseQuestion testCourseQuestion:testCourseQuestions){
//                TestCourseQuestionDto dto=testCourseQuestionMapper.toDto(testCourseQuestion);
//                questionIds.add(dto.getQuestionId());
//            }
//            testCourseDto.setTestCourseQuestionDtos();
//            testCourseDto.setQuestionId(questionIds);
            testCourseDtos.add(testCourseDto);
        }
        Collections.sort(testCourseDtos, new Comparator<TestCourseDto>() {
            @Override
            public int compare(TestCourseDto o1, TestCourseDto o2) {
                return Integer.compare(o1.getSequence(),o2.getSequence());
            }
        });
        return testCourseDtos;
    }

    @Override
    public TestCourseDto getTestCourseOverview(long testCourseId) {
        TestCourseEntity testCourseEntity=testCourseRepository.findById(testCourseId).get();
        TestCourseDto testCourseDto=testCourseMapper.toDto(testCourseEntity);
        return testCourseDto;

    }

    @Override
    public TestCourseDto getDetailTestCourse(long testCourseId) {
        TestCourseEntity testCourseEntity=testCourseRepository.findById(testCourseId).get();
        TestCourseDto testCourseDto=testCourseMapper.toDto(testCourseEntity);
        List<TestCourseQuestionDto> testCourseQuestionDtos=new ArrayList<>();
        List<TestCourseQuestion>testCourseQuestions=  testCourseQuestionRepository.findByTestCourseEntityId(testCourseId);
        for(TestCourseQuestion testCourseQuestion:testCourseQuestions){
            TestCourseQuestionDto testCourseQuestionDto=new TestCourseQuestionDto();
            long questionId=testCourseQuestion.getQuestion().getId();
            testCourseQuestionDto.setQuestionContent(questionRepository.findById(questionId).get().getContent());
            testCourseQuestionDto.setMultipleAns(questionRepository.findById(questionId).get().getIsMultipleAnswers());
            testCourseQuestionDto.setTestCourseId(testCourseQuestion.getId());
            testCourseQuestionDto.setQuestionId(testCourseQuestion.getQuestion().getId());
            testCourseQuestionDto.setAnswerResDTOList(questionService.getAnswersByQuestionId(questionId));
            testCourseQuestionDtos.add(testCourseQuestionDto);
        }
        testCourseDto.setTestCourseQuestionDtos(testCourseQuestionDtos);
        return testCourseDto;
    }

    @Override
    public Timestamp getLastTimeToDoTest(long onlineCourseId, long userId) {
        Object results = testCourseRepository.getLastTimeToDoTest(userId, onlineCourseId);
        Timestamp lastUpdated=null;
        if(results!=null) {
            Object[] row = (Object[]) results;
             lastUpdated = (Timestamp) row[2];
        }
        return lastUpdated;
    }
}
