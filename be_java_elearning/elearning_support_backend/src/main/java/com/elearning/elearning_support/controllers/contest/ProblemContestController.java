package com.elearning.elearning_support.controllers.contest;

import com.elearning.elearning_support.dtos.contest.ProblemContestDto;
import com.elearning.elearning_support.dtos.judge0.JudgeResultDto;
import com.elearning.elearning_support.dtos.judge0.SubmissionDto;
import com.elearning.elearning_support.dtos.judge0.SubmissionResponseDto;
import com.elearning.elearning_support.services.contest.ProblemContestService;
import com.elearning.elearning_support.services.judge0.SubmissionService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/problem-contest")
public class ProblemContestController {

    @Autowired
    ProblemContestService problemService;
    @Autowired
    SubmissionService submissionService;
    @PostMapping
    @Operation(summary = "Tạo mới problem contest")
    public ResponseEntity<ProblemContestDto> createProblem(@RequestBody ProblemContestDto problemDto) {
        ProblemContestDto createdProblem = problemService.createProblem(problemDto);
        return new ResponseEntity<>(createdProblem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật problem contest")
    public ResponseEntity<ProblemContestDto> updateProblem(
            @PathVariable Long id,
            @RequestBody ProblemContestDto problemDto) {
        ProblemContestDto updatedProblem = problemService.updateProblem(id, problemDto);
        return ResponseEntity.ok(updatedProblem);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết problem contest theo ID")
    public ResponseEntity<ProblemContestDto> getProblemById(@PathVariable Long id) {
        ProblemContestDto problem = problemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping
    public ResponseEntity<Page<ProblemContestDto>> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        long currentUserId= AuthUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ProblemContestDto> problems = problemService.getAllProblems(pageable,currentUserId);
        return ResponseEntity.ok(problems);
    }


    @GetMapping("/topic/{topicId}")
    @Operation(summary = "Lấy danh sách problem contest theo topic")
    public ResponseEntity<List<ProblemContestDto>> getProblemsByTopicId(@PathVariable Long topicId) {
        List<ProblemContestDto> problems = problemService.getProblemsByTopicId(topicId);
        return ResponseEntity.ok(problems);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa problem contest")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/submit-code")
    public ResponseEntity<SubmissionResponseDto> submitCode(@RequestBody SubmissionDto dto) {
        SubmissionResponseDto submissionResponseDto=submissionService.judgeSubmission(dto);
        return ResponseEntity.ok(submissionResponseDto);
    }
    @PostMapping("/run-code")
    public ResponseEntity<List<JudgeResultDto>> runCode(
            @RequestBody SubmissionDto submissionDto) {

        return ResponseEntity.ok(
                submissionService.runCode(submissionDto)
        );
    }

}
