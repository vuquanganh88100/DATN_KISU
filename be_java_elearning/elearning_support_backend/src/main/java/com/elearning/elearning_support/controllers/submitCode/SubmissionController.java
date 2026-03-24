//package com.elearning.elearning_support.controllers.submitCode;
//
//
//import com.elearning.elearning_support.dtos.judge0.SubmissionDto;
//import com.elearning.elearning_support.dtos.judge0.SubmissionResponseDto;
//import com.elearning.elearning_support.services.judge0.SubmissionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/problem-contest")
//public class SubmissionController {
//    @Autowired
//    SubmissionService submissionService;
//    @PostMapping("/submit-code")
//    public ResponseEntity<SubmissionResponseDto> submitCode(@RequestBody SubmissionDto dto) {
//        SubmissionResponseDto submissionResponseDto=submissionService.judgeSubmission(dto);
//        return ResponseEntity.ok(submissionResponseDto);
//    }
//
//}
