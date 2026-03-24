package com.elearning.elearning_support.controllers.chatBotController;

import com.elearning.elearning_support.dtos.chatbot.ChatRequestDto;
import com.elearning.elearning_support.dtos.chatbot.ChatResponseDto;
import com.elearning.elearning_support.services.chatbot.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatBotController {
    @Autowired
    ChatService chatService;


    @PostMapping
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        return chatService.handle(request);
    }
}

