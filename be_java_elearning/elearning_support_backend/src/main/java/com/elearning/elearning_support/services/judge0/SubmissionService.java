package com.elearning.elearning_support.services.judge0;

import com.elearning.elearning_support.dtos.judge0.JudgeResultDto;
import com.elearning.elearning_support.dtos.judge0.SubmissionDto;
import com.elearning.elearning_support.dtos.judge0.SubmissionResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SubmissionService {
    SubmissionResponseDto judgeSubmission(SubmissionDto submissionDto);
    List<SubmissionResponseDto> getSubmissionsByStudentId(Long studentId);
    List<SubmissionResponseDto> getSubmissionsByProblemId(Long problemId);
    SubmissionResponseDto getSourceCodeBySubmissionId(Long submissionId);
    List<JudgeResultDto> runCode(SubmissionDto submissionDto);
}
