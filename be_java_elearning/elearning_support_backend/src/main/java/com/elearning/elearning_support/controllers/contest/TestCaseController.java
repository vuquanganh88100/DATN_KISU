package com.elearning.elearning_support.controllers.contest;

import com.elearning.elearning_support.dtos.contest.TestCaseDto;
import com.elearning.elearning_support.dtos.contest.TestCaseGenByJudgeDto;
import com.elearning.elearning_support.services.contest.TestCaseService;
import com.google.api.client.util.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-case")
public class TestCaseController {
    @Autowired
    TestCaseService testCaseService;
    @PostMapping("/generate-input")
    public ResponseEntity<String> generateInput(@RequestBody String problemContest) {
        try {
            String result = testCaseService.generateInputTestCase(problemContest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @PostMapping("/generate-testcase")
    public ResponseEntity<List<TestCaseDto>> generateTestCase(
            @RequestBody List<TestCaseGenByJudgeDto> testCaseGenByJudgeDtos
    ) {
        List<TestCaseDto> testCaseDtos =
                testCaseService.generateTestCase(testCaseGenByJudgeDtos);
        return ResponseEntity.ok(testCaseDtos);
    }


}


