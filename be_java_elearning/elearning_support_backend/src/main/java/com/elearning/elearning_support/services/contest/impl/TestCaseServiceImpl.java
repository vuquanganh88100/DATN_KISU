package com.elearning.elearning_support.services.contest.impl;

import com.elearning.elearning_support.dtos.contest.TestCaseDto;
import com.elearning.elearning_support.dtos.contest.TestCaseGenByJudgeDto;
import com.elearning.elearning_support.dtos.judge0.JudgeRequestDto;
import com.elearning.elearning_support.dtos.judge0.JudgeResultDto;
import com.elearning.elearning_support.entities.contest.TestCase;
import com.elearning.elearning_support.services.contest.TestCaseService;
import com.elearning.elearning_support.services.judge0.JudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TestCaseServiceImpl implements TestCaseService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;
    @Autowired
    JudgeService judgeService;


    @Override
    public String generateInputTestCase(String problemContest) {

        String url = baseUrl + "/v1/chat/completions";
        System.out.println("baseUrl: "+baseUrl);

        String prompt = String.format(
                "Tạo đúng 10 test case cho đề bài sau, trả về CHỈ JSON hợp lệ.\n" +
                        "Mỗi phần tử có dạng: {\"input\": \"...\", \"expected_output\": \"...\"}.\n" +
                        "Không markdown, không giải thích, không dấu ```.\n" +
                        "Đề bài: %s",
                problemContest
        );

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0.7
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.getBody().get("choices");

            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            return message.get("content").toString().trim();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi gọi Groq API: " + e.getMessage(), e);
        }
    }

    /**
     * Loại bỏ phần ```json ... ``` hoặc ``` ... ```
     */
    public String cleanJson(String rawText) {
        // Regex để lấy nội dung bên trong ```json ... ```
        return rawText.replaceAll("(?s)```(?:json)?\\s*(.*?)\\s*```", "$1").trim();
    }

    @Override
    public      List<TestCaseDto> generateTestCase(List<TestCaseGenByJudgeDto> testCaseGenByJudgeDtos)
    {
        List<TestCaseDto>testCaseDtos=new ArrayList<>();
        for(TestCaseGenByJudgeDto testCaseGenByJudgeDto:testCaseGenByJudgeDtos){
            TestCaseDto testCaseDto=new TestCaseDto();
            JudgeRequestDto req = new JudgeRequestDto(
                    testCaseGenByJudgeDto.getSourceCode(),
                    testCaseGenByJudgeDto.getLanguageId(),
                    testCaseGenByJudgeDto.getInput(),
                    testCaseGenByJudgeDto.getExpectedOutput()
            );
            String token = judgeService.submit(req);
            JudgeResultDto result = judgeService.pollResult(token);
            String actualOutput = result.getStdout() != null
                    ? result.getStdout().trim()
                    : "";
            testCaseDto.setPublic(false);
            testCaseDto.setInput(testCaseGenByJudgeDto.getInput());
            testCaseDto.setExpectedOutput(actualOutput);
            testCaseDtos.add(testCaseDto);
        }
        return  testCaseDtos;

    }

}
