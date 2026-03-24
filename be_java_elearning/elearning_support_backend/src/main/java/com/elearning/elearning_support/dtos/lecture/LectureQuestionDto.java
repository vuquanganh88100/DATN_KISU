package com.elearning.elearning_support.dtos.lecture;

import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureQuestionDto {
    private Long id;
    private Integer timeStart;
    private Integer timeEnd;
    private Long lectureId;
    private Long questionId;
    private String content ;
    private  List<AnswerResDTO> answerResDTOList;
    private boolean isMultipleAns;
    private Long timeShownUp;
}
