package com.elearning.elearning_support.services.chatbot;

import com.elearning.elearning_support.dtos.chatbot.ChatRequestDto;
import com.elearning.elearning_support.dtos.chatbot.ChatResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {
    ChatResponseDto handle(ChatRequestDto req);
}
