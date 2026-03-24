package com.elearning.elearning_support.services.resultTestCourseService.impl;

import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDetailDto;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDetailResDto;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDto;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseResDto;
import com.elearning.elearning_support.entities.answer.Answer;
import com.elearning.elearning_support.entities.question.Question;
import com.elearning.elearning_support.entities.resultTestCourse.ResultTestCourseDetail;
import com.elearning.elearning_support.entities.resultTestCourse.ResultTestCourseEntity;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.entities.testCourse.TestCourseQuestion;
import com.elearning.elearning_support.mapper.resultTestCourse.ResultTestCourseDetailMapper;
import com.elearning.elearning_support.mapper.resultTestCourse.ResultTestCourseMapper;
import com.elearning.elearning_support.repositories.answer.AnswerRepository;
import com.elearning.elearning_support.repositories.question.QuestionRepository;
import com.elearning.elearning_support.repositories.resultTestCourse.ResultTestCourseDetailRepository;
import com.elearning.elearning_support.repositories.resultTestCourse.ResultTestCourseRepository;
import com.elearning.elearning_support.repositories.testCourse.TestCourseQuestionRepository;
import com.elearning.elearning_support.repositories.testCourse.TestCourseRepository;
import com.elearning.elearning_support.services.question.QuestionService;
import com.elearning.elearning_support.services.resultTestCourseService.ResultTestCourseService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import com.elearning.elearning_support.utils.pdfFile.TestCourseQuestionPdfExported;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ResultTestCourseServiceImpl implements ResultTestCourseService {
    @Autowired
    ResultTestCourseMapper resultTestCourseMapper;
    @Autowired
    ResultTestCourseDetailMapper resultTestCourseDetailMapper;
    @Autowired
    ResultTestCourseRepository resultTestCourseRepository;
    @Autowired
    ResultTestCourseDetailRepository resultTestCourseDetailRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    TestCourseRepository testCourseRepository;
    @Autowired
    TestCourseQuestionRepository testCourseQuestionRepository;
    @Autowired
    QuestionService questionService;
    @Override
    public void saveResultTestCourse(ResultTestCourseDto dto) {
        ResultTestCourseEntity entity=resultTestCourseMapper.toEntity(dto);
        ResultTestCourseEntity savedCourse= resultTestCourseRepository.save(entity);
        List<ResultTestCourseDetailDto>resultTestCourseDetailDtoList=dto.getResultTestCourseDetailDtoList();
        dto.setId(savedCourse.getId());
        int numberCorrectAns=0;
        for(ResultTestCourseDetailDto detailDto:resultTestCourseDetailDtoList){
            detailDto.setResultTestCourseId(dto.getId());
            ResultTestCourseDetail detail=resultTestCourseDetailMapper.toEntity(detailDto);
            List<Integer>selectedAnsIds= Arrays.asList(detail.getSelectedAnsId());
            boolean isCorrectAns=true;
            if(selectedAnsIds.isEmpty()){
                isCorrectAns=false;
            }else{
                for(Integer selectedAnsId:selectedAnsIds){
                    Answer answer=answerRepository.findById(Long.valueOf(selectedAnsId)).get();
                    if(answer.getIsCorrect()==false){
                        isCorrectAns=false;
                        break;
                    }
                }
            }
            if(isCorrectAns){
                numberCorrectAns++;
            }
            detail.setIsCorrect(isCorrectAns);
            resultTestCourseDetailRepository.save(detail);
        }
        long totalPoint=testCourseRepository.findById(dto.getTestCourseId()).get().getTotalPoint();
        long questionQuantity=testCourseRepository.findById(dto.getTestCourseId()).get().getQuestionQuantity();
        double studentPoint = totalPoint * ((double) numberCorrectAns / questionQuantity);
        savedCourse.setStudentPoint(studentPoint);
        System.out.println("student Point :"+studentPoint);
        resultTestCourseRepository.save(savedCourse);
    }
    /**
     * return detail result student .
     * after submit test -> server return detail result
     */
    @Override
    public ResultTestCourseResDto getResStudentTest(long testCourseId) {
        long currentUser= AuthUtils.getCurrentUserId();
        long resultId=getResultId(currentUser,testCourseId);
        ResultTestCourseEntity resultTestCourseEntity=resultTestCourseRepository.findById(resultId).get();
        Long resultTestCourseId=resultTestCourseEntity.getId();
        ResultTestCourseResDto resultTestCourseResDto =new ResultTestCourseResDto();
        resultTestCourseResDto.setStudentPoint(resultTestCourseEntity.getStudentPoint());
        resultTestCourseResDto.setTestPoint(testCourseRepository.findById(testCourseId).get().getTotalPoint());
        List<ResultTestCourseDetailResDto>detailResDtos=new ArrayList<>();
        List<TestCourseQuestion>testCourseQuestions=  testCourseQuestionRepository.findByTestCourseEntityId(testCourseId);
        for(TestCourseQuestion testCourseQuestion:testCourseQuestions){
            Long questionId=testCourseQuestion.getQuestion().getId();
            String questionContent=testCourseQuestion.getQuestion().getContent();

            ResultTestCourseDetail resultTestCourseDetail=resultTestCourseDetailRepository
                    .findByQuestionIdAndResultTestCourseId(
                            questionId ,resultTestCourseId);
            ResultTestCourseDetailResDto detailResDto=new ResultTestCourseDetailResDto();

            // set gia tri detalRest
            detailResDto.setQuestionId(questionId);
            detailResDto.setQuestionContent(questionContent);
            detailResDto.setAnswerResDTOS(questionService.getAnswersByQuestionId(questionId));
            detailResDto.setSelectedAns(Arrays.asList(resultTestCourseDetail.getSelectedAnsId()));
            detailResDto.setCorrect(resultTestCourseDetail.getIsCorrect());
            detailResDtos.add(detailResDto);
        }
        resultTestCourseResDto.setResultTestCourseDetailResDtos(detailResDtos);
        return resultTestCourseResDto;
    }

    @Override
    public Long getResultId(long userId, long testId) {
        long getResultTestCourseId=resultTestCourseRepository.findByUserIdAndTestCourseEntityId(userId,testId).getId();
        return getResultTestCourseId;
    }


}
