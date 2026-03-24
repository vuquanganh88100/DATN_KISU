package com.elearning.elearning_support.dtos.question;

import java.util.ArrayList;
import java.util.List;
import com.elearning.elearning_support.dtos.answer.AnswerReqDTO;
import com.elearning.elearning_support.enums.question.QuestionLevelEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionCreateDTO {

    String content;

    QuestionLevelEnum level;

    Boolean isMultipleAnswers = Boolean.TRUE;

    Long[] lstImageId;

    List<AnswerReqDTO> lstAnswer = new ArrayList<>();
}
