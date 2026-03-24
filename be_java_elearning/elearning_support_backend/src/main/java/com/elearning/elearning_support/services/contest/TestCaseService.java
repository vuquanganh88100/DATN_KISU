package com.elearning.elearning_support.services.contest;

import com.elearning.elearning_support.dtos.contest.TestCaseDto;
import com.elearning.elearning_support.dtos.contest.TestCaseGenByJudgeDto;

import java.util.List;

public interface TestCaseService {
    String generateInputTestCase(String problemContest);
    String cleanJson(String rawText);
    List<TestCaseDto> generateTestCase(List<TestCaseGenByJudgeDto> testCaseGenByJudgeDtos);
}
