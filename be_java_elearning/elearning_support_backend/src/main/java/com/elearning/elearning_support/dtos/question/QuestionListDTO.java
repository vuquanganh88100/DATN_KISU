package com.elearning.elearning_support.dtos.question;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.BeanUtils;
import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import com.elearning.elearning_support.utils.object.ObjectMapperUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionListDTO {

    @Schema(description = "Id câu hỏi")
    Long id;

    @Schema(description = "Nội dung câu hỏi")
    String content;

    @Schema(description = "Mã câu hỏi")
    String code;

    @Schema(description = "Mức độ câu hỏi")
    Integer level;

    @Schema(description = "Câu trả lời của câu hỏi")
    List<AnswerResDTO> lstAnswer = new ArrayList<>();

    @Schema(description = "Cờ câu hỏi đã nằm trong test hay chưa")
    Boolean isInTest;

    @Schema(description = "Câu hỏi có nhiều phương án?")
    Boolean isMultipleAnswers;

    @Schema(description = "Cờ kiểm tra phiên bản mới nhất của câu hỏi")
    Boolean isNewest;

    public QuestionListDTO(IListQuestionDTO iListQuestionDTO) {
        BeanUtils.copyProperties(iListQuestionDTO, this);
        this.lstAnswer = ObjectMapperUtil.listMapper(iListQuestionDTO.getLstAnswerJson(), AnswerResDTO.class);
        this.lstAnswer.sort(Comparator.comparing(AnswerResDTO::getId));
    }

}
