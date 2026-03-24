package com.elearning.elearning_support.dtos.question;

public interface IListQuestionDTO {

    Long getId();

    String getCode();

    String getContent();

    Integer getLevel();

    String getLstAnswerJson();

    Boolean getIsInTest();

    Boolean getIsMultipleAnswers();

    Boolean getIsNewest();
}
