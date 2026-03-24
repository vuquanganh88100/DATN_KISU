package com.elearning.elearning_support.services.judge0.impl;

import com.elearning.elearning_support.constants.JudgeStatusConstants;
import com.elearning.elearning_support.dtos.judge0.JudgeRequestDto;
import com.elearning.elearning_support.dtos.judge0.JudgeResultDto;
import com.elearning.elearning_support.dtos.judge0.SubmissionDto;
import com.elearning.elearning_support.dtos.judge0.SubmissionResponseDto;
import com.elearning.elearning_support.entities.contest.ProblemContest;
import com.elearning.elearning_support.entities.contest.TestCase;
import com.elearning.elearning_support.entities.contest.Verdict;
import com.elearning.elearning_support.entities.submitContest.JudgeResult;
import com.elearning.elearning_support.entities.submitContest.SubmissionContest;
import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.repositories.contest.ProblemContestRepository;
import com.elearning.elearning_support.repositories.contest.TestCaseRepository;
import com.elearning.elearning_support.repositories.submitContest.JudgeResultRepository;
import com.elearning.elearning_support.repositories.submitContest.SubmissionRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.services.judge0.JudgeService;
import com.elearning.elearning_support.services.judge0.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private JudgeService judgeService;
    @Autowired
    private TestCaseRepository testCaseRepository;
    @Autowired
    private ProblemContestRepository problemContestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private JudgeResultRepository judgeResultRepository;
    @Autowired
    private Judge0StatusMapper judge0StatusMapper;

    @Override
    public SubmissionResponseDto judgeSubmission(SubmissionDto submissionDto) {

        long problemId = submissionDto.getProblemId();

        List<TestCase> testCases =
                testCaseRepository.findByProblemContest_Id(problemId);

        ProblemContest problem = problemContestRepository.findById(problemId)
                .orElseThrow();

        User student = userRepository.findById(submissionDto.getStudentId())
                .orElseThrow();

        SubmissionContest submission = new SubmissionContest();
        submission.setStudentId(student);
        submission.setProblemContest(problem);
        submission.setLanguageId(submissionDto.getLanguage_id());
        submission.setSourceCode(submissionDto.getSource_code());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setVerdict("PROCESSING");

        submission = submissionRepository.save(submission);

        Verdict finalVerdict = Verdict.ACCEPTED;
        int passedCount = 0;
        double maxExecutionTime = 0.0;
        int maxMemory = 0;

        // ===== JUDGE TESTCASES =====
        for (TestCase tc : testCases) {

            JudgeRequestDto req = new JudgeRequestDto(
                    submission.getSourceCode(),
                    submission.getLanguageId(),
                    tc.getInput(),
                    tc.getExpectedOutput()
            );

            String token = judgeService.submit(req);
            JudgeResultDto result = judgeService.pollResult(token);

            String actual = result.getStdout() != null
                    ? result.getStdout().trim()
                    : "";
            String expected = tc.getExpectedOutput() != null
                    ? tc.getExpectedOutput().trim()
                    : "";

            boolean outputMatched = actual.equals(expected);

            Verdict testcaseVerdict =
                    judge0StatusMapper.mapTestcaseVerdict(
                            result.getStatus().getId(),
                            outputMatched
                    );

            // JUDGE_RESULT
            JudgeResult jr = new JudgeResult();
            jr.setSubmissionContest(submission);
            jr.setTestCase(tc);
            jr.setStatus(testcaseVerdict.name());
            jr.setActualOutput(result.getStdout());
            jr.setExecutionTime(
                    result.getTime() != null
                            ? Double.parseDouble(result.getTime())
                            : null
            );
            judgeResultRepository.save(jr);

            if (testcaseVerdict == Verdict.ACCEPTED) {
                passedCount++;
            } else {
                finalVerdict = testcaseVerdict;
            }

            // MAX TIME
            if (result.getTime() != null) {
                maxExecutionTime = Math.max(
                        maxExecutionTime,
                        Double.parseDouble(result.getTime())
                );
            }

            // MAX MEMORY
            if (result.getMemory() != null) {
                maxMemory = Math.max(maxMemory, result.getMemory());
            }

            if (judge0StatusMapper.isTerminal(testcaseVerdict)) {
                break;
            }
        }

        submission.setVerdict(finalVerdict.name());

        if (finalVerdict == Verdict.ACCEPTED) {
            submission.setRuntime(maxExecutionTime);
            submission.setMemory(maxMemory);
        } else {
            submission.setRuntime(null);
            submission.setMemory(null);
        }

        submissionRepository.save(submission);

        return new SubmissionResponseDto(
                submission.getId(),
                finalVerdict.name(),
                submission.getRuntime(),
                submission.getMemory(),
                passedCount,
                testCases.size(),
                null,
                submission.getSubmittedAt(),
                null,
                null,
                submission.getProblemContest().getId(),
                null,
                submission.getSourceCode()
        );
    }

    @Override
    public List<SubmissionResponseDto> getSubmissionsByStudentId(Long studentId) {

        List<SubmissionContest> submissions =
                submissionRepository.findByStudentId_Id(studentId);

        List<SubmissionResponseDto> result = new ArrayList<>();

        for (SubmissionContest submission : submissions) {

            int totalTestcases = 0;
            int passedTestcases = 0;

            if (submission.getJudgeResults() != null) {
                totalTestcases = submission.getJudgeResults().size();

                for (JudgeResult jr : submission.getJudgeResults()) {
                    if ("Pass".equalsIgnoreCase(jr.getStatus())) {
                        passedTestcases++;
                    }
                }
            }

            SubmissionResponseDto dto = new SubmissionResponseDto(
                    submission.getId(),
                    submission.getVerdict(),
                    submission.getRuntime(),
                    submission.getMemory(),
                    passedTestcases,
                    totalTestcases,
                    null,
                    submission.getSubmittedAt(),
                    submission.getProblemContest().getTopicContest().getName(),
                    submission.getProblemContest().getTitle(),
                    submission.getProblemContest().getId(),
                    null,
                    submission.getSourceCode()

            );

            result.add(dto);
        }

        return result;
    }

    @Override
    public List<SubmissionResponseDto> getSubmissionsByProblemId(Long problemId) {

        List<SubmissionContest> submissions =
                submissionRepository
                        .findByProblemContest_Id(problemId);

        List<SubmissionResponseDto> result = new ArrayList<>();

        for (SubmissionContest submission : submissions) {
            int totalTestcases = 0;
            int passedTestcases = 0;

            if (submission.getJudgeResults() != null) {
                totalTestcases = submission.getJudgeResults().size();

                for (JudgeResult jr : submission.getJudgeResults()) {
                    if ("Pass".equalsIgnoreCase(jr.getStatus())) {
                        passedTestcases++;
                    }
                }
            }
            String studentName=userRepository.findById(submission.getStudentId().getId()).get().getFullName();

            SubmissionResponseDto dto = new SubmissionResponseDto(
                    submission.getId(),
                    submission.getVerdict(),
                    submission.getRuntime(),
                    submission.getMemory(),
                    passedTestcases,
                    totalTestcases,
                    null,
                    submission.getSubmittedAt(),
                    submission.getProblemContest().getTopicContest().getName(),
                    submission.getProblemContest().getTitle(),
                    submission.getProblemContest().getId(),
                    studentName,
                    submission.getSourceCode()
            );

            result.add(dto);
        }

        result.sort((a, b) ->
                b.getSubmittedAt().compareTo(a.getSubmittedAt())
        );

        return result;
    }

    @Override
    public SubmissionResponseDto getSourceCodeBySubmissionId(Long submissionId) {
        SubmissionContest submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new RuntimeException("Submission not found"));

        return new SubmissionResponseDto(
                submission.getId(),
                submission.getVerdict(),
                submission.getRuntime(),
                submission.getMemory(),
                null,
                null,
                getLanguageName(submission.getLanguageId()),
                submission.getSubmittedAt(),
                submission.getProblemContest().getTopicContest().getName(),
                submission.getProblemContest().getTitle(),
                submission.getProblemContest().getId(),
                null,
                submission.getSourceCode()
        );

    }
    private String getLanguageName(Integer id) {
        if (id == null) return "Unknown";

        switch (id) {
            case 49: return "C (GCC)";
            case 50: return "C++ (GCC)";
            case 51: return "C# (.NET)";
            case 62: return "Java (OpenJDK)";
            case 63: return "JavaScript (Node.js)";
            case 71: return "Python";
            case 72: return "Rust";
            case 73: return "Go";
            case 64: return "Bash";
            default: return "Unknown";
        }
    }
    public List<JudgeResultDto> runCode(SubmissionDto submissionDto) {

        long problemId = submissionDto.getProblemId();

        // chỉ lấy 2 test public
        List<TestCase> testCases =
                testCaseRepository
                        .findTop2ByProblemContest_IdAndIsPublicTrue(problemId);

        List<JudgeResultDto> results = new ArrayList<>();

        for (TestCase tc : testCases) {

            JudgeRequestDto req = new JudgeRequestDto(
                    submissionDto.getSource_code(),
                    submissionDto.getLanguage_id(),
                    tc.getInput(),
                    tc.getExpectedOutput()
            );

            String token = judgeService.submit(req);
            JudgeResultDto result = judgeService.pollResult(token);

            results.add(result);
        }

        return results;
    }

}

