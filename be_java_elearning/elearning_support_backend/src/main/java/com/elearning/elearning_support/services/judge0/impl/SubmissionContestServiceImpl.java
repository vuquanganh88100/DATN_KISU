//package com.elearning.elearning_support.services.judge0.impl;
//
//import com.elearning.elearning_support.dtos.judge0.JudgeResultDto;
//import com.elearning.elearning_support.dtos.judge0.SubmissionDto;
//import com.elearning.elearning_support.entities.contest.ProblemContest;
//import com.elearning.elearning_support.entities.contest.TestCase;
//import com.elearning.elearning_support.entities.submitContest.JudgeResult;
//import com.elearning.elearning_support.entities.submitContest.SubmissionContest;
//import com.elearning.elearning_support.entities.users.User;
//import com.elearning.elearning_support.repositories.contest.ProblemContestRepository;
//import com.elearning.elearning_support.repositories.contest.TestCaseRepository;
//import com.elearning.elearning_support.repositories.submitContest.JudgeResultRepository;
//import com.elearning.elearning_support.repositories.submitContest.SubmissionRepository;
//import com.elearning.elearning_support.repositories.users.UserRepository;
//import com.elearning.elearning_support.services.judge0.SubmissionContestService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class SubmissionContestServiceImpl implements SubmissionContestService{
//    private static final String PISTON_URL = "https://emkc.org/api/v2/piston/execute";
//    private static final double TIME_LIMIT_SECONDS = 5.0; // 5 seconds time limit
//    private static final double MEMORY_LIMIT_MB = 256.0; // 256 MB memory limit
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Autowired
//    SubmissionRepository submissionRepository;
//    @Autowired
//    JudgeResultRepository judgeResultRepository;
//    @Autowired
//    TestCaseRepository testCaseRepository;
//    @Autowired
//    ProblemContestRepository problemContestRepository;
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    public SubmissionDto submitCode(SubmissionDto submissionDto) {
//        User student = userRepository.findById(submissionDto.getStudentId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        ProblemContest problem = problemContestRepository.findById(submissionDto.getProblemId())
//                .orElseThrow(() -> new RuntimeException("Problem not found"));
//
//        SubmissionContest submission = new SubmissionContest();
//        submission.setLanguageId(submissionDto.getLanguageId());
//        submission.setSourceCode(submissionDto.getSourceCode());
//        submission.setProblemContest(problem);
//        submission.setStudentId(student);
//        submission.setSubmittedAt(LocalDateTime.now());
//
//        submission = submissionRepository.save(submission);
//        List<TestCase> testCases = testCaseRepository.findByProblemContest_Id(submissionDto.getProblemId());
//
//        boolean allAccepted = true;
//        List<JudgeResultDto> judgeResultDtos = new ArrayList<>();
//
//        for (TestCase tc : testCases) {
//            String language = mapLanguageIdToPistonLanguage(submissionDto.getLanguageId());
//            ExecutionResult execResult = executeCodeWithTiming(
//                    submissionDto.getSourceCode(),
//                    language,
//                    tc.getInput()
//            );
//
//            String status;
//            String actualOutput = execResult.getStdout();
//
//            // Determine final status
//            if (!execResult.getStatus().equals("Success")) {
//                status = execResult.getStatus();
//                if (execResult.getStatus().equals("Runtime Error") && !execResult.getStderr().isEmpty()) {
//                    actualOutput = execResult.getStderr();
//                }
//            } else {
//                // Check if output matches expected
//                String trimmedActualOutput = actualOutput.trim();
//                String trimmedExpectedOutput = tc.getExpectedOutput().trim();
//
//                if (trimmedActualOutput.equals(trimmedExpectedOutput)) {
//                    status = "Accepted";
//                } else {
//                    status = "Wrong Answer";
//                }
//            }
//
//            if (!"Accepted".equalsIgnoreCase(status)) {
//                allAccepted = false;
//            }
//
//            // Save JudgeResult
//            JudgeResult jr = new JudgeResult();
//            jr.setSubmissionContest(submission);
//            jr.setTestCase(tc);
//            jr.setStatus(status);
//            jr.setActualContext(actualOutput);
//            jr.setExecutionTime(execResult.getExecutionTime());
//            judgeResultRepository.save(jr);
//
//            // Convert to DTO
//            JudgeResultDto jrDto = new JudgeResultDto();
//            jrDto.setTestCaseId(tc.getId());
//            jrDto.setStatus(status);
//            jrDto.setActualOutput(actualOutput);
//            jrDto.setExecutionTime(execResult.getExecutionTime());
//            judgeResultDtos.add(jrDto);
//        }
//
//        submission.setVerdict(allAccepted ? "Accepted" : "Wrong Answer");
//        submissionRepository.save(submission);
//
//        // Convert to DTO
//        SubmissionDto resultDto = new SubmissionDto();
//        resultDto.setProblemId(problem.getId());
//        resultDto.setStudentId(student.getId());
//        resultDto.setLanguageId(submission.getLanguageId());
//        resultDto.setSourceCode(submission.getSourceCode());
//        resultDto.setVerdict(submission.getVerdict());
//        resultDto.setSubmiitedAt(submission.getSubmittedAt());
//        resultDto.setJudgeResultDtoList(judgeResultDtos);
//
//        return resultDto;
//    }
//
//    /**
//     * Excute time = time to call api becasude pistonAPI not provide excution time ...
//     */
//    private ExecutionResult executeCodeWithTiming(String sourceCode, String language, String input) {
//        try {
//            Map<String, Object> reqBody = new HashMap<>();
//            reqBody.put("language", language);
//            reqBody.put("version", "*");
//
//            // Create files array
//            List<Map<String, String>> files = new ArrayList<>();
//            Map<String, String> file = new HashMap<>();
//            file.put("content", sourceCode);
//            files.add(file);
//            reqBody.put("files", files);
//
//            // Set input
//            reqBody.put("stdin", input);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reqBody, headers);
//
//            // Start timing with high precision
//            long startTime = System.nanoTime();
//            ResponseEntity<Map> response = restTemplate.postForEntity(PISTON_URL, entity, Map.class);
//            long endTime = System.nanoTime();
//
//            // Calculate execution time in seconds with high precision
//            double executionTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
//
//            Map<String, Object> res = response.getBody();
//
//            ExecutionResult result = new ExecutionResult();
//            result.setExecutionTime(executionTimeSeconds);
//
//            if (res != null) {
//                Map<String, Object> run = (Map<String, Object>) res.get("run");
//                if (run != null) {
//                    String stdout = (String) run.get("stdout");
//                    String stderr = (String) run.get("stderr");
//                    Integer exitCode = (Integer) run.get("code");
//
//                    result.setStdout(stdout != null ? stdout : "");
//                    result.setStderr(stderr != null ? stderr : "");
//                    result.setExitCode(exitCode != null ? exitCode : 0);
//
//                    // Determine status based on execution result
//                    if (executionTimeSeconds > TIME_LIMIT_SECONDS) {
//                        result.setStatus("Time Limit Exceeded");
//                    } else if (stderr != null && !stderr.trim().isEmpty()) {
//                        result.setStatus("Runtime Error");
//                    } else if (exitCode != null && exitCode != 0) {
//                        result.setStatus("Runtime Error");
//                    } else {
//                        result.setStatus("Success");
//                    }
//                } else {
//                    result.setStatus("System Error");
//                    result.setStderr("No run information returned from API");
//                }
//            } else {
//                result.setStatus("System Error");
//                result.setStderr("No response from API");
//            }
//
//            return result;
//
//        } catch (Exception e) {
//            ExecutionResult result = new ExecutionResult();
//            result.setStatus("System Error");
//            result.setStderr("Exception: " + e.getMessage());
//            result.setExecutionTime(0.0);
//            return result;
//        }
//    }
//
//    /**
//     * Inner class to hold execution results
//     */
//    private static class ExecutionResult {
//        private String status;
//        private String stdout;
//        private String stderr;
//        private int exitCode;
//        private double executionTime;
//
//        // Getters and setters
//        public String getStatus() { return status; }
//        public void setStatus(String status) { this.status = status; }
//
//        public String getStdout() { return stdout; }
//        public void setStdout(String stdout) { this.stdout = stdout; }
//
//        public String getStderr() { return stderr; }
//        public void setStderr(String stderr) { this.stderr = stderr; }
//
//        public int getExitCode() { return exitCode; }
//        public void setExitCode(int exitCode) { this.exitCode = exitCode; }
//
//        public double getExecutionTime() { return executionTime; }
//        public void setExecutionTime(double executionTime) { this.executionTime = executionTime; }
//    }
//
//    /**
//     * Map Judge0 language IDs to Piston language names
//     * You'll need to adjust this based on your specific language ID mapping
//     */
//    private String mapLanguageIdToPistonLanguage(Integer languageId) {
//        switch (languageId) {
//            case 71:
//                return "python";
//            case 62:
//                return "java";
//            case 63:
//                return "javascript";
//            case 54:
//                return "cpp";
//            case 50:
//                return "c";
//            case 51:
//                return "csharp";
//            case 70:
//                return "python2";
//            case 72:
//                return "ruby";
//            case 73:
//                return "rust";
//            case 74:
//                return "typescript";
//            case 75:
//                return "c";
//            case 76:
//                return "cpp";
//            case 77:
//                return "cobol";
//            case 78:
//                return "kotlin";
//            case 79:
//                return "objc";
//            case 80:
//                return "r";
//            case 81:
//                return "scala";
//            case 82:
//                return "sql";
//            case 83:
//                return "swift";
//            case 84:
//                return "vb";
//            default:
//                return "python";
//        }
//    }
//}