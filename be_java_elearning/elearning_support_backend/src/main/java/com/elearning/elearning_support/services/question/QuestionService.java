package com.elearning.elearning_support.services.question;

import java.util.List;
import java.util.Set;

import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.elearning.elearning_support.dtos.fileAttach.importFile.ImportResponseDTO;
import com.elearning.elearning_support.dtos.question.QuestionDetailsDTO;
import com.elearning.elearning_support.dtos.question.QuestionListCreateDTO;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.dtos.question.QuestionUpdateDTO;
import com.elearning.elearning_support.enums.question.QuestionLevelEnum;

@Service
public interface QuestionService {

    void createListQuestion(QuestionListCreateDTO createDTO);

    void updateQuestion(Long questionId, QuestionUpdateDTO updateDTO);

    QuestionListResDTO getListQuestion(Long subjectId, String subjectCode, Set<Long> chapterId, String chapterCode, QuestionLevelEnum level,
        String search, Long testId, Integer fetchSize);

    /**
     * Import questions
     */
    ImportResponseDTO importQuestion(MultipartFile fileImport);


    /**
     * Get question details
     */
    QuestionDetailsDTO getQuestionDetails(Long questionId);


    /**
     * Xóa question
     */
    void deleteQuestion(Long questionId);
    List<AnswerResDTO> getAnswersByQuestionId(Long questionId) ;

}
