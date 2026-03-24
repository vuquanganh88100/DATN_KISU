package com.elearning.elearning_support.mapper.resultTestCourse;

import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDetailDto;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDto;
import com.elearning.elearning_support.entities.resultTestCourse.ResultTestCourseDetail;
import com.elearning.elearning_support.entities.resultTestCourse.ResultTestCourseEntity;
import com.elearning.elearning_support.repositories.resultTestCourse.ResultTestCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResultTestCourseDetailMapper {
    @Autowired
    ResultTestCourseRepository resultTestCourseRepository;
    public ResultTestCourseDetail toEntity(ResultTestCourseDetailDto dto){
        ResultTestCourseDetail entity=new ResultTestCourseDetail();
        entity.setQuestionId(dto.getQuestionId());
        entity.setResultTestCourse(resultTestCourseRepository.findById(dto.getResultTestCourseId()).get());
        entity.setSelectedAnsId(dto.getSelectedAnsId());
        return entity;
    }
    public ResultTestCourseDetailDto toDto(ResultTestCourseDetail entity){
        ResultTestCourseDetailDto dto=new ResultTestCourseDetailDto();
        dto.setResultTestCourseId(entity.getResultTestCourse().getId());
        dto.setQuestionId(dto.getQuestionId());
        dto.setSelectedAnsId(dto.getSelectedAnsId());
        return dto;
    }
}
