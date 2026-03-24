package com.elearning.elearning_support.dtos.contest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TestCaseDto {
    private String input;
    private String expectedOutput;
    private boolean isPublic;
}
