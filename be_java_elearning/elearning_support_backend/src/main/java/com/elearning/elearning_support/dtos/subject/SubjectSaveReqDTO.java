package com.elearning.elearning_support.dtos.subject;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectSaveReqDTO {

    @NotNull
    @Schema(description = "Tiêu đề môn học")
    String title;

    @NotNull
    @Max(value = 10)
    @Min(value = 1)
    @Schema(description = "Mã môn học")
    String code;

    @Schema(description = "Mô tả môn học")
    String description;

    @Schema(description = "Số tín chỉ của môn học")
    Integer credit;

    @Schema(description = "Id Khoa/ Viện / Đơn vị phụ trách")
    Long departmentId;

}
