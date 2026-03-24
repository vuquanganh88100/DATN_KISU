package com.elearning.elearning_support.dtos.judge0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeRequestDto {
    private String source_code;
    private Integer language_id;
    private String stdin;
    private String expected_output;
}
