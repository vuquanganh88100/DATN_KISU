package com.elearning.elearning_support.services.judge0;

import com.elearning.elearning_support.dtos.judge0.JudgeRequestDto;
import com.elearning.elearning_support.dtos.judge0.JudgeResultDto;
import org.springframework.stereotype.Service;

@Service
public interface JudgeService {
    String submit(JudgeRequestDto request);
    JudgeResultDto pollResult(String token);

}
