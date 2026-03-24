package com.elearning.elearning_support.controllers.submission;

import com.elearning.elearning_support.dtos.judge0.SubmissionDto;
import com.elearning.elearning_support.dtos.judge0.SubmissionResponseDto;
import com.elearning.elearning_support.services.judge0.SubmissionService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    @Autowired
    SubmissionService submissionService;
    @GetMapping()
    public ResponseEntity<List<SubmissionResponseDto>>
    getSubmissionsByStudent() {
        long studentId= AuthUtils.getCurrentUserId();
        List<SubmissionResponseDto> submissions =
                submissionService.getSubmissionsByStudentId(studentId);

        return ResponseEntity.ok(submissions);
    }
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<List<SubmissionResponseDto>>
    getSubmissionsByProblem(@PathVariable Long problemId) {

        return ResponseEntity.ok(
                submissionService.getSubmissionsByProblemId(problemId)
        );
    }
    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionResponseDto> getSourceCode(
            @PathVariable Long submissionId) {

        SubmissionResponseDto submissionResponseDto =
                submissionService.getSourceCodeBySubmissionId(submissionId);

        return ResponseEntity.ok(submissionResponseDto);
    }
}
