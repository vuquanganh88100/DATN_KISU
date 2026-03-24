package com.elearning.elearning_support.services.contest.impl;

import com.elearning.elearning_support.dtos.contest.ProblemContestDto;
import com.elearning.elearning_support.dtos.contest.ProblemStatisticProjection;
import com.elearning.elearning_support.dtos.contest.TestCaseDto;
import com.elearning.elearning_support.dtos.judge0.StudentSubmissionProjection;
import com.elearning.elearning_support.entities.contest.ProblemContest;
import com.elearning.elearning_support.entities.contest.TestCase;
import com.elearning.elearning_support.entities.contest.TopicContest;
import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.repositories.contest.ProblemContestRepository;
import com.elearning.elearning_support.repositories.contest.TestCaseRepository;
import com.elearning.elearning_support.repositories.contest.TopicContestRepository;
import com.elearning.elearning_support.repositories.submitContest.SubmissionRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.services.contest.ProblemContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class ProblemContestServiceImpl implements ProblemContestService {

    private final ProblemContestRepository problemRepository;
    private final UserRepository userRepository;
    private final TopicContestRepository topicRepository;
    private final TestCaseRepository testCaseRepository;
    @Autowired
    private TopicContestRepository topicContestRepository;
    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public ProblemContestDto createProblem(ProblemContestDto problemDto) {
        ProblemContest problem = new ProblemContest();
        problem.setTitle(problemDto.getTitle());
        problem.setDescription(problemDto.getDescription());
        problem.setInputFormat(problemDto.getInputFormat());
        problem.setOutputFormat(problemDto.getOutputFormat());
        problem.setConstraints(problemDto.getConstraints());
        problem.setLevel(problemDto.getLevel());

        User teacher = userRepository.findById(problemDto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        TopicContest topic = topicRepository.findById(problemDto.getTopicContestId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        problem.setTeacherId(teacher);
        problem.setTopicContest(topic);

        // Lưu problem trước để lấy id
        ProblemContest savedProblem = problemRepository.save(problem);

        // Xử lý test case nếu có
        List<TestCaseDto> testCaseDtos = problemDto.getTestCases();
        if (testCaseDtos != null && !testCaseDtos.isEmpty()) {
            // B1: Xáo trộn danh sách test case
            Collections.shuffle(testCaseDtos);

            // B2: Gán isPublic = true cho 2 test case đầu tiên (nếu có đủ)
            for (int i = 0; i < testCaseDtos.size(); i++) {
                testCaseDtos.get(i).setPublic(i < 2); // true cho 2 đầu, false cho còn lại
            }

            // B3: Ánh xạ sang entity và gán problem
            List<TestCase> testCases = testCaseDtos.stream().map(tcDto -> {
                TestCase testCase = new TestCase();
                testCase.setInput(tcDto.getInput());
                testCase.setExpectedOutput(tcDto.getExpectedOutput());
                testCase.setPublic(tcDto.isPublic()); // đã set ở bước trên
                testCase.setProblemContest(savedProblem); // thiết lập khóa ngoại
                return testCase;
            }).collect(Collectors.toList());

            testCaseRepository.saveAll(testCases);
        }

        return convertToDto(savedProblem);
    }

    @Override
    public ProblemContestDto updateProblem(Long id, ProblemContestDto problemDto) {
        ProblemContest problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        problem.setTitle(problemDto.getTitle());
        problem.setDescription(problemDto.getDescription());
        problem.setInputFormat(problemDto.getInputFormat());
        problem.setOutputFormat(problemDto.getOutputFormat());
        problem.setConstraints(problemDto.getConstraints());

        ProblemContest updatedProblem = problemRepository.save(problem);
        return convertToDto(updatedProblem);
    }

    @Override
    public ProblemContestDto getProblemById(Long id) {
        ProblemContest problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        ProblemContestDto dto = convertToDto(problem);
        if (dto.getTestCases() != null) {
            List<TestCaseDto> publicTestCases = dto.getTestCases().stream()
                    .filter(TestCaseDto::isPublic)
                    .collect(Collectors.toList());
            dto.setTestCases(publicTestCases);
        }

        return dto;
    }

    @Override
    public Page<ProblemContestDto> getAllProblems(Pageable pageable, long studentId) {

        Map<Long, ProblemStatisticProjection> statMap =
                problemRepository.getProblemStatistics()
                        .stream()
                        .collect(Collectors.toMap(
                                ProblemStatisticProjection::getProblemId,
                                s -> s
                        ));

        // ===== Submission của student =====
        Map<Long, List<StudentSubmissionProjection>> studentMap =
                submissionRepository.getStudentSubmissions(studentId)
                        .stream()
                        .collect(Collectors.groupingBy(
                                StudentSubmissionProjection::getProblemId
                        ));

        return problemRepository.findAll(pageable)
                .map(problem -> {

                    ProblemContestDto dto =
                            convertToDtoWithoutTestCases(problem);

                    // ===== AC RATE =====
                    ProblemStatisticProjection stat =
                            statMap.get(problem.getId());

                    long total = stat == null || stat.getTotalSubmissions() == null
                            ? 0 : stat.getTotalSubmissions();

                    long totalAC = stat == null || stat.getTotalAC() == null
                            ? 0 : stat.getTotalAC();

                    double rate = total == 0 ? 0 : totalAC * 100.0 / total;

                    dto.setTotalSubmissions(total);
                    dto.setTotalAC(totalAC);
                    dto.setAcRate(Math.round(rate * 100.0) / 100.0);

                    // ===== STATUS =====
                    List<StudentSubmissionProjection> submissions =
                            studentMap.get(problem.getId());

                    if (submissions == null || submissions.isEmpty()) {
                        dto.setStatus("UNATTEMPTED");
                    } else if (submissions.stream()
                            .anyMatch(s -> "ACCEPTED".equals(s.getVerdict()))) {
                        dto.setStatus("SOLVED");
                    } else {
                        dto.setStatus("FAILED");
                    }

                    return dto;
                });
    }

    @Override
    public List<ProblemContestDto> getProblemsByTopicId(Long topicId) {
        Map<Long, ProblemStatisticProjection> statMap =
                problemRepository.getProblemStatistics()
                        .stream()
                        .collect(Collectors.toMap(
                                ProblemStatisticProjection::getProblemId,
                                s -> s
                        ));

        return problemRepository.findByTopicContestId(topicId)
                .stream()
                .map(problem -> {

                    ProblemContestDto dto =
                            convertToDtoWithoutTestCases(problem);

                    ProblemStatisticProjection stat =
                            statMap.get(problem.getId());

                    long total = stat == null || stat.getTotalSubmissions() == null
                            ? 0 : stat.getTotalSubmissions();

                    long ac = stat == null || stat.getTotalAC() == null
                            ? 0 : stat.getTotalAC();

                    dto.setTotalSubmissions(total);
                    dto.setTotalAC(ac);
                    double rate = total == 0 ? 0.0 : (ac * 100.0 / total);
                    dto.setAcRate(Math.round(rate * 100.0) / 100.0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProblem(Long id) {
        if (!problemRepository.existsById(id)) {
            throw new RuntimeException("Problem not found");
        }
        problemRepository.deleteById(id);
    }

    private ProblemContestDto convertToDto(ProblemContest problem) {
        ProblemContestDto dto = new ProblemContestDto();
        TopicContest topicContest=topicContestRepository.findById(problem.getTopicContest().getId()).get();
        dto.setTopicContestName(topicContest.getName());
        dto.setId(problem.getId());
        dto.setTitle(problem.getTitle());
        dto.setDescription(problem.getDescription());
        dto.setInputFormat(problem.getInputFormat());
        dto.setOutputFormat(problem.getOutputFormat());
        dto.setConstraints(problem.getConstraints());
        dto.setTeacherId(problem.getTeacherId().getId());
        dto.setTopicContestId(problem.getTopicContest().getId());
        dto.setLevel(problem.getLevel());

        // statistic submission


        //  danh sách test case nếu cần
        List<TestCaseDto> testCaseDtos = problem.getTestCases() != null
                ? problem.getTestCases().stream().map(tc -> {
            TestCaseDto tcDto = new TestCaseDto();
            tcDto.setInput(tc.getInput());
            tcDto.setExpectedOutput(tc.getExpectedOutput());
            tcDto.setPublic(tc.isPublic());
            return tcDto;
        }).collect(Collectors.toList())
                : new ArrayList<>();

        dto.setTestCases(testCaseDtos);
        return dto;
    }
    public ProblemContestDto convertToDtoWithoutTestCases(ProblemContest entity) {
        ProblemContestDto dto = convertToDto(entity);
        dto.setTestCases(null);
        return dto;
    }

}
