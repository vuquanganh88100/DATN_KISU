package com.elearning.elearning_support.repositories.answer;

import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import com.elearning.elearning_support.dtos.answer.AnswerResInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.elearning.elearning_support.entities.answer.Answer;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Transactional
    @Modifying
    void deleteAllByQuestionId(Long questionId);

    @Query(value = "SELECT a.id AS id, a.content AS content, a.is_correct AS isCorrect " +
            "FROM answer a WHERE a.question_id = :questionId", nativeQuery = true)
    List<AnswerResInterface> findAnswersByQuestionId(@Param("questionId") Long questionId);
}
