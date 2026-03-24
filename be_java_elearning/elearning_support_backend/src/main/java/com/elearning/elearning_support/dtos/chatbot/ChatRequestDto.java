package com.elearning.elearning_support.dtos.chatbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDto {
    private long problemId;
    private Long submissionId ; // optional
    private String message;
}
