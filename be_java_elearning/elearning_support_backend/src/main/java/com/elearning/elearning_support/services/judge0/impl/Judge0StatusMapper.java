package com.elearning.elearning_support.services.judge0.impl;

import com.elearning.elearning_support.constants.JudgeStatusConstants;
import com.elearning.elearning_support.entities.contest.Verdict;
import org.springframework.stereotype.Component;

@Component
public class Judge0StatusMapper {

    public Verdict mapTestcaseVerdict(int judge0StatusId, boolean outputMatched) {

        switch (judge0StatusId) {

            case JudgeStatusConstants.COMPILE_ERROR:
                return Verdict.COMPILATION_ERROR;

            case JudgeStatusConstants.RUNTIME_ERROR:
                return Verdict.RUNTIME_ERROR;

            case JudgeStatusConstants.TIME_LIMIT:
                return Verdict.TIME_LIMIT_EXCEEDED;

            case JudgeStatusConstants.WRONG_ANSWER:
                return Verdict.WRONG_ANSWER;

            case JudgeStatusConstants.ACCEPTED:
                return outputMatched
                        ? Verdict.ACCEPTED
                        : Verdict.WRONG_ANSWER;

            default:
                return Verdict.RUNTIME_ERROR;
        }
    }

    public boolean isTerminal(Verdict verdict) {
        return verdict == Verdict.COMPILATION_ERROR
                || verdict == Verdict.RUNTIME_ERROR
                || verdict == Verdict.TIME_LIMIT_EXCEEDED;
    }
}
