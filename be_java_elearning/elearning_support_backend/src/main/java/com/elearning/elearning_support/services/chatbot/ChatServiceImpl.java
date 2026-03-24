package com.elearning.elearning_support.services.chatbot;

import com.elearning.elearning_support.dtos.chatbot.ChatLlmService;
import com.elearning.elearning_support.dtos.chatbot.ChatRequestDto;
import com.elearning.elearning_support.dtos.chatbot.ChatResponseDto;
import com.elearning.elearning_support.dtos.chatbot.ModeChatBot;
import com.elearning.elearning_support.entities.contest.ProblemContest;
import com.elearning.elearning_support.entities.submitContest.SubmissionContest;
import com.elearning.elearning_support.repositories.contest.ProblemContestRepository;
import com.elearning.elearning_support.repositories.submitContest.JudgeResultRepository;
import com.elearning.elearning_support.repositories.submitContest.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    ProblemContestRepository problemContestRepository;
    @Autowired
    JudgeResultRepository judgeResultRepository;
    @Autowired
    SubmissionRepository submissionRepository;
    @Autowired
    PromptBuilderChatBot promptBuilderChatBot;
    @Autowired
    ChatLlmService chatLlmService;


    private ModeChatBot parseMode(String message) {
        if (message.startsWith("/hint")) return ModeChatBot.HINT;
        if (message.startsWith("/debug")) return ModeChatBot.DEBUG;
        if (message.startsWith("/explain")) return ModeChatBot.EXPLAIN;
        throw new IllegalArgumentException("Bạn cần bắt đầu cuộc hội thoại với /hint , /debug hoặc /explain");
    }
    public ChatResponseDto handle(ChatRequestDto req) {

        ModeChatBot mode = parseMode(req.getMessage());

        ProblemContest problem = problemContestRepository.findById(req.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        SubmissionContest submission = null;
        if (req.getSubmissionId() != null) {
            submission = submissionRepository.findById(req.getSubmissionId())
                    .orElse(null);
        }

        String prompt = promptBuilderChatBot.build(
                mode,
                problem,
                submission,
                req.getMessage()
        );
        String answer = chatLlmService.chat(prompt);

        // 5. Trả response
        return new ChatResponseDto(mode.name(), answer);
    }

}
