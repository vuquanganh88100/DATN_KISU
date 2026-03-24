package com.elearning.elearning_support.dtos.judge0;

import com.elearning.elearning_support.constants.JudgeStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JudgeResultDto {
    private String stdout;
    private String stderr;
    private String compile_output;
    private String time;
    private Integer memory;
    private JudgeStatusDto status;
    private String message;
}
