package com.elearning.elearning_support.controllers.contest;

import com.elearning.elearning_support.dtos.contest.TopicContestDto;
import com.elearning.elearning_support.services.contest.TopicContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topic-contest")
@Tag(name = "APIs Topic Contest")
@RequiredArgsConstructor
public class TopicContestController {

    private final TopicContestService topicContestService;

    @PostMapping
    @Operation(summary = "Tạo mới topic contest")
    public ResponseEntity<TopicContestDto> createTopicContest(@RequestBody TopicContestDto requestDto) {
        TopicContestDto createdTopicContest = topicContestService.createTopicContest(requestDto);
        return new ResponseEntity<>(createdTopicContest, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật topic contest")
    public ResponseEntity<TopicContestDto> updateTopicContest(
            @PathVariable Long id,
            @RequestBody TopicContestDto requestDto) {
        TopicContestDto updatedTopicContest = topicContestService.updateTopicContest(id, requestDto);
        return ResponseEntity.ok(updatedTopicContest);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả topic contest")
    public ResponseEntity<List<TopicContestDto>> getAllTopicContests() {
        List<TopicContestDto> topicContests = topicContestService.getAllTopicContests();
        return ResponseEntity.ok(topicContests);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết topic contest theo ID")
    public ResponseEntity<TopicContestDto> getTopicContestById(@PathVariable Long id) {
        TopicContestDto topicContest = topicContestService.getTopicContestById(id);
        return ResponseEntity.ok(topicContest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa topic contest")
    public ResponseEntity<Void> deleteTopicContest(@PathVariable Long id) {
        topicContestService.deleteTopicContest(id);
        return ResponseEntity.noContent().build();
    }
}
