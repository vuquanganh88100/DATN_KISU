package com.elearning.elearning_support.dtos.testCourse;

import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCourseQuestionDto {
    private long id;
    private long questionId;
    private long testCourseId;
    private List<AnswerResDTO> answerResDTOList;
    private String questionContent;
    private boolean isMultipleAns;

}
