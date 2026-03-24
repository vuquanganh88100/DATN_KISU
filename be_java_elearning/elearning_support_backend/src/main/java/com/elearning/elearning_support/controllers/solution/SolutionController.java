package com.elearning.elearning_support.controllers.solution;

import com.elearning.elearning_support.dtos.solution.*;
import com.elearning.elearning_support.services.solution.SolutionCommentService;
import com.elearning.elearning_support.services.solution.SolutionService;
import com.elearning.elearning_support.services.solution.SolutionVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problem/solution")
@RequiredArgsConstructor
public class SolutionController {

   @Autowired
   SolutionService solutionService;
    @Autowired
    SolutionCommentService solutionCommentService;
    @Autowired
    SolutionVoteService solutionVoteService;

    // ================= SOLUTION =================

    @PostMapping
    public ResponseEntity<?> createSolution(
            @RequestBody SolutionCreateReqDTO dto) {

        SolutionResDTO res = solutionService.createSolution(dto);

        if (res == null) {
            throw new IllegalStateException(
                    "CREATE_SOLUTION_FAILED: user chưa đăng nhập hoặc problem không tồn tại"
            );
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/problem/{problemId}")
    public ResponseEntity<?> getSolutionsByProblem(
            @PathVariable Long problemId) {

        return ResponseEntity.ok(
                solutionService.getSolutionsByProblem(problemId)
        );
    }

    // ================= COMMENT =================

    @PostMapping("/comment")
    public ResponseEntity<?> createComment(
            @RequestBody SolutionCommentCreateReqDTO dto) {

        SolutionCommentResDTO res =
                solutionCommentService.createComment(dto);

        if (res == null) {
            throw new IllegalStateException(
                    "CREATE_COMMENT_FAILED: user chưa đăng nhập hoặc solution không tồn tại"
            );
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{solutionId}/comments")
    public ResponseEntity<?> getCommentsBySolution(
            @PathVariable Long solutionId) {

        return ResponseEntity.ok(
                solutionCommentService.getCommentsBySolution(solutionId)
        );
    }

    // ================= VOTE =================

    @PostMapping("/vote")
    public ResponseEntity<?> voteSolution(
            @RequestBody SolutionVoteReqDTO dto) {

        boolean success = solutionVoteService.vote(dto);

        if (!success) {
            throw new IllegalStateException(
                    "VOTE_FAILED: user chưa đăng nhập hoặc solution không tồn tại"
            );
        }

        return ResponseEntity.ok().build();
    }
}