package com.elearning.elearning_support.dtos.contest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseGenByJudgeDto extends TestCaseDto{
    private String sourceCode;
    private int languageId;
}
